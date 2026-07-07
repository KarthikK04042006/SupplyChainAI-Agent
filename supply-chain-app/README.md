# Supply Chain AI Agent

A commercial-grade Supply Chain AI SaaS application built with **Spring Boot 3.2.5**, **Thymeleaf**, **Spring Security**, and an **H2** embedded database.  It ships with a fully functional glassmorphism UI, Chart.js dashboards, AI-driven predictions, PDF/Excel exports, CSV ingestion, and a complete admin panel.

---

## 🚀 Getting Started

### Prerequisites
- **Java 19** (GraalVM 22.3.1 or OpenJDK 19)
- **Maven 3.8+**

### Build & Run

```bash
cd supply-chain-app
mvn clean package -DskipTests
java -jar target/supply-chain-ai-agent-1.0.0.jar
```

The server starts on **port 8099** by default.

### Demo Credentials

| Role  | Username | Password  |
|-------|----------|-----------|
| Admin | `admin`  | `admin123` |
| User  | `demo`   | `demo123`  |

---

## 📦 Features

| Feature | Description |
|---------|-------------|
| **Auth** | Login, Register, Remember Me, session management, role-based access (ADMIN / USER) |
| **Executive Dashboard** | 4 KPI cards (orders, revenue, profit, avg order value), Sales Trend, Region Doughnut, Category Bar, Supply Chain Radar |
| **CSV Upload** | Drag-and-drop file upload with progress bar, column guide, supports the full Superstore CSV schema |
| **Analytics Center** | Monthly compare chart, segment pie, category profit chart, regional horizontal bar, searchable products table |
| **AI Predictions** | Demand forecast, revenue prediction, risk gauge, confidence score, inventory alerts, smart recommendations |
| **Report Center** | One-click PDF (iText 7) and Excel (Apache POI) download, browser print |
| **Upload History** | Searchable/filterable history of all CSV uploads with row counts and status |
| **User Profile** | Edit first name, last name, email; change password |
| **Settings** | Dark mode toggle, accent colour picker, notification preferences — all persisted to `localStorage` |
| **Admin Panel** | User management (enable/disable, delete), system stats (total users, uploads, records) |
| **Error Pages** | Custom 404 and 500 pages |
| **Dark Mode** | Full dark mode across all pages via CSS custom properties |
| **Responsive** | Mobile-first layout with collapsible sidebar |

---

## 🗂️ Project Structure

```
supply-chain-app/
├── src/main/java/com/supplychain/ai/
│   ├── config/          SecurityConfig, DataInitializer
│   ├── controller/      Auth, Dashboard, Analytics, AI, Reports, Upload, History, Profile, Settings, Admin
│   ├── dto/             AnalyticsSummaryDto, UserRegistrationDto, ProfileUpdateDto
│   ├── exception/       GlobalExceptionHandler
│   ├── model/           User, Role, SupplyChainData, UploadHistory
│   ├── repository/      User, Role, SupplyChainData, UploadHistory repositories
│   └── service/         Analytics, AI, CSV, Report, User, CustomUserDetails services
├── src/main/resources/
│   ├── templates/       Thymeleaf HTML (layout + all pages)
│   └── static/
│       ├── css/         style.css, auth.css, animations.css
│       └── js/          main.js (Chart.js, drag-drop, search, dark mode)
└── pom.xml
```

---

## 🔒 Security

- **Spring Security** with BCrypt password encoding
- CSRF protection enabled (H2 console excluded only)
- Role-based access: `/admin/**` requires `ROLE_ADMIN`
- H2 console restricted to `ROLE_ADMIN`
- Admin mutations (toggle/delete user) use **POST** — not GET — to prevent CSRF via links
- Session management with 30-minute timeout and single-session enforcement

---

## 🗄️ Database

Uses **H2 file-backed** embedded database (`jdbc:h2:file:./data/supplychain`).  Schema is auto-managed by Hibernate (`ddl-auto=update`).  Default users are seeded by `DataInitializer` on first start.

To browse the database: log in as `admin` then visit `/h2-console`.

---

## 📊 CSV Format

Upload any CSV with the following columns (Superstore-compatible):

```
Order ID, Order Date, Ship Date, Ship Mode, Customer ID, Customer Name,
Segment, Country, City, State, Region, Category, Sub-Category,
Product Name, Sales, Quantity, Discount, Profit, Shipping Cost, Order Priority
```

---

## 🛠️ Configuration

All settings live in `src/main/resources/application.properties`:

| Property | Default | Description |
|----------|---------|-------------|
| `server.port` | `8099` | HTTP port |
| `spring.datasource.url` | `jdbc:h2:file:./data/supplychain` | H2 file path |
| `spring.h2.console.enabled` | `true` | Enable H2 browser console |
| `app.upload.dir` | `uploads/` | Directory for uploaded CSV files |
| `spring.servlet.multipart.max-file-size` | `10MB` | Max upload size |

---

## 📚 Tech Stack

| Layer | Technology |
|-------|-----------|
| Backend | Spring Boot 3.2.5, Spring Security, Spring Data JPA |
| Templating | Thymeleaf 3 + thymeleaf-extras-springsecurity6 |
| Database | H2 (embedded, file-backed) |
| CSV | OpenCSV 5.9 |
| PDF | iText 7.2.5 |
| Excel | Apache POI 5.2.5 |
| Frontend | Bootstrap 5.3, Chart.js 4, Font Awesome 6, Inter font |
| Build | Maven 3 |
