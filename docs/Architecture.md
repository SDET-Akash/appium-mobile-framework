# Architecture

> Status (updated 2026-08-27): the Configuration Layer, the Android
> Driver Layer, `BaseTest`, `BasePage`, and the Android Page Object
> layer (`LoginPage`, `DashboardPage`) are all implemented and
> verified — three test classes (`DriverSmokeTest`, `LoginTest`,
> `DashboardTest`) currently pass against a real Appium/Android
> session (see [ExecutionFlow.md](ExecutionFlow.md)). `TestListener`
> is implemented and registered. Reporting (`AllureManager`,
> `ExtentReportManager`), `RetryAnalyzer`, and `AnnotationTransformer`
> remain scaffolding with no logic. iOS support does not exist beyond
> the `IOSDriverFactory` stub and empty `pages/ios` package.

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
│ Tests (src/test/java/.../tests) — IMPLEMENTED (Android)     │
│   - TestNG-annotated test classes only.                    │
│   - Depend on: BaseTest, pages/, flows/, config/            │
├───────────────────────────────────────────────────────────┤
│ BaseTest (src/main/java/.../base) — IMPLEMENTED             │
│   - Method-level driver setup & teardown (@BeforeMethod/     │
│     @AfterMethod). Every current test class extends it.      │
│   - Depend on: config/, driver/ (via DriverManager)          │
├───────────────────────────────────────────────────────────┤
│ Pages (pages/android) — IMPLEMENTED; pages/ios, pages/common │
│ remain empty                                                 │
│   - Page Object Model. android/ holds LoginPage/             │
│     DashboardPage; ios/ and common/ are reserved for later.  │
│   - Every Page Object extends BasePage.                      │
│   - Depend on: BasePage only — never DriverManager directly  │
├───────────────────────────────────────────────────────────┤
│ BasePage (src/main/java/.../base) — IMPLEMENTED              │
│   - Owns low-level driver interaction (element click/text     │
│     entry/visibility) via PageFactory + WebDriverWait.        │
│     Waiting is inline in BasePage — there is no separate      │
│     WaitUtils/utils/wait class.                               │
│   - Depend on: driver/ (via AndroidDriver passed by the page) │
├───────────────────────────────────────────────────────────┤
│ Driver (driver/) — IMPLEMENTED                               │
│   - DriverManager (lifecycle/thread-local ownership),        │
│     delegating creation to DriverFactory (contract).         │
│   - Depend on: exceptions/                                   │
├───────────────────────────────────────────────────────────┤
│ DriverFactory implementations (driver/) — Android IMPLEMENTED│
│   - AndroidDriverFactory (implemented) /                     │
│     IOSDriverFactory (stub only, throws Unsupported-          │
│     OperationException).                                     │
├───────────────────────────────────────────────────────────┤
│ Config (config/) — IMPLEMENTED                                │
│   - ConfigReader (source) → ConfigManager (typed access) →   │
│     CapabilityBuilder (Appium options) → Environment (enum), │
│     EnvironmentManager (env selection).                       │
├───────────────────────────────────────────────────────────┤
│ Cross-cutting: listeners/ (TestListener implemented;          │
│ RetryAnalyzer/AnnotationTransformer empty), reports/ (both     │
│ empty), utils/screenshot (implemented), utils/gesture          │
│ (reserved, empty), exceptions/ (implemented)                  │
└───────────────────────────────────────────────────────────┘
```

Dependency direction flows top-to-bottom only: Tests depend on BaseTest,
BaseTest depends on driver/config and (indirectly, via test bodies)
Pages/flows, Pages depend only on BasePage, BasePage depends on the
AndroidDriver it's constructed with, DriverManager depends on
DriverFactory, DriverFactory is implemented per platform. Nothing below
depends on anything above it.

**Page Objects must never access `DriverManager` directly.** Page
objects receive their `AndroidDriver` through their constructor
(supplied by the caller via `BaseTest.getDriver()`), and all low-level
interaction is routed through `BasePage` — this keeps the Pages layer
decoupled from driver lifecycle concerns.

## Driver Layer — Implemented

The Android slice of the driver layer is implemented and backs every
passing test in the suite (see [ExecutionFlow.md](ExecutionFlow.md)):

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
     EnvironmentManager
           │  resolves active Environment (enum: QA, STAG, PROD)
           │  from the `-Denv` system property (defaults to QA)
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

- **`Environment`** — enum of `QA`, `STAG`, `PROD`; selects which
  properties file `ConfigReader` loads.
- **`EnvironmentManager`** — resolves the active `Environment` from the
  `env` system property (`-Denv=stag`), defaulting to `QA` if unset.
  `BaseTest.setUp()` calls this rather than hardcoding an environment.
- **`ConfigReader`** — loads `config/{qa,stag,prod}.properties` from the
  classpath for a given `Environment` and exposes raw `get(key)` lookups.
  Throws `ConfigurationException` if the file or key is missing.
- **`ConfigManager`** — wraps a `ConfigReader` with the typed accessors
  the rest of the framework uses (`getPlatform()`, `getAutomationName()`,
  `getDeviceName()`, `getAppiumServerUrl()`, `getGrantPermission()`,
  `getAppInstall()`, `getAppPath()`), so callers never touch property
  keys directly. `getAppPath()` resolves the configured classpath-relative
  APK resource (e.g. `apps/qa/kylas-qa-debug.apk`) to an absolute
  filesystem path, or returns blank untouched if the environment's app
  path hasn't been supplied yet.
- **`CapabilityBuilder`** — turns a `ConfigManager` into the
  `Map<String, Object>` capabilities `AndroidDriverFactory.createDriver`
  expects, validating that `platform`, `automationName`, and `deviceName`
  are non-blank before returning.
- **`UserDataReader`** / **`UserDataManager`** — load
  `testdata/users.properties` and expose typed accessors
  (`getValidUserEmail()`, `getValidUserPassword()`,
  `getInvalidUserEmail()`, `getInvalidUserPassword()`) used by
  `LoginTest` and `LoginFlow`.

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
           │  EnvironmentManager.getEnvironment() → ConfigManager
           │  → CapabilityBuilder.build()
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

`BaseTest` centralizes the driver lifecycle above; it does not contain
login logic, page navigation, or RBAC. All three current test classes
(`DriverSmokeTest`, `LoginTest`, `DashboardTest`) extend it.

## Page Object Model — Implemented (Android)

- **`BasePage`** — constructor takes an `AndroidDriver`, initializes
  `@AndroidFindBy`-annotated fields via `AppiumFieldDecorator`, and
  exposes `click`, `enterText`, `clearAndEnterText`, `getText`, and
  `isDisplayed` helpers, each waiting via a 15-second `WebDriverWait`.
- **`LoginPage`** — email/password entry, sign-in, forgot-password,
  and invalid-login-message assertion.
- **`DashboardPage`** — dashboard/meetings/tasks tab navigation and
  selected-state checks.

## Flows — Implemented (partial)

- **`LoginFlow`** — wraps `LoginPage` + `UserDataManager` behind a
  single `loginAsValidUser()` call that returns a `DashboardPage`. Used
  by `DashboardTest`.

<a id="known-duplication"></a>
**Known duplication (not addressed by this cleanup):** `LoginTest`
drives `LoginPage` directly instead of going through `LoginFlow`, so
the valid-login steps are implemented twice (once in `LoginFlow`, once
inline in `LoginTest.verifyUserCanLoginSuccessfully`). Centralizing
login/session handling so tests reuse `LoginFlow` (or a similar
mechanism) instead of duplicating these steps is a separate, planned
task — this cleanup intentionally left both implementations in place
rather than merging them.

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
- **Interface Segregation** — `pages/common` is reserved for narrow,
  per-screen contracts once a second platform needs to share them;
  nothing currently requires it since only Android is implemented.

## Platform Extensibility (Android → iOS)

Android is the only platform exercised today. The `IOSDriverFactory`
stub and the empty `pages/ios` package already exist so iOS support is
additive later: new factory + new page implementations against the
same (currently empty) `pages/common` contracts, no changes to
`DriverManager`, `ConfigManager`, `BaseTest`, or existing Android code.

## Reporting & Logging

All layers log through Log4j2 (`log4j2.xml`), including third-party
library logs bridged via `log4j-slf4j2-impl`. `TestListener`
(registered in `suites/testng.xml`) logs test start/pass/fail/skip and,
on failure, calls `ScreenshotUtil.takeScreenshot(...)` to save a
screenshot under `target/screenshots/`.

**`reports/ExtentReportManager` and `reports/AllureManager` are both
empty classes with no logic** — neither is referenced anywhere in the
codebase. The `allure-testng` Maven dependency is present and will
still generate raw Allure results from TestNG execution on its own
(independent of `AllureManager`), but no framework code currently
enriches that output (e.g. attaching screenshots to Allure steps) or
produces an ExtentReports HTML dashboard. Building out one or both of
these is future work, not something this cleanup implements.

## Exceptions

- **`FrameworkException`** — root unchecked exception; all others
  extend it.
- **`ConfigurationException`** — actively used throughout `config/`
  (`ConfigReader`, `ConfigManager`, `CapabilityBuilder`,
  `EnvironmentManager`, `UserDataReader`).
- **`DriverInitializationException`** — actively used by
  `DriverManager`, `AndroidDriverFactory`, and `BaseTest`.
- **`PageOperationException`** — defined with the same message/cause
  constructors as the others, but **not thrown anywhere yet**; no page
  object currently raises it. Kept as-is per cleanup scope — a class
  having few/no current usages is not, on its own, evidence it's
  obsolete, and page-level failure handling is expected to grow into it.

## Listeners

- **`TestListener`** — implements `ITestListener`, registered in
  `suites/testng.xml`, actively runs on every test.
- **`RetryAnalyzer`** — empty class, does not implement TestNG's
  `IRetryAnalyzer`, not referenced by any `@Test` annotation or listener.
  No retry behavior currently exists.
- **`AnnotationTransformer`** — empty class, does not implement TestNG's
  `IAnnotationTransformer`, not registered in `suites/testng.xml`. No
  annotation-transformation behavior currently exists.

## Framework Rules

Non-negotiable rules for all future implementation work in this
framework:

- Never use `Thread.sleep()`.
- Always wait via `BasePage`'s helpers (`click`/`enterText`/
  `isDisplayed`, backed by `WebDriverWait`) for synchronization — there
  is currently no separate `WaitUtils` class.
- Every page extends `BasePage`.
- Never instantiate `AndroidDriver`/`IOSDriver` directly.
- Always obtain the driver through `DriverManager` (in `BaseTest`) or
  the constructor-injected driver (in page objects).
- Never hardcode APK paths.
- Never hardcode environment values.
- Never hardcode credentials — use `UserDataManager`.

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
