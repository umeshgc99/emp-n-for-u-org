# Employee Portal – React UI

React 18 + Vite + MSAL (Microsoft Entra ID sign-in). Talks to the Azure Functions backend in the repo root.

## Screens and the API each one calls
| Screen | Who sees it | API |
|---|---|---|
| My Profile | everyone | `GET /v1/me` |
| My Salary | everyone | `GET /v1/me/salary` |
| Holidays (+ add form for HR/Admin) | everyone | `GET /v1/holidays`, `POST /v1/holidays` |
| Leave (apply + track) | everyone | `POST /v1/leaves`, `GET /v1/me/leaves` |
| Training (request + track) | everyone | `POST /v1/trainings`, `GET /v1/me/trainings` |
| Employees (list, view/set salary, add employee) | HR, Director (set salary / add: HR only) | `GET/POST /v1/employees`, `GET/PUT /v1/employees/{id}/salary` |
| Approvals (leave + training) | HR, Director | `GET /v1/leaves/pending`, `POST /v1/leaves/{id}/decision`, `GET /v1/trainings/pending`, `POST /v1/trainings/{id}/decision` |

Menus are hidden by role for convenience only; the backend enforces every rule.

## Code layout
```
src/
├── authConfig.js   MSAL setup (tenant, client id, API scope)
├── api.js          one function per backend endpoint, adds the Bearer token
├── hooks.js        useLoad (fetch data) / useAction (submit + messages)
├── UserContext.jsx logged-in user (from /me) and role helper
├── App.jsx         login screen, route table, role-based routes
├── components/     Layout (sidebar), ui (shared bits)
└── pages/          Profile, MySalary, Holidays, Leaves, Trainings, Employees, Approvals
```

## Run locally
1. Complete the Entra ID setup below.
2. `copy .env.example .env` and fill in the values.
3. Start the backend from the repo root (`mvn clean package` then `mvn azure-functions:run`, needs PostgreSQL and `local.settings.json`).
4. `npm install` then `npm run dev` and open http://localhost:5173.

## Entra ID setup (one time)
**API app registration**
1. Entra admin center → App registrations → New (single tenant). Copy its **Application (client) ID** and your **Tenant ID**.
2. Manifest: set `"accessTokenAcceptedVersion": 2` (the backend validates v2 tokens).
3. Expose an API: set the Application ID URI (`api://<client-id>`) and add a scope named `access_as_user`.
4. App roles: create roles with values `Employee`, `HR`, `Director`, `Admin` (allowed member types: Users/Groups).
5. Enterprise applications → this app → Users and groups: assign each person (or group) a role. Anyone without a role is treated as Employee.

**SPA app registration**
1. New registration, platform **Single-page application**, redirect URIs `http://localhost:5173` (and your production URL later).
2. API permissions → add `access_as_user` from the API app, then grant admin consent.

**Backend settings** (`local.settings.json`, or Function App settings in Azure): `ENTRA_TENANT_ID` = tenant id, `ENTRA_API_AUDIENCE` = the **API app's client ID (GUID)**, not the `api://` URI.

**UI `.env`**: `VITE_ENTRA_CLIENT_ID` = SPA client id, `VITE_ENTRA_TENANT_ID`, `VITE_API_SCOPE` = `api://<api-client-id>/access_as_user`.

## First login
The user must already have a row in `employees` with the same email as their Entra sign-in (HR adds them, or insert the first HR user by SQL). The first successful call links their Entra account to that row. Without a row the portal shows "No employee record exists for this account".

## Deploy to Azure Static Web Apps
`npm run build` outputs `dist/`. Deploy with the Static Web Apps GitHub Action (app location `ui`, output `dist`). Set `VITE_*` values as build environment variables. Either link the Function App as the SWA backend (then `VITE_API_BASE_URL=/api`) or keep it separate and set its URL plus CORS on the Function App to the SWA domain. Add the SWA URL as a redirect URI on the SPA registration.
