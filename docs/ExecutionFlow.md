# Execution Flow

> Status (updated 2026-08-27): three test classes are currently
> implemented and passing against a real Appium server and Android
> emulator — `DriverSmokeTest`, `LoginTest`, and `DashboardTest` (see
> **Currently Verified** below). Retry logic and the Extent/Allure
> reporting managers are not implemented yet — see **Not Yet
> Implemented** at the bottom of this document.

## Currently Verified: Full Test Suite

All three classes registered in `suites/testng.xml` extend `BaseTest`
and were run together via `mvn clean test` against:

- Environment: `Environment.QA` (`src/test/resources/config/qa.properties`)
- Appium server: `http://127.0.0.1:4723`
- Device: `emulator-5554`
- App: `apps/qa/kylas-qa-debug.apk` (resolved via `ConfigManager.getAppPath()`) —
  the Kylas Sales Android app, package `io.kylas.sales.droid.qa.debug`

Result: `Tests run: 4, Failures: 0, Errors: 0, Skipped: 0` (`DriverSmokeTest`
runs one `@Test` method; `LoginTest` runs two).

### Startup (every test, via BaseTest)

```
BaseTest.setUp()  [@BeforeMethod]
      │  EnvironmentManager.getEnvironment()  (defaults to QA)
      ▼
ConfigManager(environment)  ──uses──►  ConfigReader (loads {env}.properties)
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
Kylas Sales Android app (apps/qa/kylas-qa-debug.apk)
      │
      ▼
DriverManager.setDriver(driver)
```

### Teardown (every test, via BaseTest)

```
BaseTest.tearDown()  [@AfterMethod(alwaysRun = true)]
      ▼
DriverManager.removeDriver()
      │
      ▼
 try { driver.quit() }        — closes the Appium session
      │
      ▼
 finally { ThreadLocal.remove() }   — always runs, even if quit() throws
```

### DriverSmokeTest

Asserts a non-null session ID and non-null current package (proving the
app actually launched), and logs session ID, current package, and
current activity. It performs no page/UI interaction — it only exists
to verify the Configuration + Driver layers wire together correctly.

### LoginTest

Drives `LoginPage` directly (constructed with `getDriver()` from
`BaseTest`) — it does not go through `LoginFlow`:

```
LoginTest.verifyUserCanLoginSuccessfully()
      │
      ▼
new LoginPage(getDriver())
      │  enterEmail(validEmail) → enterPassword(validPassword) → clickSignIn()
      ▼
new DashboardPage(getDriver())
      │
      ▼
assert dashboardPage.isDashboardDisplayed()
```

```
LoginTest.verifyUserCannotLoginWithInvalidCredentials()
      │
      ▼
new LoginPage(getDriver())
      │  enterEmail(invalidEmail) → enterPassword(invalidPassword) → clickSignIn()
      ▼
assert loginPage.isInvalidLoginMessageDisplayed()
```

Credentials come from `UserDataManager` → `UserDataReader` →
`testdata/users.properties`.

### DashboardTest

Logs in via `LoginFlow` (unlike `LoginTest` — see
[Architecture.md#known-duplication](Architecture.md#known-duplication)),
then verifies tab navigation:

```
DashboardTest.verifyDashboardTabs()
      │
      ▼
new LoginFlow(getDriver()).loginAsValidUser()
      │  internally: new LoginPage(driver) → enterEmail/enterPassword/clickSignIn
      ▼
DashboardPage
      │
      ▼
assert isDashboardDisplayed()
      │
      ▼
clickDashboardTab() → assert isDashboardTabSelected()
      │
      ▼
clickUpcomingMeetings() → assert isUpcomingMeetingsTabSelected()
      │
      ▼
clickUpcomingTasks() → assert isUpcomingTasksTabSelected()
```

### Failure handling (all tests)

`TestListener` (registered in `suites/testng.xml`) observes every test:

```
Test fails
      │
      ▼
TestListener.onTestFailure(result)
      │  logs failure reason, then
      ▼
ScreenshotUtil.takeScreenshot(DriverManager.getDriver(), testName)
      │
      ▼
target/screenshots/{testName}_{timestamp}.png
```

## Not Yet Implemented

- **Retry on failure** — `RetryAnalyzer` is an empty class; it is not
  wired to any `@Test` (no `retryAnalyzer = ...` attribute is set, and
  `AnnotationTransformer` — which would apply it framework-wide — is
  also empty and unregistered). A failed test fails once, with no retry.
- **Extent/Allure reporting** — `ExtentReportManager` and
  `AllureManager` are both empty classes; `TestListener` does not call
  either. The `allure-testng` Maven dependency still collects raw
  results independently of these classes, but no ExtentReports HTML
  dashboard is produced and no extra Allure enrichment (e.g. attaching
  the failure screenshot to the Allure report itself) happens.
- **iOS execution** — `IOSDriverFactory` throws
  `UnsupportedOperationException`; `pages/ios` is empty; `tests/ios` is
  empty.

## Failure Propagation

```
Config/Driver/Page failure
        │
        ▼
 exceptions/* (Configuration|DriverInitialization)Exception
        │        (PageOperationException is defined but not yet thrown
        │         by any page object)
        ▼
 TestNG marks test failed ──► listeners/TestListener
        │                              │
        ▼                              ▼
 (no retry — RetryAnalyzer is    utils/screenshot/ScreenshotUtil
  empty/unwired)                  (captures + saves screenshot)
```

## Parallel Execution

`DriverManager` holds the active driver per-thread (`ThreadLocal`, not
static/shared), so TestNG's suite-level `parallel="methods"`/`"classes"`
execution would be safe if enabled — each thread would get its own
driver session from its own `AndroidDriverFactory` call. `suites/testng.xml`
does not currently set a `parallel` attribute, so the suite runs
sequentially today.
