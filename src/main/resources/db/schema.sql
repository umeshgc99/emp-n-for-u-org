-- Employee Management Portal schema (PostgreSQL). Roles live in Entra ID App Roles, not here.
CREATE TABLE IF NOT EXISTS employees (
    id           BIGSERIAL PRIMARY KEY,
    entra_oid    UUID UNIQUE,
    email        VARCHAR(255) UNIQUE NOT NULL,
    name         VARCHAR(255) NOT NULL,
    department   VARCHAR(100),
    designation  VARCHAR(100),
    joining_date DATE NOT NULL,
    manager_id   BIGINT REFERENCES employees(id)
);

CREATE TABLE IF NOT EXISTS salaries (
    employee_id    BIGINT PRIMARY KEY REFERENCES employees(id),
    base           NUMERIC(12,2) NOT NULL,
    bonus          NUMERIC(12,2) NOT NULL DEFAULT 0,
    effective_from DATE NOT NULL
);

CREATE TABLE IF NOT EXISTS leave_balances (
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    type        VARCHAR(20) NOT NULL,          -- ANNUAL, SICK, CASUAL
    remaining   INT NOT NULL,
    PRIMARY KEY (employee_id, type)
);

CREATE TABLE IF NOT EXISTS leave_requests (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    type        VARCHAR(20) NOT NULL,
    from_date   DATE NOT NULL,
    to_date     DATE NOT NULL,
    days        INT NOT NULL,
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',   -- PENDING, APPROVED, REJECTED
    reviewed_by BIGINT REFERENCES employees(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS holidays (
    id           BIGSERIAL PRIMARY KEY,
    holiday_date DATE NOT NULL UNIQUE,
    name         VARCHAR(150) NOT NULL
);

CREATE TABLE IF NOT EXISTS training_requests (
    id          BIGSERIAL PRIMARY KEY,
    employee_id BIGINT NOT NULL REFERENCES employees(id),
    title       VARCHAR(200) NOT NULL,
    reason      TEXT,
    status      VARCHAR(20) NOT NULL DEFAULT 'PENDING',
    reviewed_by BIGINT REFERENCES employees(id),
    created_at  TIMESTAMPTZ NOT NULL DEFAULT now()
);

CREATE TABLE IF NOT EXISTS audit_log (
    id                 BIGSERIAL PRIMARY KEY,
    actor_oid          VARCHAR(64) NOT NULL,
    action             VARCHAR(100) NOT NULL,
    target_employee_id BIGINT,
    at                 TIMESTAMPTZ NOT NULL DEFAULT now()
);
