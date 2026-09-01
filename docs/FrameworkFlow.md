# Framework Flow

> Status (updated 2026-08-27): the Configuration Layer, Android Driver
> Layer, `BaseTest`, `BasePage`, Android page objects, `LoginFlow`, and
> `TestListener` are implemented and exercised by the passing Android
> test suite — see the status breakdown below and
> [Architecture.md](Architecture.md) for the full package-level state.

This document describes the *structural* flow — which layer calls which
— as opposed to [ExecutionFlow.md](ExecutionFlow.md), which describes the
runtime sequence of a single test run.

## Build Status

**IMPLEMENTED:**
- Project scaffolding (package structure, build config)
- Configuration Layer (`Environment`, `EnvironmentManager`,
  `ConfigReader`, `ConfigManager`, `CapabilityBuilder`) — resolves
  per-environment properties into an Appium capabilities map
- Android Driver Layer (`DriverFactory`, `AndroidDriverFactory`,
  `DriverManager`)
- `BaseTest` (`@BeforeMethod`/`@AfterMethod` driver setup-teardown,
  `protected getDriver()`) — extended by all three current test classes
- `BasePage` (element click/enterText/getText/isDisplayed helpers via
  `PageFactory` + `WebDriverWait`)
- Android page objects: `LoginPage`, `DashboardPage`
- `LoginFlow` (`flows/`) — used by `DashboardTest`
- User test data (`UserDataReader`, `UserDataManager`,
  `testdata/users.properties`)
- `TestListener` — registered in `suites/testng.xml`; logs test
  start/pass/fail/skip and captures a screenshot on failure via
  `ScreenshotUtil`
- Test classes: `DriverSmokeTest`, `LoginTest`, `DashboardTest` — all
  passing against a real Appium session on `emulator-5554`
- Environment property files (`qa.properties`, `stag.properties`,
  `prod.properties`); QA APK staged at `apps/qa/kylas-qa-debug.apk`

**NOT YET IMPLEMENTED:**
- `RetryAnalyzer` / `AnnotationTransformer` — both empty classes, not
  registered or wired to any test
- `ExtentReportManager` / `AllureManager` — both empty classes, not
  called by `TestListener` or anywhere else
- iOS support (`IOSDriverFactory` is a stub; `pages/ios`, `tests/ios`
  are empty)
- Gesture utilities (`utils/gesture` is empty — reserved for future
  long-press/swipe/scroll/drag-and-drop helpers)
- Centralized login/session reuse across all tests (`LoginTest`
  currently duplicates the login steps `LoginFlow` already implements —
  see [Architecture.md#known-duplication](Architecture.md#known-duplication));
  this is a separate, planned task
- CI/CD

## Layer Call Graph

```
 Test class (tests/android; tests/ios reserved)
        │  extends
        ▼
 BaseTest (base/)
        │  uses
        ├──────────────► ConfigManager (config/)
        │                     │ uses
        │                     ├──► ConfigReader
        │                     ├──► CapabilityBuilder
        │                     └──► Environment (via EnvironmentManager)
        │
        └──────────────► DriverManager (driver/)
                              │ delegates creation to
                              └──► DriverFactory
                                       ├──► AndroidDriverFactory (implemented)
                                       └──► IOSDriverFactory (stub)

 Test body (called directly by the test method, not by BaseTest):
        Page objects (pages/android) ──extend──► BasePage (base/)
        flows/LoginFlow ──wraps──► pages/android/LoginPage, DashboardPage,
                                    config/UserDataManager

 Cross-cutting (invoked via TestNG, not called directly by tests):
        listeners/TestListener (implemented, registered)
              ──► utils/screenshot/ScreenshotUtil (implemented, on failure)
              ──► reports/ExtentReportManager (empty — not actually called)
              ──► reports/AllureManager (empty — not actually called)
        listeners/AnnotationTransformer (empty, unregistered)
              ──► listeners/RetryAnalyzer (empty, unregistered)

 Exceptions (exceptions/) are thrown by config/ and driver/ (base/ also
 wraps a MalformedURLException into DriverInitializationException).
 PageOperationException exists but is not thrown by pages/ yet.
```

## Package Responsibilities at a Glance

| Package | Responsibility | Status |
|---|---|---|
| `config/` | Resolve environment + capabilities into a single typed access point, plus test user data | Implemented |
| `driver/` | Create and own the lifecycle of the platform driver session | Implemented (Android); iOS stub |
| `pages/` | Represent app screens; the only layer that touches driver elements directly | Implemented (`android/`); `ios/`, `common/` reserved/empty |
| `flows/` | Multi-page/multi-step test flows composed from page objects | Implemented (`LoginFlow` only) |
| `base/` | Wire config → driver → test, and reverse on teardown; low-level page interaction helpers | Implemented |
| `listeners/` | React to TestNG events without tests calling anything explicitly | `TestListener` implemented; `RetryAnalyzer`/`AnnotationTransformer` empty |
| `reports/` | Translate listener events into Extent/Allure output | Empty — not implemented |
| `utils/` | Stateless helpers shared by pages | `screenshot/` implemented; `gesture/` reserved/empty |
| `exceptions/` | Typed failures raised by config/ and driver/ | Implemented (`PageOperationException` defined but unused) |

## Rule of thumb

If a change requires touching two layers that aren't adjacent in the
call graph above (e.g. a test class reaching into `DriverFactory`
directly, skipping `DriverManager`), it's a sign the change is bypassing
the intended architecture.
