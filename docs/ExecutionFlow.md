# Execution Flow

> Status: three flows are documented here. The first two — **Currently
> Verified** — are real and have run successfully against a live Appium
> server and emulator. The third — **Planned Full Framework Flow** — is
> still target design; the pages/listeners/reports layers it describes
> are not implemented yet.

## Currently Verified: Driver Smoke Test

`DriverSmokeTest` (`src/test/java/com/automation/mobile/tests/android/DriverSmokeTest.java`)
is a temporary TestNG test that exercises the Configuration Layer and
the Driver Layer end to end — no `BaseTest`/`BasePage`, no page objects
involved (it performs the same setup `BaseTest` now centralizes, but
manually, as its own verification that the layers wire together
correctly). It has been run and passed against:

- Environment: `Environment.QA` (`src/test/resources/config/qa.properties`)
- Appium server: `http://127.0.0.1:4723`
- Device: `emulator-5554`
- App: `apps/qa/qa.apk` (resolved via `ConfigManager.getAppPath()`) — the Kylas
  Sales Android app, package `io.kylas.sales.droid.qa.debug`

### Startup

```
DriverSmokeTest
      │  new ConfigManager(Environment.QA)
      ▼
ConfigManager  ──uses──►  ConfigReader (loads qa.properties)
      │
      ▼
CapabilityBuilder.build()
      │  produces a capabilities map (platform, automationName,
      │  deviceName, app, autoGrantPermissions, enforceAppInstall)
      ▼
AndroidDriverFactory.createDriver(serverUrl, capabilities)
      │  builds UiAutomator2Options, then constructs
      ▼
AndroidDriver
      │  session opened against
      ▼
Appium Server (http://127.0.0.1:4723)
      │  drives the session through
      ▼
UiAutomator2
      │  automates
      ▼
Android Emulator (emulator-5554)
      │  which launches
      ▼
Kylas Sales Android app (apps/qa/qa.apk)
```

The test then stores the driver via `DriverManager.setDriver(driver)`,
asserts a non-null session ID and non-null current package (proving the
app actually launched), and logs session ID, current package, and
current activity.

### Teardown

```
DriverManager.removeDriver()
      │
      ▼
 try { driver.quit() }        — closes the Appium session
      │
      ▼
 finally { ThreadLocal.remove() }   — always runs, even if quit() throws
```

### What this proves vs. what it doesn't

This confirms `Environment` → `ConfigReader` → `ConfigManager` →
`CapabilityBuilder` → `AndroidDriverFactory` → `AndroidDriver` →
`DriverManager` work correctly together against a real environment. It
does **not** exercise `BaseTest` (no test extends it yet), retries,
reporting, or page objects — those don't exist yet or aren't wired in.

## Currently Verified: BaseTest (compiled, not yet exercised by a test)

`BaseTest` (`src/main/java/com/automation/mobile/base/BaseTest.java`)
implements the same setup/teardown flow as `DriverSmokeTest` above, but
centralized behind TestNG's `@BeforeMethod`/`@AfterMethod` so future test
classes get it just by extending `BaseTest`:

```
@BeforeMethod setUp()
      │  ConfigManager(Environment.QA) → CapabilityBuilder.build()
      │  → new URL(appiumServerUrl)  [MalformedURLException → DriverInitializationException]
      ▼
AndroidDriverFactory.createDriver(url, capabilities)
      ▼
DriverManager.setDriver(driver)

(test body — via protected getDriver())

@AfterMethod(alwaysRun = true) tearDown()
      ▼
DriverManager.removeDriver()
```

It has been verified via `mvn clean test-compile` (compiles cleanly)
and `mvn clean test` (build stays green; `DriverSmokeTest` still passes
unmodified alongside it). No test class extends `BaseTest` yet —
refactoring `DriverSmokeTest` to do so is the next step, at which point
this section should be merged into "Currently Verified" above.

## Planned Full Framework Flow

The sequence below is the target behavior once the remaining layers are
built. It is not what `DriverSmokeTest` above does, and none of the
config/base/listener/report classes it references have logic yet.

## Sequence for one test run

1. **Suite start** — Surefire loads `suites/testng.xml`.
   `listeners/AnnotationTransformer` registers, attaching
   `listeners/RetryAnalyzer` to every `@Test` method.
2. **Config resolution** — `BaseTest.setUp()` (`@BeforeMethod`, currently
   fixed to `Environment.QA`) constructs `config/ConfigManager`, which
   uses `config/ConfigReader` to load the appropriate
   `src/test/resources/config/{qa,stag,prod}.properties` file. This part
   is implemented — see
   [Architecture.md](Architecture.md#configuration-layer--implemented).
   A proper environment-selection mechanism (replacing the hardcoded
   `QA`) is still to come.
3. **Capability assembly** — `config/CapabilityBuilder.build()` turns
   resolved config into the Appium capabilities map (implemented).
4. **Driver creation** — `BaseTest.setUp()` builds the driver via
   `driver/AndroidDriverFactory` directly and stores it with
   `driver/DriverManager.setDriver(...)`; `IOSDriverFactory` support is
   not wired in yet. Failures here raise
   `exceptions/DriverInitializationException`.
5. **Test execution** — the test class calls into `pages/` objects only.
   Page objects pull the active driver from `DriverManager` and use
   `utils/wait`, `utils/gesture`, etc. for interactions.
6. **Per-test reporting** — `listeners/TestListener` observes
   start/success/failure and forwards to both
   `reports/ExtentReportManager` and `reports/AllureManager`. On
   failure, `utils/screenshot` captures the screen and attaches it to
   both reports.
7. **Retry (on failure)** — `listeners/RetryAnalyzer` decides whether to
   re-run the failed method before it's reported as a final failure.
8. **Driver teardown** — after each test (or suite, depending on the
   configured driver scope), `DriverManager` quits the session.
9. **Suite end** — `reports/ExtentReportManager` flushes the HTML
   report; Allure results are written to `allure-results/` for
   `allure serve`/`allure generate`.

## Failure Propagation

```
Config/Driver/Page failure
        │
        ▼
 exceptions/* (Configuration|DriverInitialization|PageOperation)Exception
        │
        ▼
 TestNG marks test failed ──► listeners/TestListener
        │                              │
        ▼                              ▼
 RetryAnalyzer (retry?)      reports/{Extent,Allure}Manager (record + screenshot)
```

## Parallel Execution

`DriverManager` is expected to hold the active driver per-thread (not
static/shared) so TestNG's suite-level `parallel="methods"`/`"classes"`
execution is safe once implemented — each thread gets its own driver
session from its own `DriverFactory` call.
