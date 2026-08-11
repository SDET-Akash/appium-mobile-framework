# Architecture

> Status: structure frozen, no logic implemented yet. This document
> describes the intended architecture the scaffolded packages are built
> for, not code that currently exists.

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
│ BaseTest (src/main/java/.../base)                           │
│   - Suite/class/method setup & teardown.                    │
│   - Depend on: driver/ (via DriverManager), config/,         │
│     listeners/                                               │
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
│ Driver (driver/)                                            │
│   - DriverManager (lifecycle/thread-local ownership),        │
│     delegating creation to DriverFactory (contract).         │
│   - Depend on: config/ (capabilities), exceptions/           │
├───────────────────────────────────────────────────────────┤
│ DriverFactory implementations (driver/)                      │
│   - AndroidDriverFactory / IOSDriverFactory.                 │
├───────────────────────────────────────────────────────────┤
│ Config (config/)                                             │
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
