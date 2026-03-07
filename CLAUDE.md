# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project Overview

Multi-salon reservation platform with event-driven architecture. Handles cancellation/no-show fee calculation via a versioned policy engine, separates read/write models, and enforces multi-tenant data isolation by `store_id`.

**Language:** Korean documentation, English code conventions.

## Tech Stack

- **Backend:** Spring Boot, Spring Data JPA
- **Messaging:** Kafka (event streaming for reservation state changes)
- **Cache/Lock:** Redis (slot locking, idempotency, concurrency control)
- **Search:** OpenSearch (read model for admin queries)
- **Database:** RDBMS with BIGINT PKs, UUID for event IDs
- **Infra:** Docker, Docker Compose
- **CI/CD:** GitHub Actions
- **Secrets:** Secret Manager

## Architecture

### Event-Driven + CQRS

- Reservation state changes publish events to Kafka
- Consumers update OpenSearch read models, trigger notifications, write audit logs
- Write path uses JPA + RDBMS; read path (admin search) uses OpenSearch
- ReservationEvent table serves as immutable audit log

### Policy Engine

- `PolicyVersion` stores versioned cancellation/no-show rules per store (`rules_json`)
- Reservations pin `policy_version_id` at confirmation time
- Penalties are calculated using the pinned policy, not the current one — enables historical recalculation and auditability
- `Penalty.basis_json` captures the full calculation basis for traceability

### Multi-Tenancy

- All domain data is scoped by `store_id`
- Every query and mutation must include `store_id` as a condition
- Enforce at repository/QueryDSL layer as a default filter

## Domain Model (9 entities)

`Store` → `Staff`, `ServiceMenu`, `StaffSchedule`, `TimeSlot`, `PolicyVersion`
`TimeSlot` → `Reservation` (slot status: OPEN/HELD/BOOKED/BLOCKED)
`Reservation` → `Penalty` (0 or 1), `ReservationEvent` (audit log)
`Reservation` pins `PolicyVersion` at creation

Key statuses:
- **Reservation:** REQUESTED → CONFIRMED → COMPLETED | CANCELED | NO_SHOW
- **TimeSlot:** OPEN → HELD → BOOKED | BLOCKED (HELD is optional for MVP)

Key constraints:
- `uq_slot_staff_start (staff_id, start_at)` — prevents double-booking
- `uq_penalty_reservation (reservation_id)` — one penalty per reservation
- `uq_policy_store_version (store_id, version)` — unique policy versions per store

See `docs/erd.md` for full schema, indexes, and FK relationships.

## Development Status

This project is in the **early implementation phase** — domain model and architecture are documented, source code scaffolding is in progress. Check the current branch and recent commits for the latest state.
