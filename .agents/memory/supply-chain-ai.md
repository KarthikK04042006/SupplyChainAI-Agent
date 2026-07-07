---
name: Supply Chain AI App
description: Spring Boot 3.2.5 SaaS in supply-chain-app/ — build/run/security decisions
---

## Key Decisions

**Why H2 instead of MySQL:** MySQL is unavailable in the Replit environment. H2 file-backed (`jdbc:h2:file:./data/supplychain`) persists across restarts and requires no external service.

**Why Java 19:** Only GraalVM 22.3.1 (Java 19) is available via the Replit module system. `pom.xml` is pinned to `<java.version>19</java.version>` and Spring Boot 3.2.5.

**Why iText 7.2.5 individual JARs:** The `itext7-core` BOM artifact is not available in Maven Central at that version; individual `kernel`, `layout`, `io` artifacts at 7.2.5 work correctly.

**Port:** Runs on **8099** (8080 is taken by the api-server artifact).

**Workflow name:** `Supply Chain AI App`

**Security rules applied:**
- CSRF enabled globally; only `/h2-console/**` excluded (dev tool, no state changes)
- H2 console restricted to `ROLE_ADMIN` (not public)
- Admin user toggle/delete are **POST** endpoints — not GET — to prevent CSRF via forged links
- iText `Cell` vs POI `Cell` name conflict resolved by using fully-qualified `com.itextpdf.layout.element.Cell` in ReportService

**Default seed accounts:** admin/admin123 (ROLE_ADMIN), demo/demo123 (ROLE_USER) — seeded by DataInitializer on first boot if absent.

**How to Apply:** When modifying SecurityConfig, always keep CSRF on and use POST for any admin mutation endpoint.
