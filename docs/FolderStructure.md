# Folder Structure

> Status: reflects the actual repository contents after the 2026-08-27
> cleanup pass. Classes marked "(empty)" have JavaDoc/scaffolding only,
> no logic yet; classes marked "(implemented)" contain real logic and
> are exercised by the passing Android test suite.
>
> This cleanup removed six directories that contained only a `.gitkeep`
> placeholder and had no implementation or concrete near-term purpose:
> `builders/`, `constants/`, `enums/`, `utils/date/`, `utils/device/`,
> `utils/file/`. A handful of other placeholder directories were
> deliberately **kept** even though they currently contain only
> `.gitkeep` — see "Reserved placeholder directories" below.

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
    │   ├── base/                    Test/page base classes — IMPLEMENTED
    │   │   ├── BaseTest.java            (implemented) — @BeforeMethod/@AfterMethod driver
    │   │   │                             setup-teardown; every test class extends it
    │   │   └── BasePage.java            (implemented) — click/enterText/getText/isDisplayed
    │   │                                 helpers built on PageFactory + WebDriverWait
    │   ├── config/                 Configuration resolution — IMPLEMENTED
    │   │   ├── ConfigReader.java        (implemented) — loads config/{qa,stag,prod}.properties
    │   │   ├── ConfigManager.java       (implemented) — typed config access point
    │   │   ├── CapabilityBuilder.java   (implemented) — Appium capabilities assembly
    │   │   ├── Environment.java         (implemented) — QA/STAG/PROD enum
    │   │   ├── EnvironmentManager.java  (implemented) — resolves active Environment from
    │   │   │                             the `-Denv` system property (defaults to QA)
    │   │   ├── UserDataManager.java     (implemented) — typed accessors for test user data
    │   │   └── UserDataReader.java      (implemented) — loads testdata/users.properties
    │   ├── driver/                 Driver lifecycle — IMPLEMENTED (Android)
    │   │   ├── DriverFactory.java           (implemented) — factory contract
    │   │   ├── DriverManager.java           (implemented) — ThreadLocal<AndroidDriver> lifecycle owner
    │   │   ├── AndroidDriverFactory.java     (implemented) — builds UiAutomator2Options + AndroidDriver
    │   │   └── IOSDriverFactory.java         (stub) — throws UnsupportedOperationException, no iOS logic
    │   ├── flows/                   Multi-page test flows — IMPLEMENTED (partial)
    │   │   └── LoginFlow.java           (implemented) — `loginAsValidUser()`, used by
    │   │                                 DashboardTest; LoginTest does not use it yet (see
    │   │                                 [Architecture.md](Architecture.md#known-duplication))
    │   ├── pages/                  Page Object Model
    │   │   ├── android/                 IMPLEMENTED
    │   │   │   ├── LoginPage.java            (implemented)
    │   │   │   └── DashboardPage.java        (implemented)
    │   │   ├── ios/                      (empty — reserved for planned iOS page objects)
    │   │   └── common/                   (empty — reserved for future shared/cross-platform
    │   │                                   page contracts)
    │   ├── exceptions/              Custom exception hierarchy — IMPLEMENTED
    │   │   ├── FrameworkException.java              (implemented) — root exception, message/cause constructors
    │   │   ├── DriverInitializationException.java   (implemented) — used by driver/ and base/
    │   │   ├── ConfigurationException.java          (implemented) — used throughout config/
    │   │   └── PageOperationException.java          (implemented, unused) — defined but not
    │   │                                              yet thrown anywhere in the codebase
    │   ├── listeners/               TestNG hooks
    │   │   ├── TestListener.java            (implemented) — ITestListener, registered in
    │   │   │                                 suites/testng.xml; logs test start/pass/fail
    │   │   │                                 and captures a screenshot on failure
    │   │   ├── RetryAnalyzer.java           (empty) — not registered anywhere, no retry logic
    │   │   └── AnnotationTransformer.java   (empty) — not registered anywhere, does not
    │   │                                     implement IAnnotationTransformer
    │   ├── reports/                 Reporting managers — NOT IMPLEMENTED
    │   │   ├── ExtentReportManager.java     (empty) — no logic, unused
    │   │   └── AllureManager.java           (empty) — no logic, unused (the `allure-testng`
    │   │                                     Maven dependency runs independently of this class)
    │   └── utils/                   Utilities, split by concern
    │       ├── screenshot/                 IMPLEMENTED
    │       │   └── ScreenshotUtil.java          (implemented) — used by TestListener on failure
    │       └── gesture/                    (empty — reserved for future Appium gestures:
    │                                         long press, swipe, scroll, drag and drop)
    └── test/
        ├── java/com/automation/mobile/
        │   └── tests/
        │       ├── android/                  IMPLEMENTED — all three pass against a real
        │       │   │                          Appium session (see [ExecutionFlow.md](ExecutionFlow.md))
        │       │   ├── DriverSmokeTest.java      (implemented) — verifies BaseTest's driver
        │       │   │                              lifecycle produces a working Android session
        │       │   ├── LoginTest.java             (implemented) — valid + invalid login,
        │       │   │                              drives LoginPage directly (not via LoginFlow)
        │       │   └── DashboardTest.java          (implemented) — logs in via LoginFlow,
        │       │                                    verifies dashboard tab navigation
        │       └── ios/                      (empty — reserved for future iOS tests)
        └── resources/
            ├── apps/
            │   ├── qa/kylas-qa-debug.apk       QA Android APK (Kylas Sales app)
            │   ├── stag/                       (.gitkeep only — reserved; no STAG APK staged yet)
            │   └── prod/                       (empty, not git-tracked — no APK staged yet)
            ├── config/
            │   ├── qa.properties        fully populated, including appPath=apps/qa/kylas-qa-debug.apk
            │   ├── stag.properties      deviceName/appiumServerUrl populated; appPath blank
            │   └── prod.properties      deviceName/appiumServerUrl populated; appPath blank
            ├── testdata/
            │   └── users.properties        valid/invalid user credentials used by LoginTest/LoginFlow
            ├── suites/testng.xml           registers TestListener; runs DriverSmokeTest,
            │                                LoginTest, DashboardTest
            └── log4j2.xml                  Console + rolling-file logging config
```

## Removed in the 2026-08-27 cleanup pass

These directories contained only a `.gitkeep` file, had no implementation,
and had no concrete near-term purpose called out by the team — so they
were removed along with their `.gitkeep`:

- `src/main/java/com/automation/mobile/builders/`
- `src/main/java/com/automation/mobile/constants/`
- `src/main/java/com/automation/mobile/enums/`
- `src/main/java/com/automation/mobile/utils/date/`
- `src/main/java/com/automation/mobile/utils/device/`
- `src/main/java/com/automation/mobile/utils/file/`

They can be re-created trivially whenever a concrete need (a constant, an
enum, a date/device/file helper) actually arises — nothing else in the
framework referenced them.

## Reserved placeholder directories (kept)

The following also currently contain only `.gitkeep`, but were
**deliberately kept** because they map to already-planned, near-term work
rather than being speculative scaffolding:

| Directory | Reason kept |
|---|---|
| `pages/common/` | Reserved for shared/cross-platform page contracts once iOS page objects exist |
| `pages/ios/` | Part of the planned iOS platform support |
| `utils/gesture/` | Reserved for future Appium gesture helpers (long press, swipe, scroll, drag-and-drop) — no gesture-based test currently needs one |
| `tests/ios/` | Reserved for future iOS test classes |
| `apps/stag/` | Corresponds to the already-configured `stag.properties` environment; will hold a STAG APK once one is staged |

## Naming Conventions

- Base package: `com.automation.mobile`
- Page objects live under `pages/<platform>` — never `screens/` (superseded
  naming, see decision log in [Architecture.md](Architecture.md)).
- One factory class per platform in `driver/`, no nested `android/`/`ios/`
  subfolders under `driver/` (superseded by flat, named files).
