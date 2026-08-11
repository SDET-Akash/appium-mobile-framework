# Execution Flow

> Status: planned design — describes the intended runtime sequence for a
> single `mvn clean test` run once the driver/base/listener layers are
> implemented. Today, running `mvn clean test` executes the empty suite
> in `src/test/resources/suites/testng.xml` (0 tests) — this document is
> the target behavior, not current behavior.

## Sequence for one test run

1. **Suite start** — Surefire loads `suites/testng.xml`.
   `listeners/AnnotationTransformer` registers, attaching
   `listeners/RetryAnalyzer` to every `@Test` method.
2. **Config resolution** — `BaseTest` (suite/class setup) calls
   `config/ConfigManager`, which uses `config/ConfigReader` to load
   `src/test/resources/config/config.properties` and resolves the target
   `config/Environment`.
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
