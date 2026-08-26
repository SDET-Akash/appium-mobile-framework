# Architecture

> Status: the Android Driver Layer, the Configuration Layer, and
> `BaseTest` are implemented and verified against a real Appium session
> (see [Driver Layer — Implemented](#driver-layer--implemented) and
> [Configuration Layer — Implemented](#configuration-layer--implemented)
> below). `BaseTest` centralizes the same driver-lifecycle flow
> `DriverSmokeTest` performs manually, but no test class extends it yet —
> that refactor is the next step. Everything else described in this
> document beyond those sections — BasePage, Pages, reporting, listeners
> — is still the target design, not code that currently exists.

## Goals

- Single framework, two mobile platforms (Android now, iOS added later
  without restructuring).
- Every cross-cutting concern (driver lifecycle, config, reporting,
  logging, retries) isolated behind its own package so it can be
  implemented and tested independently.
- No layer reaches "sideways" into another layer's internals — only
  through the interfaces/managers described below.

## Layers

```
        Tests
          │
          ▼
       BaseTest
          │
          ▼
        Pages
          │
          ▼
       BasePage
          │
          ▼
     DriverManager
          │
          ▼
     DriverFactory
          │
          ▼
 AndroidDriverFactory / IOSDriverFactory
```

```
┌───────────────────────────────────────────────────────────┐
│ Tests (src/test/java/.../tests)                            │
│   - JUnit-free, TestNG-annotated test classes only.        │
│   - Depend on: BaseTest, pages/, models/                   │
├───────────────────────────────────────────────────────────┤
│ BaseTest (src/main/java/.../base) — IMPLEMENTED             │
│   - Method-level driver setup & teardown (@BeforeMethod/     │
│     @AfterMethod). No test class extends it yet.             │
│   - Depend on: config/, driver/ (via DriverManager)          │
├───────────────────────────────────────────────────────────┤
│ Pages (pages/android, pages/ios, pages/common)              │
│   - Page Object Model. common/ holds platform-agnostic     │
│     contracts; android/ and ios/ hold implementations.     │
│   - Every Page Object extends BasePage.                    │
│   - Depend on: BasePage only — never DriverManager directly│
├───────────────────────────────────────────────────────────┤
│ BasePage (src/main/java/.../base)                            │
│   - Owns ALL low-level driver interaction (element lookup,   │
│     waits, gestures) on behalf of Page Objects.               │
│   - Depend on: driver/ (via DriverManager), utils/            │
├───────────────────────────────────────────────────────────┤
│ Driver (driver/) — IMPLEMENTED                               │
│   - DriverManager (lifecycle/thread-local ownership),        │
│     delegating creation to DriverFactory (contract).         │
│   - Depend on: config/ (capabilities, once it exists),        │
│     exceptions/                                               │
├───────────────────────────────────────────────────────────┤
│ DriverFactory implementations (driver/) — Android IMPLEMENTED│
│   - AndroidDriverFactory (implemented) /                     │
│     IOSDriverFactory (stub only, throws Unsupported-          │
│     OperationException).                                     │
├───────────────────────────────────────────────────────────┤
│ Config (config/) — NOT YET IMPLEMENTED (empty declarations)  │
│   - ConfigReader (source) → ConfigManager (typed access) →   │
│     CapabilityBuilder (Appium options) → Environment (enum).  │
├───────────────────────────────────────────────────────────┤
│ Cross-cutting: listeners/, reports/, utils/, exceptions/,     │
│ constants/, enums/, models/, builders/, factory/               │
└───────────────────────────────────────────────────────────┘
```

Dependency direction flows top-to-bottom only: Tests depend on BaseTest,
BaseTest depends on Pages/driver/config, Pages depend only on BasePage,
BasePage depends on DriverManager, DriverManager depends on
DriverFactory, DriverFactory is implemented per platform. Nothing below
depends on anything above it.

**Page Objects must never access `DriverManager` directly.** All driver
interaction is routed through `BasePage` — this is the one rule that
keeps the Pages layer decoupled from driver lifecycle concerns and is
what makes swapping/mocking the driver layer possible without touching
any page object.

## Driver Layer — Implemented

The Android slice of the driver layer is implemented and has created a
real Appium session against `emulator-5554` (see
[ExecutionFlow.md](ExecutionFlow.md) for the verified run):

```
     DriverFactory  (contract)
           │  implemented by
           ▼
 AndroidDriverFactory
           │  builds UiAutomator2Options, then constructs
           ▼
     AndroidDriver
           │  stored/retrieved via
           ▼
     DriverManager
```

- **`DriverFactory`** — the platform-neutral contract:
  `createDriver(URL appiumServerUrl, Map<String, Object> capabilities)`.
- **`AndroidDriverFactory`** — fixes only `platformName=Android` and
  `automationName=UiAutomator2` (that's what makes it *this* factory);
  every other capability (`deviceName`, `app`, etc.) is applied from the
  caller-supplied map, never hardcoded. Wraps creation failures in
  `DriverInitializationException` with the original cause preserved.
- **`DriverManager`** — holds the active `AndroidDriver` per thread via a
  static `ThreadLocal<AndroidDriver>` (no public static driver field).
  `getDriver()` fails clearly if called before `setDriver()`.
  `removeDriver()` quits the session and clears the thread-local
  reference inside a `try`/`finally`, so the reference is always removed
  even if `driver.quit()` throws.
- **`IOSDriverFactory`** — exists only as a stub implementing
  `DriverFactory` (throws `UnsupportedOperationException`); no iOS logic
  has been implemented.

## Configuration Layer — Implemented

```
     Environment (enum: QA, STAG, PROD)
           │  selects
           ▼
     ConfigReader
           │  loads config/{qa,stag,prod}.properties from the classpath
           ▼
     ConfigManager
           │  exposes typed accessors, then
           ▼
     CapabilityBuilder
           │  assembles
           ▼
 Map<String, Object> capabilities  ──►  AndroidDriverFactory.createDriver(...)
```

Its job is to **supply data into the Driver Layer**, not to sit as
another step after it — `AndroidDriverFactory.createDriver(...)` already
accepts a server URL and a capability map from its caller; the
Configuration Layer's whole purpose is to be that caller, resolving
per-environment values instead of a test hardcoding them.

- **`Environment`** — enum of `QA`, `STAG`, `PROD`; selects which
  properties file `ConfigReader` loads.
- **`ConfigReader`** — loads `config/{qa,stag,prod}.properties` from the
  classpath for a given `Environment` and exposes raw `get(key)` lookups.
  Throws `ConfigurationException` if the file or key is missing.
- **`ConfigManager`** — wraps a `ConfigReader` with the typed accessors
  the rest of the framework uses (`getPlatform()`, `getAutomationName()`,
  `getDeviceName()`, `getAppiumServerUrl()`, `getGrantPermission()`,
  `getAppInstall()`, `getAppPath()`), so callers never touch property
  keys directly. `getAppPath()` resolves the configured classpath-relative
  APK resource (e.g. `apps/qa/qa.apk`) to an absolute filesystem path, or
  returns blank untouched if the environment's app path hasn't been
  supplied yet.
- **`CapabilityBuilder`** — turns a `ConfigManager` into the
  `Map<String, Object>` capabilities `AndroidDriverFactory.createDriver`
  expects, validating that `platform`, `automationName`, and `deviceName`
  are non-blank before returning.

Supplies, per environment, from
`src/test/resources/config/{qa,stag,prod}.properties` — see
[FolderStructure.md](FolderStructure.md):

- Appium server URL
- device name
- APK path
- platform
- automation name
- auto-grant-permissions / enforce-app-install flags

## BaseTest — Implemented

```
     @BeforeMethod setUp()
           │  Environment.QA → ConfigManager → CapabilityBuilder.build()
           ▼
     AndroidDriverFactory.createDriver(url, capabilities)
           │
           ▼
     DriverManager.setDriver(driver)

     (test body — via protected getDriver())

     @AfterMethod(alwaysRun = true) tearDown()
           │
           ▼
     DriverManager.removeDriver()
```

`BaseTest` centralizes only the driver lifecycle described above; it
does not yet contain login logic, page objects, or RBAC. `Environment`
is fixed to `QA` for now — a proper environment-selection mechanism is
still to be introduced. No test class extends `BaseTest` yet;
`DriverSmokeTest` still performs the same setup manually as its own
verification of the Configuration + Driver Layers. Refactoring
`DriverSmokeTest` to extend `BaseTest` is the next step.

## Design Principles Applied

- **Single Responsibility** — each class in `driver/` and `config/` has
  exactly one reason to change (e.g. `ConfigReader` only reads raw
  values; `CapabilityBuilder` only assembles Appium options).
- **Open/Closed** — adding a platform means adding a new
  `*DriverFactory` implementation, not modifying `DriverManager` or
  existing factories.
- **Dependency Inversion** — `DriverManager` depends on the
  `DriverFactory` interface, never on `AndroidDriverFactory` or
  `IOSDriverFactory` directly; Page Objects depend on `BasePage`, never
  on `DriverManager` or the interface below it.
- **Interface Segregation** — `pages/common` will define narrow,
  per-screen contracts rather than one large shared base.

## Platform Extensibility (Android → iOS)

Android is the only platform exercised in v1. The `ios/` folders under
`driver/` (as `IOSDriverFactory`) and `pages/ios` already exist so iOS
support is additive later: new factory + new page implementations
against the same `pages/common` contracts, no changes to `DriverManager`,
`ConfigManager`, `BaseTest`, or existing Android code.

## Reporting & Logging

Two independent, parallel reporting paths — `reports/ExtentReportManager`
(standalone HTML dashboard) and `reports/AllureManager` (step/attachment
data for the `allure-testng` adapter) — both fed from the same
`listeners/TestListener` hook so test outcomes are captured once and
distributed to both reports. All layers log through Log4j2
(`log4j2.xml`), including third-party library logs bridged via
`log4j-slf4j2-impl`.

## Framework Rules

Non-negotiable rules for all future implementation work in this
framework:

- Never use `Thread.sleep()`.
- Always use `WaitUtils` (`utils/wait`) for synchronization.
- Every page extends `BasePage`.
- Never instantiate `AndroidDriver`/`IOSDriver` directly.
- Always obtain the driver through `DriverManager`.
- Never hardcode APK paths.
- Never hardcode environment values.
- Never hardcode credentials.

> `DriverSmokeTest` now resolves the Appium server URL, device name, and
> APK path through the Configuration Layer (`ConfigManager` /
> `CapabilityBuilder`) rather than hardcoding them. It remains a
> throwaway verification test — not a pattern to follow — because it
> performs driver setup/teardown manually instead of extending
> `BaseTest`. See [ExecutionFlow.md](ExecutionFlow.md).

## Locator Strategy

Locators must be chosen in the following priority order, from most to
least preferred:

```
Accessibility ID
      │
      ▼
  Resource ID
      │
      ▼
AndroidUIAutomator
      │
      ▼
    XPath
```

- **Accessibility ID** — most stable across app versions and platforms;
  preferred whenever the app exposes one.
- **Resource ID** — Android's `resource-id`; stable within a given app
  build, the default choice when accessibility IDs aren't available.
- **AndroidUIAutomator** — used for locators requiring UiAutomator's
  richer selector syntax (e.g. matching by text/class combinations) when
  the above aren't sufficient.
- **XPath** — last resort only. Slowest and most brittle to UI changes;
  used only when no other strategy can express the required locator.
