import { createContext, useContext } from "react";

/** { employee, roles: ["HR", ...] } returned by GET /me */
export const UserContext = createContext(null);
export const useUser = () => useContext(UserContext);

/** UI-only convenience; the backend enforces the same rules. */
export const hasRole = (user, ...roles) => roles.some((r) => user.roles.includes(r));
