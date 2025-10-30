# Online Product Trading Website

[![Build](https://img.shields.io/badge/build-passing-brightgreen)](#)
[![License](https://img.shields.io/badge/license-MIT-blue)](#)
[![Java](https://img.shields.io/badge/java-21-orange)](#)
[![Vue](https://img.shields.io/badge/vue-3-brightgreen)](#)

> A secure, interactive online marketplace for buying and selling products — with an AI-powered price trend advisory that helps users decide whether to **buy now** or **wait**.

---

## Table of Contents

- [Project Overview](#project-overview)
- [Key Features](#key-features)
- [AI Price Advisory](#ai-price-advisory)
- [Tech Stack](#tech-stack)
- [Architecture & Project Layout](#architecture--project-layout)
- [Getting Started (Dev)](#getting-started-dev)
  - [Backend](#backend)
  - [Frontend](#frontend)
  - [AI Service (optional)](#ai-service-optional)
- [Configuration](#configuration)
- [API & Database Notes](#api--database-notes)
- [Contributing](#contributing)
- [License & Contact](#license--contact)

---

## Project Overview

This project implements a full-featured online product trading website with the following capabilities:

- Product listing and management
- User accounts and JWT authentication
- Order lifecycle management (1 = Pending Payment, 2 = Paid, 3 = Cancelled)
- Interactive features: comments, likes, favourites
- Operation logging for monitoring and audits
- Optional AI module: analyzes historical prices and gives **Buy/Wait** suggestions

---

## Key Features

- **User management** (register / login / profile)
- **Product CRUD** with images, categories, and search
- **Order system**: create orders, track status, refunds
- **Interactions**: comments, likes, favourites, notifications to sellers
- **Operation logs**: record user/admin actions, exportable reports
- **AI price advice**: recommend buy/wait based on predicted short-term trend
---

## AI Price Advisory
The AI module (optional microservice) provides short-term directional prediction for product categories or specific SKUs and returns:
- `trend`: `UP` | `DOWN` | `STABLE`
- `recommendation`: `BUY_NOW` | `WAIT` | `NO_DECISION`
- `confidence`: numeric score (0.0 - 1.0)
- `rationale`: short human-readable explanation

**How it works (high level)**
1. Collect historical price series and relevant features (moving averages, listing counts, seasonality).
2. Train a model (e.g., LSTM / Transformer / tree-based ensemble) offline.
3. Serve predictions through a REST endpoint.
4. Feed user actions / realized prices back for retraining to improve accuracy.
**Advice presentation**: always show confidence and a brief rationale to avoid overclaiming.
---
## Tech Stack
- **Backend**: Java, Spring Boot 3, MyBatis
- **Database**: PostgreSQL
- **Auth**: JWT
- **Frontend**: Vue 3 (Vite), Axios, Element Plus / Tailwind CSS
- **AI (optional)**: Python (FastAPI / Flask), TensorFlow / scikit-learn
- **Cache / Queue**: Redis (optional)
- **Containerization**: Docker / docker-compose
---
## Architecture & Project Layout
/backend
├── src/main/java/...
│   ├── controller
│   ├── service
│   ├── mapper         (MyBatis interfaces)
│   ├── model / dto / vo
│   ├── config
│   └── aspect
└── resources/mapper/*.xml
/frontend
├── src/
│   ├── views/
│   ├── components/
│   ├── router/
│   ├── store/
│   └── utils/request.js (axios instance)

/ai (optional)
├── training/
└── inference_service/

/infra
├── docker-compose.yml
└── deploy scripts

---

## Getting Started (Dev)

### Backend
1. Set up PostgreSQL and create a database (e.g., `trading_website`).
2. Configure `application.yml` or `application.properties` with DB URL, user, password, and JWT secret.
3. Build & run:
```bash
# from backend root
mvn clean package
java -jar target/{your-app}.jar
````

> Note: this project uses a context path (optional). If you set `server.servlet.context-path`, make controllers use relative paths (e.g., `@RequestMapping("/interaction")`) to avoid double-prefix issues.

### Frontend

```bash
cd frontend
npm install
npm run dev
```

Make sure `src/utils/request.js` `baseURL` points to backend API (e.g., `http://localhost:21090/api/trading-website/v1.0`).

### AI Service (optional)

* Train model offline with historical price data.
* Start inference service with FastAPI / Flask exposing e.g. `POST /predict` returning JSON `{trend, recommendation, confidence, rationale}`.

---

## Configuration

Place common settings in `application.yml`:

```yaml
server:
  port: 21090
  servlet:
    context-path: /api/trading-website/v1.0

spring:
  datasource:
    url: jdbc:postgresql://localhost:5432/trading_website
    username: your_user
    password: your_password

jwt:
  secret: your_jwt_secret
  expire: 86400
```

**CORS**: enable global CORS for dev (allow `http://localhost:21091`).

**Pagination**: pass `current` as page number and `size` as page size; in MyBatis XML use:

```xml
LIMIT #{size} OFFSET ${(current - 1) * size}
```

---

## API & Database Notes

* Use POST for endpoints annotated with `@PostMapping` in controllers.
* Keep numeric-state columns (e.g., `trade_status`) as `smallint`/`integer` and compare with numeric literals (`= 1`, `= 2`) — do **not** compare `smallint` to boolean (`= true`) in PostgreSQL.
* Example order statuses:

  * `1` = Pending payment
  * `2` = Paid
  * `3` = Cancelled

**MyBatis tips**

* For dynamic SQL building, be careful with `<choose>` and numeric expressions to avoid generating stray tokens.
* When doing `IN` loops with `@Param("ids") List<Integer> ids`, use `<foreach collection="ids">`.

---

## Contributing

1. Fork the repository
2. Create a feature branch (`git checkout -b feat/my-feature`)
3. Implement and test (backend unit tests + frontend manual test)
4. Open a PR with a clear description and linkage to issues

---

## License & Contact

* License: MIT (or choose your preferred license)
* Project Lead: Chen Pinyan - 2470865290@qq.com
