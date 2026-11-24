// One function per backend endpoint (see docs/architecture.md). All calls carry the Entra access token.
import { InteractionRequiredAuthError } from "@azure/msal-browser";
import { msalInstance, apiScopes } from "./authConfig";

const BASE = (import.meta.env.VITE_API_BASE_URL || "/api").replace(/\/$/, "");

export class ApiError extends Error {
  constructor(status, message) {
    super(message);
    this.status = status;
  }
}

async function accessToken() {
  const account = msalInstance.getActiveAccount() || msalInstance.getAllAccounts()[0];
  try {
    const r = await msalInstance.acquireTokenSilent({ scopes: apiScopes, account });
    return r.accessToken;
  } catch (e) {
    if (e instanceof InteractionRequiredAuthError) {
      await msalInstance.acquireTokenRedirect({ scopes: apiScopes, account });
    }
    throw e;
  }
}

async function request(method, path, body) {
  const res = await fetch(`${BASE}/v1${path}`, {
    method,
    headers: {
      Authorization: `Bearer ${await accessToken()}`,
      ...(body ? { "Content-Type": "application/json" } : {}),
    },
    body: body ? JSON.stringify(body) : undefined,
  });
  const text = await res.text();
  const data = text ? JSON.parse(text) : null;
  if (!res.ok) throw new ApiError(res.status, data?.error || `Request failed (${res.status})`);
  return data;
}

export const api = {
  // profile
  me: () => request("GET", "/me"),
  mySalary: () => request("GET", "/me/salary"),
  myLeaves: () => request("GET", "/me/leaves"),
  myTrainings: () => request("GET", "/me/trainings"),

  // employees (list: HR/Director, create: HR)
  employees: () => request("GET", "/employees"),
  employee: (id) => request("GET", `/employees/${id}`),
  createEmployee: (e) => request("POST", "/employees", e),
  employeeSalary: (id) => request("GET", `/employees/${id}/salary`),
  setSalary: (id, s) => request("PUT", `/employees/${id}/salary`, s),

  // holidays (add: HR/Admin)
  holidays: () => request("GET", "/holidays"),
  addHoliday: (h) => request("POST", "/holidays", h),

  // leave
  applyLeave: (l) => request("POST", "/leaves", l),
  pendingLeaves: () => request("GET", "/leaves/pending"),
  decideLeave: (id, approved) => request("POST", `/leaves/${id}/decision`, { approved }),

  // training
  requestTraining: (t) => request("POST", "/trainings", t),
  pendingTrainings: () => request("GET", "/trainings/pending"),
  decideTraining: (id, approved) => request("POST", `/trainings/${id}/decision`, { approved }),
};
