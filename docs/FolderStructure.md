# Folder Structure

> Status: reflects the actual repository contents as of this document's
> last update. Classes marked "(empty)" have JavaDoc only, no logic yet;
> classes marked "(implemented)" contain real logic and have been
> compiled/run.

```
appium-mobile-framework/
├── pom.xml                        Java 17, TestNG/Surefire/Allure/Extent build config
├── .gitignore
├── README.md
├── docs/                          This documentation set
├── builds/                        Local APK/AAB/IPA staging (gitignored, .gitkeep only)
│   ├── qa/
│   ├── dev/
│   └── archive/
├── logs/                          Runtime log output (gitignored)
└── src/
    ├── main/java/com/automation/mobile/
    │   ├── base/                    Test/page base classes
    │   │   ├── BaseTest.java            (empty) — planned suite/method setup-teardown
    │   │   └── BasePage.java            (empty) — planned low-level driver interaction owner
    │   ├── config/                 Configuration resolution — NOT YET IMPLEMENTED
    │   │   ├── ConfigReader.java        (empty) — raw value source contract
    │   │   ├── ConfigManager.java       (empty) — typed config access point
    │   │   ├── CapabilityBuilder.java   (empty) — Appium options assembly
    │   │   └── Environment.java         (empty) — target environment enum
    │   ├── driver/                 Driver lifecycle — IMPLEMENTED (Android)
    │   │   ├── DriverFactory.java           (implemented) — factory contract
    │   │   ├── DriverManager.java           (implemented) — ThreadLocal<AndroidDriver> lifecycle owner
    │   │   ├── AndroidDriverFactory.java     (implemented) — builds UiAutomator2Options + AndroidDriver
    │   │   └── IOSDriverFactory.java         (stub) — throws UnsupportedOperationException, no iOS logic
    │   ├── pages/                  Page Object Model
    │   │   ├── android/                 (empty — Android screens)
    │   │   ├── ios/                      (empty — iOS screens, future)
    │   │   └── common/                   (empty — shared screen contracts)
    │   ├── exceptions/              Custom exception hierarchy
    │   │   ├── FrameworkException.java              (implemented) — root exception, message/cause constructors
    │   │   ├── DriverInitializationException.java   (implemented) — used by the Driver Layer
    │   │   ├── ConfigurationException.java          (implemented) — message/cause constructors, unused so far
    │   │   └── PageOperationException.java          (implemented) — message/cause constructors, unused so far
    │   ├── listeners/               TestNG hooks
    │   │   ├── TestListener.java            (empty) — ITestListener
    │   │   ├── RetryAnalyzer.java           (empty) — IRetryAnalyzer
    │   │   └── AnnotationTransformer.java   (empty) — IAnnotationTransformer
    │   ├── reports/                 Reporting managers
    │   │   ├── ExtentReportManager.java     (empty)
    │   │   └── AllureManager.java           (empty)
    │   ├── utils/                   Utilities, split by concern
    │   │   ├── wait/          (empty)
    │   │   ├── gesture/       (empty)
    │   │   ├── screenshot/    (empty)
    │   │   ├── device/        (empty)
    │   │   ├── file/          (empty)
    │   │   └── date/          (empty)
    │   ├── factory/                 (empty) — test-data object factories (Lead/Deal/Contact/User, future)
    │   ├── models/                  (empty) — POJOs/DTOs
    │   ├── builders/                (empty) — fluent test-data builders
    │   ├── constants/               (empty) — framework-wide constants
    │   └── enums/                    (empty) — shared enums
    └── test/
        ├── java/com/automation/mobile/
        │   └── tests/
        │       ├── android/
        │       │   └── DriverSmokeTest.java   (implemented, temporary) — verifies the
        │       │       Driver Layer against a real Appium session; hardcodes
        │       │       Appium URL, device name and APK path since the
        │       │       Configuration Layer doesn't exist yet
        │       └── ios/                  (empty)
        └── resources/
            ├── config/
            │   ├── qa.properties        deviceName/appiumServerUrl populated; appPath blank
            │   ├── stag.properties      deviceName/appiumServerUrl populated; appPath blank
            │   └── prod.properties      deviceName/appiumServerUrl populated; appPath blank
            ├── testdata/                   (empty)
            ├── suites/testng.xml           <test> entry running DriverSmokeTest
            └── log4j2.xml                  Console + rolling-file logging config
```

`appPath` is blank in all three environment property files because the
only APK currently available (`app-dev-debug.apk`) has not been
confirmed to belong to a specific one of QA/STAG/PROD — its filename is
not treated as proof. See [Architecture.md](Architecture.md#configuration-layer--next-not-yet-implemented).

`base/` was moved from `src/test/java` to `src/main/java` so that both
tests (`BaseTest`) and future page objects (`BasePage`) can depend on it
from a shared, non-test-scoped location.

## Naming Conventions

- Base package: `com.automation.mobile`
- Page objects live under `pages/<platform>` — never `screens/` (superseded
  naming, see decision log in [Architecture.md](Architecture.md)).
- One factory class per platform in `driver/`, no nested `android/`/`ios/`
  subfolders under `driver/` (superseded by flat, named files).
