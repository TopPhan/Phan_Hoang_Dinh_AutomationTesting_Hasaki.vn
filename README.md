![Java](https://img.shields.io/badge/Java-21-orange?style=flat-square&logo=java)
![Selenium](https://img.shields.io/badge/Selenium-4.x-green?style=flat-square&logo=selenium)
![TestNG](https://img.shields.io/badge/TestNG-7.x-red?style=flat-square)
![Maven](https://img.shields.io/badge/Maven-Project-blue?style=flat-square&logo=apache-maven) <br>
![Regression Test](https://github.com/TopPhan/Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn/actions/workflows/E2E_Purchase.yml/badge.svg) 
# 🌿 [Hasaki.vn](https://hasaki.vn/) — E2E Automation Testing Framework

<img width="1911" height="866" alt="image" src="https://github.com/user-attachments/assets/302da9c5-8e77-4723-9e1a-80cbf7b0c15a" />

---

[![Allure Report](https://img.shields.io/badge/Allure%20Report-View%20Here-ff69b4?style=for-the-badge&logo=allure)](https://TopPhan.github.io/Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn/) 

<img width="1919" height="872" alt="image" src="https://github.com/user-attachments/assets/d675167b-1158-4baa-94b6-5dbd29ff8432" />
---
<img width="1369" height="619" alt="image" src="https://github.com/user-attachments/assets/ac11c96d-823a-4534-a30b-d1029081da65" />
---
<img width="1371" height="617" alt="image" src="https://github.com/user-attachments/assets/b629938d-4bfa-412b-9f22-3b64bf262f3a" />
---

---

## 📋 Table of Contents

- [Project Overview](#-project-overview)
- [Tech Stack](#%EF%B8%8F-technology-stack)
- [Project Architecture](#-project-structure)
- [Flow Diagrams](#-architecture--flow-diagrams)
- [Key Technologies Explained](#-key-technologies-explained)
- [Test Suites & Coverage](#-test-suites--coverage)
- [Data-Driven Testing](#-data-driven-testing)
- [Security — GitHub Secrets](#-security--credential-management)
- [Configuration & How to Run](#-how-to-run-the-project)
- [Allure Report](#-allure-report)
- [CI/CD Pipeline](#%EF%B8%8F-cicd-pipeline--github-actions)

---

## 🌿 Project Overview

**Hasaki.vn Automation Testing** is a production-grade end-to-end test automation framework targeting [Hasaki.vn](https://hasaki.vn/) — Vietnam's leading beauty & skincare e-commerce platform.  
Built with **Selenium WebDriver**, **TestNG**, and the **Page Object Model**, this framework validates the full purchase journey across multiple browsers simultaneously, with automated reporting deployed to GitHub Pages on every CI run.

| Metric | Result |
|---|---|
| 🧪 Total Test Cases | **102 tests** (Regression · Smoke · E2E · PreCondition) |
| ✅ Latest CI Pass Rate | **93.87%** (96 / 102) |
| 🖥️ Parallel Browsers on CI | **Chrome + Microsoft Edge** |
| ⏱️ CI Build Time | **~4d 01h** total history · latest run ~5–6 min |
| 📈 Allure Report | Auto-deployed to **GitHub Pages** — keeps last 20 runs |
| 🤖 CI Platform | **GitHub Actions (Ubuntu Latest)** |
| 🔐 Credential Security | **GitHub Secrets** — zero hardcoded credentials in source |

### 🌟 Key Strengths

- **True parallel execution** — Chrome and Edge run simultaneously using TestNG `parallel="tests" thread-count="2"`, cutting test time nearly in half.
- **Zero-flake resilience** — Custom `ValidateHelper` implements smart fallback: Explicit Wait → Fluent Wait → JavaScript click fallback.
- **Data-driven design** — Test data externalized via Excel (`.xlsx`) and JSON, enabling scenario expansion without code changes.
- **Live Allure reporting** — Every CI run auto-publishes an interactive report to GitHub Pages with 20-run trend history.
- **Full Page Object Model** — Clean separation of locators, page actions, and test logic across 7 page classes.
- **Secure credential management** — All account credentials injected at runtime from GitHub Secrets; no sensitive data stored in source code.
- **Auto screenshot on failure** — TestNG listener captures screenshots automatically on failure, stored in `ScreenShots/`.

---

## 🧱 Project Structure

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
│   │   │       │   ├── CaptureScreenshot.java
│   │   │       │   └── CaptureVideo.java
│   │   │       ├── Helpers/
│   │   │       │   ├── ExcelHelpers.java
│   │   │       │   ├── JsonHelper.java
│   │   │       │   └── ValidateHelper.java        # Core assertion & wait engine
│   │   │       ├── CustomSoftAssert.java
│   │   │       └── PropertiesFile.java            # configs.properties reader
│   │   └── resources/
│   │       ├── configs.properties                 # Global settings (browser, URL, timeouts)
│   │       ├── log4j2.properties
│   │       └── data/
│   │           ├── Data.xlsx
│   │           └── SearchData.json
│   │
│   └── test/
│       └── java/
│           ├── DataProviders/
│           │   └── DataProviders.java
│           ├── pages/
│           │   ├── CartPage.java
│           │   ├── CheckoutPage.java
│           │   ├── LoginPage.java
│           │   ├── MyAccountPage.java
│           │   ├── MyAddressPage.java
│           │   ├── ProductDetailPage.java
│           │   └── SearchPage.java
│           ├── pojoClass/
│           │   ├── AddressModel.java
│           │   ├── LoginModel.java
│           │   ├── ProductModel.java
│           │   └── SearchModel.java
│           ├── resources/
│           │   ├── master/
│           │   │   ├── Master_E2E_Purchase.xml
│           │   │   ├── Master_Regression.xml
│           │   │   └── Master_Smoke.xml
│           │   └── sub_suites/
│           │       ├── E2E_Purchase.xml           # Uses ${CHROME_USER/PASS}, ${EDGE_USER/PASS}
│           │       ├── PreCondition_Setup.xml     # Uses ${CHROME_USER/PASS}, ${EDGE_USER/PASS}
│           │       ├── Regression_Suite.xml       # Uses ${CHROME_USER/PASS}, ${EDGE_USER/PASS}
│           │       ├── Smoke_Suite.xml            # Uses ${CHROME_USER/PASS}, ${EDGE_USER/PASS}
│           │       ├── addSingleProduct.xml       # Uses ${CHROME_USER/PASS}, ${EDGE_USER/PASS}
│           │       ├── emptyCart.xml              # Uses ${CHROME_USER/PASS}
│           │       └── QuickRun.xml               # Uses ${CHROME_USER/PASS}
│           └── testcases/
│               ├── CartTest.java
│               ├── CheckoutTest.java
│               ├── LoginTest.java
│               ├── LogoutTest.java
│               ├── MyAddressTest.java
│               ├── ProductDetailTest.java
│               ├── PurchaseEndToEndTest.java
│               └── SearchTest.java
│
├── allure-results/
├── ScreenShots/
├── logs/
├── pom.xml
└── .gitignore
```

---

## 🔄 Architecture & Flow Diagrams

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
  Push to master / PR / Manual Dispatch
              │
              ▼
      ┌───────────────────┐
      │  Checkout Code    │  (~20s)
      └────────┬──────────┘
               │
      ┌────────▼──────────┐
      │  Set up JDK 21    │  (~2s)
      └────────┬──────────┘
               │
      ┌────────▼──────────┐     ┌─────────────────────┐
      │  Install Chrome   │     │   Install Edge       │
      └────────┬──────────┘     └──────────┬──────────┘
               └──────────┬────────────────┘
                           │
              ┌────────────▼─────────────────────────┐
              │  Inject Secrets (Python3)             │
              │  GitHub Secrets → XML suites          │
              │  CHROME_USER/PASS · EDGE_USER/PASS    │
              └────────────┬─────────────────────────┘
                           │
              ┌────────────▼────────────────┐
              │  Run Maven Tests (Headless)  │  (~5–6 min)
              │  Chrome + Edge in parallel   │
              └────────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │    Upload Artifacts          │
              │  surefire · ScreenShots      │
              │  allure-results              │
              └────────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │   Allure Report Action       │
              │   Generate HTML report       │
              └────────────┬────────────────┘
                           │
              ┌────────────▼────────────────┐
              │  Deploy to GitHub Pages      │
              │  (gh-pages branch)           │
              └─────────────────────────────┘
                           │
              ✅ Report live at topphan.github.io/...
```

### E2E Purchase Test Flow

```
  [PreCondition]
  Clear Cart → Remove saved addresses → Logout
        │
        ▼
  [Login]
  Authenticate (Chrome & Edge simultaneously in parallel)
        │
        ▼
  [Product Detail]
  Search product → Open detail page → Add to Cart
        │
        ▼
  [Cart]
  Verify product name, price, quantity
  Math audit: verify subtotals sum to total
        │
        ▼
  [Checkout]
  Proceed to checkout → Deep comparison (Name, Price, SKU)
  Cart data == Checkout data → zero mismatch
        │
        ▼
  [Assertion]
  ✅ Data integrity verified
  ✅ "Đặt hàng" button enabled & visible
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
| Allure | via GitHub Action | Interactive test reporting |
| Maven | 3.x | Build & dependency management |
| GitHub Actions | ubuntu-latest | CI/CD pipeline |
| Monte Screen Recorder | 0.7.7.0 | Screen video capture |

---

## 🔐 Security — Credential Management

All test account credentials are managed through **GitHub Actions Secrets** — no email or password is ever stored in source code.

### How it works

```
GitHub Secrets (encrypted)
  CHROME_USER  ──┐
  CHROME_PASS  ──┤  GitHub Actions injects at runtime
  EDGE_USER    ──┤        │
  EDGE_PASS    ──┘        ▼
                  Python3 replace() in workflow
                  XML placeholders → real values
                  (only in runner memory, never committed)
```

All 7 XML suite files store credentials as **placeholders**:

```xml
<parameter name="email"    value="${CHROME_USER}"/>
<parameter name="password" value="${CHROME_PASS}"/>
```

The CI workflow injects real values at runtime using Python (not `sed` — Python handles special characters like `@`, `!`, `/` safely):

```yaml
- name: Inject Secrets into XML suites
  env:
    CHROME_USER: ${{ secrets.CHROME_USER }}
    CHROME_PASS: ${{ secrets.CHROME_PASS }}
    EDGE_USER:   ${{ secrets.EDGE_USER }}
    EDGE_PASS:   ${{ secrets.EDGE_PASS }}
  run: |
    python3 - << 'PYEOF'
    import os, glob
    replacements = {
        "${CHROME_USER}": os.environ["CHROME_USER"],
        "${CHROME_PASS}": os.environ["CHROME_PASS"],
        "${EDGE_USER}":   os.environ["EDGE_USER"],
        "${EDGE_PASS}":   os.environ["EDGE_PASS"],
    }
    for path in glob.glob("src/test/java/resources/sub_suites/*.xml"):
        content = open(path).read()
        for k, v in replacements.items():
            content = content.replace(k, v)
        open(path, "w").write(content)
        print(f"Injected: {path}")
    PYEOF
```

### Setting up GitHub Secrets

1. Go to your repository → **Settings → Secrets and variables → Actions**
2. Click **New repository secret** and add:

| Secret Name | Description |
|---|---|
| `CHROME_USER` | Email for Chrome browser account |
| `CHROME_PASS` | Password for Chrome browser account |
| `EDGE_USER` | Email for Edge browser account |
| `EDGE_PASS` | Password for Edge browser account |

### Running locally

Set environment variables before running tests:

**Windows (Command Prompt as Admin):**
```cmd
setx CHROME_USER "your_email@gmail.com"
setx CHROME_PASS "your_password"
setx EDGE_USER   "your_edge_email@gmail.com"
setx EDGE_PASS   "your_edge_password"
```
> Restart IntelliJ IDEA after `setx` for changes to take effect.

**macOS / Linux:**
```bash
export CHROME_USER="your_email@gmail.com"
export CHROME_PASS="your_password"
export EDGE_USER="your_edge_email@gmail.com"
export EDGE_PASS="your_edge_password"
```

---

## ⚙️ Configuration & How to Run the Project

### Prerequisites

- Java 21+ installed (`java -version`)
- Maven 3.6+ installed (`mvn -version`)
- Chrome or Edge browser installed
- Internet access to [https://hasaki.vn/](https://hasaki.vn/)
- Environment variables `CHROME_USER` / `CHROME_PASS` set (see Security section above)

### 1. Clone the Repository

```bash
git clone https://github.com/topphan/Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn.git
cd Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn
```

### 2. Configure Settings (Optional)

Edit `src/main/resources/configs.properties`:

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

**Run full E2E Purchase flow (default):**
```bash
mvn clean test
```

**Run with explicit suite:**
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

**Run headless:**
```bash
mvn clean test -Dheadless=true
```

### 4. View Allure Report Locally

```bash
# Generate and open interactive report
allure serve allure-results

# Or generate static report folder
allure generate allure-results --clean -o allure-report
```

---

## 🔬 Key Technologies Explained

### Selenium WebDriver 4.38.0
Core browser automation engine. Uses Selenium's W3C-compliant API with WebDriverManager for auto driver binary management — no manual chromedriver setup required.

### TestNG 7.10.2 — Parallel Execution
`parallel="tests"` and `thread-count="2"` in suite XML runs Chrome and Edge simultaneously. Each thread gets its own isolated `WebDriver` via `ThreadLocal`, preventing cross-browser contamination.

### Page Object Model (POM)
Each Hasaki.vn page has a dedicated Java class (`CartPage.java`, `CheckoutPage.java`, etc.), cleanly separating UI locators from test logic. New test scenarios require no locator changes.

### ValidateHelper — Smart Wait & Assertion Engine
Layered waiting strategy: Explicit Wait → Fluent Wait with polling → JavaScript click fallback. Eliminates flakiness from dynamic page rendering without `Thread.sleep()`.

### Log4j2 — Structured Logging
Timestamped, browser-tagged logs (`[CHROME]`, `[MSEDGE]`) per thread for effortless parallel debugging.

### Allure Report
Interactive test reports with step breakdowns, timeline views, pass/fail charts, failure screenshots, and 20-run trend history — auto-published to GitHub Pages on every CI push.

---

## 📊 Data-Driven Testing

Test data is fully externalized from test code following the **Data-Driven Testing (DDT)** pattern.

**Excel (`Data.xlsx`) via Apache POI:**
Tabular data for login, product details, address entries — mapped to POJO models via `ExcelHelpers.java`. Supports an `Execute` flag (Y/N) per row to enable/disable test cases without code changes.

**JSON (`SearchData.json`) via JsonHelper:**
Lightweight search keyword datasets deserialized into `SearchModel` objects.

**TestNG `@DataProvider`:**
`DataProviders.java` bridges all data sources to `@Test` methods. Each row becomes an independent test case with its own Allure result entry.

```
                    ┌─────────────────┐
                    │  DataProviders  │
                    └────────┬────────┘
          ┌─────────────────┼─────────────────┐
          ▼                 ▼                 ▼
   ┌─────────────┐  ┌─────────────┐  ┌─────────────┐
   │  Excel      │  │   JSON      │  │  Hardcode   │
   │  Data.xlsx  │  │ SearchData  │  │  Object[][] │
   │  (Apache    │  │   .json     │  │             │
   │   POI)      │  │   (Gson)    │  │             │
   └──────┬──────┘  └──────┬──────┘  └──────┬──────┘
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

## 🧪 Test Suites & Coverage

| Module | Test Class | Scenarios |
|---|---|---|
| 🔑 Login | `LoginTest.java` | Valid login, invalid credentials, empty fields, UI layout |
| 🚪 Logout | `LogoutTest.java` | Successful logout, session termination |
| 🔍 Search | `SearchTest.java` | Keyword search, multi-keyword, accuracy rate, price filter |
| 📦 Product Detail | `ProductDetailTest.java` | Product info, add-to-cart |
| 🛒 Cart | `CartTest.java` | Add items, delete items, clear cart, math audit |
| 📋 Checkout | `CheckoutTest.java` | Item verification, total calculation, Cart vs Checkout comparison |
| 📍 My Address | `MyAddressTest.java` | Add, edit, delete address |
| 🛍️ E2E Purchase | `PurchaseEndToEndTest.java` | Full flow: Login → Search → Add → Cart → Checkout |

### XML Suite Structure

```
Master Suites
├── Master_Regression.xml
│   ├── PreCondition_Setup.xml   ← clear cart, remove addresses, logout
│   └── Regression_Suite.xml     ← full regression (Chrome + Edge parallel)
│
├── Master_Smoke.xml
│   ├── PreCondition_Setup.xml
│   └── Smoke_Suite.xml          ← critical path smoke (Chrome + Edge parallel)
│
└── Master_E2E_Purchase.xml
    ├── PreCondition_Setup.xml
    └── E2E_Purchase.xml         ← full purchase journey (Chrome + Edge parallel)

Sub Suites (standalone / debug)
├── QuickRun.xml                 ← single test debug
├── addSingleProduct.xml         ← add one product debug
└── emptyCart.xml                ← clear cart utility
```

---

## 📈 Allure Report

**Report URL:** [https://topphan.github.io/Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn](https://topphan.github.io/Phan_Hoang_Dinh_AutomationTesting_Hasaki.vn/61/index.html)

**Latest result (2/28/2026):**

| | |
|---|---|
| Total test cases | **102** |
| Pass rate | **93.87%** |
| Suites | Regression · Purchase Flow · Environment Cleanup · Smoke |
| Executor | GitHub Actions |
| Trend | Stable green across all historical runs |

**What the report shows:**
- Pass / Fail / Skip summary per test
- Step-by-step breakdown per test case
- Timeline view — Chrome and Edge threads in parallel
- Historical trend chart (last 20 CI runs)
- Failure screenshots attached inline
- Environment metadata: Browser, Java version, OS, Tester

---

## ⚙️ CI/CD Pipeline — GitHub Actions

Pipeline defined in `.github/workflows/E2E_Purchase.yml`. Triggers on:
- **Push** to `master` / `main`
- **Pull Request** to `master` / `main`
- **Manual dispatch** via GitHub UI

```yaml
jobs:
  E2E_Purchase_Flow:
    runs-on: ubuntu-latest
    steps:
      - Checkout code
      - Set up JDK 21
      - Install Chrome
      - Install Edge
      - Inject Secrets into XML suites   # ← Python3 replaces ${placeholders}
      - Run Maven Tests (Headless)        # ← Chrome + Edge parallel
      - Upload Artifacts
      - Allure Report Action
      - Deploy to GitHub Pages
```

---

## 📁 Output Artifacts

| Path | Content |
|---|---|
| `allure-results/` | Raw JSON result files for Allure |
| `ScreenShots/` | PNG failure screenshots (auto-named with timestamp) |
| `logs/app-properties.log` | Full Log4j2 runtime log, browser-tagged per thread |
| `target/surefire-reports/` | Maven Surefire XML & HTML reports |

---

## 👤 Author

**Phan Hoang Dinh** — Automation Test Engineer  
[GitHub Profile](https://github.com/topphan)

---

*This framework targets the live [Hasaki.vn](https://hasaki.vn/) website for portfolio demonstration purposes.*

