import { NavLink } from "react-router-dom";
import { useMsal } from "@azure/msal-react";
import { hasRole, useUser } from "../UserContext";

export default function Layout({ children }) {
  const { instance } = useMsal();
  const user = useUser();
  const reviewer = hasRole(user, "HR", "DIRECTOR");

  return (
    <div className="shell">
      <aside>
        <h2>Employee Portal</h2>
        <nav>
          <NavLink to="/" end>My Profile</NavLink>
          <NavLink to="/salary">My Salary</NavLink>
          <NavLink to="/holidays">Holidays</NavLink>
          <NavLink to="/leaves">Leave</NavLink>
          <NavLink to="/trainings">Training</NavLink>
          {reviewer && <NavLink to="/employees">Employees</NavLink>}
          {reviewer && <NavLink to="/approvals">Approvals</NavLink>}
        </nav>
        <div className="who">
          <strong>{user.employee.name}</strong>
          <small>{user.roles.join(", ")}</small>
          <button onClick={() => instance.logoutRedirect()}>Sign out</button>
        </div>
      </aside>
      <main>{children}</main>
    </div>
  );
}
