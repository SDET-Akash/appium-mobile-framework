# Folder Structure

> Status: structure frozen as of this document's last update. Every
> package below exists in the repo today; classes marked "(empty)" have
> JavaDoc only, no logic yet.

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
    │   ├── config/                 Configuration resolution
    │   │   ├── ConfigReader.java        (empty) — raw value source contract
    │   │   ├── ConfigManager.java       (empty) — typed config access point
    │   │   ├── CapabilityBuilder.java   (empty) — Appium options assembly
    │   │   └── Environment.java         (empty) — target environment enum
    │   ├── driver/                 Driver lifecycle
    │   │   ├── DriverFactory.java           (empty) — factory contract
    │   │   ├── DriverManager.java           (empty) — session lifecycle owner
    │   │   ├── AndroidDriverFactory.java     (empty) — UiAutomator2 sessions
    │   │   └── IOSDriverFactory.java         (empty) — XCUITest sessions (future)
    │   ├── pages/                  Page Object Model
    │   │   ├── android/                 (empty — Android screens)
    │   │   ├── ios/                      (empty — iOS screens, future)
    │   │   └── common/                   (empty — shared screen contracts)
    │   ├── exceptions/              Custom exception hierarchy
    │   │   ├── FrameworkException.java              (empty) — root exception
    │   │   ├── DriverInitializationException.java   (empty)
    │   │   ├── ConfigurationException.java          (empty)
    │   │   └── PageOperationException.java          (empty)
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
        │   ├── base/                     (empty) — BaseTest
        │   └── tests/
        │       ├── android/              (empty)
        │       └── ios/                  (empty)
        └── resources/
            ├── config/config.properties   Placeholder keys only (environment, platform)
            ├── testdata/                   (empty)
            ├── suites/testng.xml           Empty <suite> skeleton
            └── log4j2.xml                  Console + rolling-file logging config
```

## Naming Conventions

- Base package: `com.automation.mobile`
- Page objects live under `pages/<platform>` — never `screens/` (superseded
  naming, see decision log in [Architecture.md](Architecture.md)).
- One factory class per platform in `driver/`, no nested `android/`/`ios/`
  subfolders under `driver/` (superseded by flat, named files).
