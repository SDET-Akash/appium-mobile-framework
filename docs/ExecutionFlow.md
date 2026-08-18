# Execution Flow

> Status: two flows are documented here. The first — **Currently
> Verified** — is real and has run successfully against a live Appium
> server and emulator. The second — **Planned Full Framework Flow** — is
> still target design; the config/base/pages/listeners/reports layers it
> describes are not implemented yet.

## Currently Verified: Driver Smoke Test

`DriverSmokeTest` (`src/test/java/com/automation/mobile/tests/android/DriverSmokeTest.java`)
is a temporary TestNG test that exercises the real Driver Layer end to
end — no Configuration Layer, no `BaseTest`/`BasePage`, no page objects
involved. It has been run and passed against:

- Appium server: `http://127.0.0.1:4723`
- Device: `emulator-5554`
- App: the Flutter Android APK at `/home/akash/kylasApk/app-dev-debug.apk/app-dev-debug.apk`

### Startup

```
DriverSmokeTest
      │  builds a capabilities map (deviceName, app) and calls
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
Flutter APK (app-dev-debug.apk)
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

This confirms `DriverFactory` → `AndroidDriverFactory` → `AndroidDriver`
→ `DriverManager` work correctly together against a real environment.
It does **not** exercise config resolution, capability building from
files, retries, reporting, or page objects — none of those exist yet.

## Planned Full Framework Flow

The sequence below is the target behavior once the remaining layers are
built. It is not what `DriverSmokeTest` above does, and none of the
config/base/listener/report classes it references have logic yet.

## Sequence for one test run

1. **Suite start** — Surefire loads `suites/testng.xml`.
   `listeners/AnnotationTransformer` registers, attaching
   `listeners/RetryAnalyzer` to every `@Test` method.
2. **Config resolution** — `BaseTest` (suite/class setup) calls
   `config/ConfigManager`, which uses `config/ConfigReader` to load the
   appropriate `src/test/resources/config/{qa,stag,prod}.properties` file
   and resolves the target `config/Environment`. (These property files
   already exist; `ConfigReader`/`ConfigManager` do not yet — see
   [Architecture.md](Architecture.md#configuration-layer--next-not-yet-implemented).)
3. **Capability assembly** — `config/CapabilityBuilder` turns resolved
   config into platform capabilities (`UiAutomator2Options` /
   `XCUITestOptions`).
4. **Driver creation** — `BaseTest` asks `driver/DriverManager` for a
   driver; `DriverManager` delegates to the platform's
   `driver/DriverFactory` implementation (`AndroidDriverFactory` or
   `IOSDriverFactory`), which opens the Appium session. Failures here
   raise `exceptions/DriverInitializationException`.
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
