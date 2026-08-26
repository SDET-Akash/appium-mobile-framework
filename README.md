# Appium Mobile Automation Framework

Production-grade, cross-platform (Android now, iOS-ready) mobile UI automation
framework built on Appium 2.x, Selenium 4 and TestNG.

> **Status:** the Configuration Layer (`Environment`, `ConfigReader`,
> `ConfigManager`, `CapabilityBuilder`), the Android Driver Layer
> (`AndroidDriverFactory`, `DriverManager`), and `BaseTest` (centralized
> driver setup/teardown) are implemented and verified against a real
> Appium session. No test class extends `BaseTest` yet, and page
> objects/reporting/listeners are still scaffolding only — see
> [docs/Architecture.md](docs/Architecture.md) for the full breakdown.

## Tech Stack

| Concern            | Technology                          |
|--------------------|--------------------------------------|
| Language / JDK     | Java 17                              |
| Build tool         | Maven                                |
| Mobile driver      | Appium 2.x (UiAutomator2 / XCUITest) |
| WebDriver protocol | Selenium 4                           |
| Test runner        | TestNG                               |
| Logging            | Log4j2                               |
| Reporting          | Allure Report + ExtentReports        |
| Target platforms   | Android (native/hybrid/Flutter), iOS (planned) |

## Folder Structure

```
appium-mobile-framework/
├── pom.xml
├── .gitignore
├── README.md
├── docs/                                  # Architecture/flow documentation
├── logs/                                  # Runtime log output (gitignored)
├── src/
│   ├── main/
│   │   ├── java/com/automation/mobile/
│   │   │   ├── base/                      # BaseTest (implemented), BasePage (planned)
│   │   │   ├── config/                    # Environment, ConfigReader, ConfigManager,
│   │   │   │                              # CapabilityBuilder — implemented
│   │   │   ├── driver/                    # DriverFactory, AndroidDriverFactory,
│   │   │   │                              # DriverManager (implemented); IOSDriverFactory (stub)
│   │   │   ├── pages/
│   │   │   │   ├── android/                  # Android page/screen objects (planned)
│   │   │   │   ├── ios/                      # iOS page/screen objects (planned)
│   │   │   │   └── common/                   # Cross-platform screen contracts (planned)
│   │   │   ├── listeners/                 # TestNG / Allure listeners (planned)
│   │   │   ├── reports/                   # ExtentReports/Allure managers (planned)
│   │   │   ├── utils/                     # Waits, gestures, helpers (planned)
│   │   │   ├── constants/                 # Framework-wide constants (planned)
│   │   │   ├── enums/                     # Shared enums (planned)
│   │   │   └── exceptions/                # Custom framework exceptions — implemented
│   │   └── resources/
│   └── test/
│       ├── java/com/automation/mobile/
│       │   └── tests/
│       │       ├── android/                  # DriverSmokeTest (temporary verification test)
│       │       └── ios/
│       └── resources/
│           ├── apps/                      # Staged APKs per environment
│           │   └── qa/qa.apk               # QA APK; stag/prod not staged yet
│           ├── config/                    # Per-environment configuration
│           │   ├── qa.properties          # QA environment values (fully populated)
│           │   ├── stag.properties        # Staging environment values (appPath blank)
│           │   └── prod.properties        # Production environment values (appPath blank)
│           ├── testdata/                  # JSON/Excel/CSV test data
│           ├── suites/                    # TestNG XML suite files
│           └── log4j2.xml                 # Logging configuration
```

`pages/` is split by platform (`android/`, `ios/`) with a `common/`
package for shared contracts, so platform-specific implementations can
diverge without leaking into shared test logic — in line with the
Interface Segregation / Dependency Inversion principles the driver layer
already follows. See [docs/FolderStructure.md](docs/FolderStructure.md)
for the fully annotated, up-to-date tree.

Empty directories contain a `.gitkeep` so the structure is preserved in git
until real classes are added.

## Dependencies — What and Why

| Dependency | Purpose |
|---|---|
| `io.appium:java-client` | Appium client library. Drives Android/iOS apps through a running Appium 2.x server (UiAutomator2/XCUITest drivers). `AppiumDriver` extends Selenium's `RemoteWebDriver`, so this is also what pulls in the Selenium remote-driver stack transitively. |
| `org.seleniumhq.selenium:selenium-java` | Explicitly pinned Selenium 4 dependency. Declared directly (not left to whatever version `java-client` happens to pull in) so the WebDriver API version is fixed and won't silently drift between Appium client releases. |
| `org.testng:testng` | Test execution engine: annotations (`@Test`, `@BeforeMethod`, ...), assertions, suite XML execution, data providers, parallel execution, and listener hooks. |
| `org.apache.logging.log4j:log4j-api` / `log4j-core` | Core logging framework for the framework's own log output (console + rolling file), configured via `log4j2.xml`. |
| `org.apache.logging.log4j:log4j-slf4j2-impl` | SLF4J-to-Log4j2 bridge. Selenium/Appium's internal libraries log via SLF4J; this routes that output into the same Log4j2 pipeline instead of leaving it in a separate/no-op logger. |
| `io.qameta.allure:allure-testng` | Allure's TestNG adapter — listens to test execution and produces Allure's step-level, attachment-capable HTML report. |
| `io.qameta.allure:allure-bom` (import scope, `dependencyManagement` only) | Bill-of-materials that pins every Allure module (including transitive `allure-java` artifacts) to one consistent version, preventing Allure dependency version mismatches. |
| `org.aspectj:aspectjweaver` | Runtime weaving agent Allure requires to process `@Step`/`@Attachment` annotations. Loaded via a `-javaagent` argument in the Surefire `argLine`. |
| `com.aventstack:extentreports` | Standalone HTML execution dashboard, used alongside Allure for a quick, self-contained local report. |

**Deliberately excluded:** no JSON/YAML config libraries, no Excel/POI, no
REST client, no BDD (Cucumber) layer, no dependency-injection framework.
None of these are needed for setup itself — they'll only be added if/when
an actual requirement (e.g. Excel-based test data) shows up, per "no
unnecessary libraries."

## Build Configuration

- **Java 17** — enforced via `maven.compiler.release=17` in the
  `maven-compiler-plugin`.
- **Surefire** — configured to run `src/test/resources/suites/testng.xml`
  (currently an empty suite skeleton) and to load the AspectJ weaver agent
  required by Allure annotations.

## Running

```bash
mvn clean test
```

This runs `DriverSmokeTest`, which creates a real Android Appium session
against a running Appium server and emulator/device (`emulator-5554` by
default, per `qa.properties`) and launches the QA APK. An Appium server
and an available Android device/emulator must be running first.

## Next Steps

- Refactor `DriverSmokeTest` to extend `BaseTest` instead of performing
  driver setup/teardown manually.
- Introduce a proper environment-selection mechanism (`BaseTest`
  currently fixes `Environment.QA`).
- Implement `BasePage` and the first real page objects/tests
  (`LoginPage`, `LoginTest`).

See [docs/FrameworkFlow.md](docs/FrameworkFlow.md) for the full
implemented/next/planned breakdown.
