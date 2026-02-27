# 🌿 Hasaki.vn — End-to-End Automation Testing Framework
<img width="1914" height="872" alt="image" src="https://github.com/user-attachments/assets/14729820-b142-4ea0-9ff0-0b5f79153642" />






---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Tech Stack](#%EF%B8%8F-technology-stack)
- [Project Architecture](#-project-structure)
- [Flow Diagrams](#-architecture--flow-diagrams)
- [Key Technologies Explained](#-key-technologies-explained)
- [Test Suites & Coverage](#-test-suites--coverage)
- [Data-Driven Testing](#-data-driven-testing)
- [Configuration & How to Run](#-how-to-run-the-project)
- [Allure Report](#-allure-report)
- [CI/CD Pipeline](#%EF%B8%8F-cicd-pipeline--github-actions)

---

## 🌿 Project Overview

- **Hasaki.vn Automation Testing** is a production-grade end-to-end test automation framework targeting [Hasaki.vn](https://hasaki.vn/) — Vietnam's leading beauty & skincare e-commerce platform. 
- Built with **Selenium WebDriver**, **TestNG**, and the **Page Object Model**, this framework validates the full purchase journey across multiple browsers simultaneously, with automated reporting deployed to GitHub Pages on every CI run.

| Metric | Result |
|---|---|
| 🧪 Total Test Cases Written | **96 tests** (Regression: 54 · Smoke: 32 · E2E: 10) |
| 🤖 Tests Run on CI (GitHub Actions) | **10 E2E tests** — full purchase flow, both browsers |
| ❌ CI Failures | **0 / 10** — 100% pass rate |
| 🖥️ Tests Run Locally (Offline) | **86 tests** — Regression (54) + Smoke (32) |
| ⏱️ CI Build Time | **~5m 09s** (Chrome + Edge in parallel) |
| 🌐 Browsers Tested In Parallel | **Chrome + Microsoft Edge** |
| 📈 Allure Report | Auto-deployed to **GitHub Pages** after every CI run |
| 🤖 CI Platform | **GitHub Actions (Ubuntu Latest)** |

### 🌟 Key Strengths

- **True parallel execution** — Chrome and Edge run simultaneously using TestNG multi-thread configuration, cutting test time nearly in half.
- **Zero-flake resilience** — Custom `ValidateHelper` implements smart fallback strategies: explicit wait → fluent wait → JavaScript click fallback, resulting in 0 failures across all 10 E2E tests in CI.
- **Data-driven design** — Test data is externalized via Excel (`.xlsx`) and JSON files, enabling test case expansion without code changes.
- **Live Allure reporting** — Every CI run auto-publishes a rich, interactive test report to GitHub Pages, preserving the last 20 historical runs for trend analysis.
- **Full Page Object Model** — Clean separation of UI locators, page actions, and test logic across 7 distinct page classes.
- **Structured logging** — Log4j2 captures timestamped, browser-tagged logs for every step, making debugging effortless.
- **Auto screenshot on failure** — The custom TestNG listener captures screenshots automatically on test failure, stored in `ScreenShots/`.

---

## 🧱 Project Structure
<a name="-project-structure"></a>

```
Hasaki.vn/
├── .github/
│   └── workflows/
│       └── E2E_Purchase.yml          # GitHub Actions CI pipeline
│
├── src/
│   ├── main/
│   │   ├── java/com/
│   │   │   ├── bases/
│   │   │   │   └── multipleThread_baseSetup.java  # Thread-safe WebDriver setup
│   │   │   ├── listener/
│   │   │   │   └── Standard_Mode_Listener.java    # TestNG listener (screenshot, logging)
│   │   │   ├── log/
│   │   │   │   └── logTest.java                   # Log4j2 logger wrapper
│   │   │   └── utility/
│   │   │       ├── Capture/
│   │   │       │   ├── CaptureScreenshot.java     # Auto screenshot utility
│   │   │       │   └── CaptureVideo.java          # Screen recording utility
│   │   │       ├── Helpers/
│   │   │       │   ├── ExcelHelpers.java          # Apache POI Excel reader
│   │   │       │   ├── JsonHelper.java            # JSON data reader
│   │   │       │   └── ValidateHelper.java        # Core assertion & wait engine
│   │   │       ├── CustomSoftAssert.java
│   │   │       └── PropertiesFile.java            # configs.properties reader
│   │   └── resources/
│   │       ├── configs.properties                 # Global settings (browser, URL, timeouts)
│   │       ├── log4j2.properties                  # Logging configuration
│   │       └── data/
│   │           ├── Data.xlsx                      # Test data (Excel)
│   │           └── SearchData.json                # Search test data (JSON)
│   │
│   └── test/
│       ├── java/
│       │   ├── DataProviders/
│       │   │   └── DataProviders.java             # TestNG @DataProvider methods
│       │   ├── pages/                             # Page Object classes
│       │   │   ├── CartPage.java
│       │   │   ├── CheckoutPage.java
│       │   │   ├── LoginPage.java
│       │   │   ├── MyAccountPage.java
│       │   │   ├── MyAddressPage.java
│       │   │   ├── ProductDetailPage.java
│       │   │   └── SearchPage.java
│       │   ├── pojoClass/                         # Data model classes
│       │   │   ├── AddressModel.java
│       │   │   ├── LoginModel.java
│       │   │   ├── ProductModel.java
│       │   │   └── SearchModel.java
│       │   ├── resources/
│       │   │   ├── master/                        # Master suite XML files
│       │   │   │   ├── Master_E2E_Purchase.xml
│       │   │   │   ├── Master_Regression.xml
│       │   │   │   └── Master_Smoke.xml
│       │   │   └── sub_suites/                    # Modular sub-suite XML files
│       │   │       ├── E2E_Purchase.xml
│       │   │       ├── addSingleProduct.xml
│       │   │       ├── emptyCart.xml
│       │   │       ├── PreCondition_Setup.xml
│       │   │       ├── Regression_Suite.xml
│       │   │       ├── Smoke_Suite.xml
│       │   │       └── QuickRun.xml
│       │   └── testcases/                         # Test classes
│       │       ├── CartTest.java
│       │       ├── CheckoutTest.java
│       │       ├── LoginTest.java
│       │       ├── LogoutTest.java
│       │       ├── MyAddressTest.java
│       │       ├── ProductDetailTest.java
│       │       ├── PurchaseEndToEndTest.java
│       │       └── SearchTest.java
│       └── (resources mirrored above)
│
├── allure-results/                                # Raw Allure JSON results
├── logs/                                          # Log4j2 runtime logs
├── ScreenShots/                                   # Failure screenshots
├── pom.xml                                        # Maven build config
└── .gitignore
```

---

## 🔄 Architecture & Flow Diagrams
<a name="-architecture--flow-diagrams"></a>

### Page Object Model Architecture

```
┌─────────────────────────────────────────────────────────┐
│                    TEST EXECUTION LAYER                 │
│  CartTest │ LoginTest │ SearchTest │ PurchaseE2ETest ... │
└────────────────────────┬────────────────────────────────┘
                         │ calls
┌────────────────────────▼────────────────────────────────┐
│                     PAGE OBJECT LAYER                   │
│  LoginPage │ CartPage │ CheckoutPage │ SearchPage ...   │
└────────────────────────┬────────────────────────────────┘
                         │ uses
┌────────────────────────▼────────────────────────────────┐
│                    UTILITY / BASE LAYER                 │
│  ValidateHelper │ multipleThread_baseSetup │ Listeners  │
└────────────────────────┬────────────────────────────────┘
                         │ drives
┌────────────────────────▼────────────────────────────────┐
│                  SELENIUM WEBDRIVER LAYER               │
│            Chrome (Thread-1) │ Edge (Thread-2)          │
└─────────────────────────────────────────────────────────┘
```

### CI/CD Pipeline Flow

```
  Push to develop/github_action  ──► GitHub Actions Triggered
              │
              ▼
      ┌───────────────────┐
      │  Checkout Code    │  (20s)
      └────────┬──────────┘
               │
      ┌────────▼──────────┐
      │  Set up JDK 21    │  (2s)
      └────────┬──────────┘
               │
      ┌────────▼──────────┐     ┌─────────────────────┐
      │  Install Chrome   │     │   Install Edge       │
      └────────┬──────────┘     └──────────┬──────────┘
               └──────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │  Run Maven Tests (Headless)  │  (5m 12s)
              │  Chrome + Edge in parallel   │
              └────────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │    Upload Artifacts          │  (1s)
              │  (surefire, screenshots,     │
              │   allure-results)            │
              └────────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │   Allure Report Action       │  (3s)
              │   Generate HTML report       │
              └────────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │  Deploy to GitHub Pages      │  (3s)
              │  (gh-pages branch)           │
              └─────────────────────────────┘
                           │
              ✅ Report live at topphan.github.io/...
```

### E2E Purchase Test Flow

```
  [PreCondition]
  Clear Cart → Logout
        │
        ▼
  [Login]
  Authenticate user account (Chrome / Edge in parallel)
        │
        ▼
  [Product Detail]
  Search product → Open detail page → Add to Cart
        │
        ▼
  [Cart]
  Verify product name, price, quantity in cart
        │
        ▼
  [Checkout]
  Proceed to checkout → Verify all items match cart
        │
        ▼
  [Assertion]
  ✅ Data integrity: Cart == Checkout
  ✅ "Đặt hàng" button is enabled & visible
```

---

## 🛠️ Technology Stack

| Tool | Version | Purpose |
|---|---|---|
| Java | 21 (Temurin) | Core language |
| Selenium WebDriver | 4.38.0 | Browser automation |
| TestNG | 7.10.2 | Test runner & parallel execution |
| WebDriverManager | 6.3.2 | Auto driver binary management |
| Apache POI | 5.4.1 | Excel data reading |
| Log4j2 | 2.25.3 | Structured logging |
| Allure | (via GitHub Action) | Interactive test reporting |
| Maven | 3.x | Build & dependency management |
| GitHub Actions | ubuntu-latest | CI/CD pipeline |
| Monte Screen Recorder | 0.7.7.0 | Screen video capture |

---

## ⚙️ Configuration & How to Run the Project
<a name="-how-to-run-the-project"></a>

### Prerequisites

- Java 21+ installed (`java -version`)
- Maven 3.6+ installed (`mvn -version`)
- Chrome or Edge browser installed
- Internet access to [https://hasaki.vn/](https://hasaki.vn/)

### 1. Clone the Repository

```bash
git clone https://github.com/topphan/Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn.git
cd Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn
```

### 2. Configure Settings (Optional)

Edit `src/main/resources/configs.properties` to adjust:

```properties
# Browser selection: chrome or edge
browser=chrome

# Headless mode (recommended for CI)
browser.headless=true

# Target URL
url=https://hasaki.vn/

# Timeouts (seconds)
timeout=10
```

### 3. Run Test Suites

**Run the full E2E Purchase flow (default):**
```bash
mvn clean test
```

**Run E2E Purchase with explicit suite:**
```bash
mvn clean test -DsuiteXmlFile=src/test/java/resources/master/Master_E2E_Purchase.xml
```

**Run Regression Suite:**
```bash
mvn clean test -DsuiteXmlFile=src/test/java/resources/master/Master_Regression.xml
```

**Run Smoke Suite:**
```bash
mvn clean test -DsuiteXmlFile=src/test/java/resources/master/Master_Smoke.xml
```

**Run in headless mode (no browser window):**
```bash
mvn clean test -Dheadless=true
```

**Run on a specific browser:**
```bash
mvn clean test -Dbrowser=edge
```

### 4. View Allure Report Locally

```bash
# Install Allure CLI (one-time setup)
# macOS: brew install allure
# Windows: scoop install allure

# Generate and open report after test run
allure serve allure-results
```

---

---

## 🔬 Key Technologies Explained

### Selenium WebDriver 4.38.0
The core browser automation engine. This project uses Selenium's latest W3C-compliant API to interact with UI elements, handle dynamic content, and support both Chrome and Edge via WebDriverManager — which auto-downloads and manages browser driver binaries without manual setup.

### TestNG 7.10.2 — Parallel Execution
TestNG drives test orchestration. The framework leverages `parallel="tests"` and `thread-count="2"` in the suite XML to run Chrome and Edge simultaneously, cutting overall test runtime nearly in half. Each thread gets its own isolated `WebDriver` instance via `ThreadLocal`, preventing cross-browser contamination.

### Page Object Model (POM)
Every page of the Hasaki.vn site is represented by a dedicated Java class (e.g., `CartPage.java`, `CheckoutPage.java`). This cleanly separates UI locators and page actions from test logic, making tests readable and maintainable. Adding a new test scenario requires no locator changes — only the test class needs updating.

### ValidateHelper — Smart Wait & Assertion Engine
The custom `ValidateHelper.java` class is the reliability backbone of the framework. Instead of hard `Thread.sleep()` calls, it implements a layered waiting strategy: Explicit Wait → Fluent Wait with polling → JavaScript click fallback. This approach eliminates flakiness caused by slow network responses or dynamic page rendering.

### Log4j2 — Structured Logging
Every action is logged with a timestamp, browser tag (e.g., `[CHROME]`, `[MSEDGE]`), thread name, and class source. This makes post-run debugging trivial — you can trace exactly what each browser thread did, in order, even during parallel runs.

### Allure Report
Allure transforms raw JSON test results into a rich interactive report with timeline views, test step breakdowns, pass/fail charts, and historical trend graphs. The framework keeps the last 20 run histories on GitHub Pages for regression trend analysis.

---

## 📊 Data-Driven Testing

Test data is fully externalized from test code, following the **Data-Driven Testing (DDT)** pattern. This allows new test scenarios to be added without modifying any Java source files.

**Excel (`Data.xlsx`) via Apache POI:**
Used for complex, tabular test data such as login credentials, product details, and address entries. `ExcelHelpers.java` reads rows and maps them to POJO model objects (`LoginModel`, `ProductModel`, `AddressModel`).

**JSON (`SearchData.json`) via JsonHelper:**
Used for lightweight, structured search keyword datasets. `JsonHelper.java` deserializes the JSON into `SearchModel` objects consumed by `@DataProvider` methods.

**TestNG `@DataProvider`:**
`DataProviders.java` acts as the bridge, feeding data from both Excel and JSON into the `@Test` methods at runtime. Each test row becomes an independent test case with its own pass/fail result in the Allure report.

```
                    ┌─────────────────┐
                    │  DataProviders  │
                    └────────┬────────┘
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
   │  Excel      │  │   JSON      │  │  Hardcode   │
   │  Data.xlsx  │  │SearchData   │  │  Object[][] │
   │  (Apache    │  │  .json      │  │             │
   │   POI)      │  │  (Gson)     │  │             │
   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
          │                 │                 │
          └─────────────────▼─────────────────┘
                    ┌─────────────────┐
                    │  POJO Models    │
                    │ LoginModel      │
                    │ SearchModel     │
                    │ ProductModel    │
                    │ AddressModel    │
                    └────────┬────────┘
                             │
                    ┌────────▼────────┐
                    │  @Test methods  │
                    └─────────────────┘
```

---

## 📈 Allure Report

The Allure report is automatically generated and deployed to GitHub Pages after every successful CI run.

**Report URL:** `https://topphan.github.io/Phan_Hoang_Di.../`

**What the report shows:**
- ✅ Pass / ❌ Fail / ⏭️ Skip summary per test
- Step-by-step breakdown of each test case
- Timeline view showing Chrome and Edge threads running in parallel
- Historical trend chart across the last 20 CI runs
- Attached screenshots on failure

**Generate report locally:**
```bash
# After running tests
allure serve allure-results

# Or generate a static report folder
allure generate allure-results --clean -o allure-report
```

---

## ⚙️ CI/CD Pipeline — GitHub Actions
<a name="-cicd-pipeline--github-actions"></a>

The pipeline is defined in `.github/workflows/E2E_Purchase.yml` and triggers on:

- **Push** to `develop/github_action` branch
- **Pull Request** to `main` or `master`
- **Manual dispatch** via GitHub UI (`workflow_dispatch`)

## 🧪 Test Suites & Coverage

| Module | Test Class | Test Scenarios |
|---|---|---|
| 🔑 Login | `LoginTest.java` | Valid login, invalid credentials, empty fields |
| 🚪 Logout | `LogoutTest.java` | Successful logout, session termination |
| 🔍 Search | `SearchTest.java` | Keyword search, multi-keyword, accuracy verification |
| 📦 Product Detail | `ProductDetailTest.java` | Product info, add-to-cart from detail page |
| 🛒 Cart | `CartTest.java` | Add items, delete items, clear cart |
| 📋 Checkout | `CheckoutTest.java` | Item verification, total calculation |
| 📍 My Address | `MyAddressTest.java` | Add address, edit address, delete address |
| 🛍️ E2E Purchase | `PurchaseEndToEndTest.java` | Full flow: Login → Search → Add → Cart → Checkout |

---
The XML suite system is organized into two levels (master → sub:

```
Master Suites
├── Master_Regression.xml
│   ├── PreCondition_Setup.xml   ← setup conditions (login first, clear cart...)
│   └── Regression_Suite.xml     ← run the full regression test suite
│
├── Master_Smoke.xml
│   ├── PreCondition_Setup.xml
│   └── Smoke_Suite.xml         ← quickly run the most critical tests
│
└── Master_E2E_Purchase.xml
    ├── PreCondition_Setup.xml
    └── E2E_Purchase.xml        ← run the end-to-end purchase flow

Sub Suites (chạy độc lập)
├── QuickRun.xml                ← debug nhanh 1 test
├── addSingleProduct.xml        ← quickly debug a single test
└── emptyCart.xml               ← completely clear the shopping cart
```


## 📁 Output Artifacts

After a test run, the following artifacts are generated:

| Path | Content |
|---|---|
| `allure-results/` | Raw JSON result files for Allure report generation |
| `ScreenShots/` | PNG screenshots captured on test failure (auto-named with timestamp) |
| `logs/app-properties.log` | Full Log4j2 runtime log with timestamped, browser-tagged entries |
| `target/surefire-reports/` | Standard Maven Surefire XML & HTML reports |

---

## 👤 Author

**Phan Hoang Dinh**
Automation Test Engineer
[GitHub Profile](https://github.com/topphan)

---

*This framework is designed for portfolio demonstration purposes, targeting the live [Hasaki.vn](https://hasaki.vn/) website.*
