# Framework Flow

> Status: planned design — describes how the layers are intended to
> connect once implemented. Nothing described here is wired up yet; see
> [Architecture.md](Architecture.md) for the current package-level state.

This document describes the *structural* flow — which layer calls which
— as opposed to [ExecutionFlow.md](ExecutionFlow.md), which describes the
runtime sequence of a single test run.

## Layer Call Graph

```
 Test class (tests/android, tests/ios)
        │  extends
        ▼
 BaseTest (base/)
        │  uses
        ├──────────────► ConfigManager (config/)
        │                     │ uses
        │                     ├──► ConfigReader
        │                     ├──► CapabilityBuilder
        │                     └──► Environment
        │
        ├──────────────► DriverManager (driver/)
        │                     │ delegates creation to
        │                     └──► DriverFactory
        │                              ├──► AndroidDriverFactory
        │                              └──► IOSDriverFactory
        │
        └──────────────► Page objects (pages/<platform>)
                              │ implement
                              └──► pages/common contracts
                              │ use
                              ├──► DriverManager (to get the active driver)
                              └──► utils/ (wait, gesture, screenshot, ...)

 Cross-cutting (invoked via TestNG, not called directly by tests):
        listeners/TestListener ──► reports/ExtentReportManager
                                ──► reports/AllureManager
        listeners/AnnotationTransformer ──► listeners/RetryAnalyzer

 Exceptions (exceptions/) are thrown by config/, driver/, and pages/,
 and caught/logged by listeners/TestListener.
```

## Package Responsibilities at a Glance

| Package | Responsibility |
|---|---|
| `config/` | Resolve environment + capabilities into a single typed access point |
| `driver/` | Create and own the lifecycle of the platform driver session |
| `pages/` | Represent app screens; the only layer that touches driver elements directly |
| `base/` | Wire config → driver → test, and reverse on teardown |
| `listeners/` | React to TestNG events without tests calling anything explicitly |
| `reports/` | Translate listener events into Extent/Allure output |
| `utils/` | Stateless helpers shared by pages (never call driver lifecycle themselves) |
| `exceptions/` | Typed failures raised by config/driver/pages, caught by listeners |
| `models/`, `builders/`, `factory/` | Test-data representation and construction, independent of the UI layers above |

## Rule of thumb

If a change requires touching two layers that aren't adjacent in the
call graph above (e.g. a test class reaching into `DriverFactory`
directly, skipping `DriverManager`), it's a sign the change is bypassing
the intended architecture.
