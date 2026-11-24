import { AuthenticatedTemplate, UnauthenticatedTemplate, useMsal } from "@azure/msal-react";
import { Navigate, Route, Routes } from "react-router-dom";
import { api } from "./api";
import { apiScopes } from "./authConfig";
import { useLoad } from "./hooks";
import { UserContext, hasRole } from "./UserContext";
import Layout from "./components/Layout";
import { Loading, ErrorBox } from "./components/ui";
import Profile from "./pages/Profile";
import MySalary from "./pages/MySalary";
import Holidays from "./pages/Holidays";
import Leaves from "./pages/Leaves";
import Trainings from "./pages/Trainings";
import Employees from "./pages/Employees";
import Approvals from "./pages/Approvals";

function Login() {
  const { instance } = useMsal();
  return (
    <div className="login">
      <h1>Employee Portal</h1>
      <p>Sign in with your company account.</p>
      <button className="primary" onClick={() => instance.loginRedirect({ scopes: apiScopes })}>
        Sign in with Microsoft
      </button>
    </div>
  );
}

function Portal() {
  const { instance } = useMsal();
  const { data: user, error, loading } = useLoad(api.me, []);

  if (loading) return <Loading />;
  if (error) {
    return (
      <div className="login">
        <h1>Cannot open the portal</h1>
        <ErrorBox error={error} />
        <button onClick={() => instance.logoutRedirect()}>Sign out</button>
      </div>
    );
  }

  return (
    <UserContext.Provider value={user}>
      <Layout>
        <Routes>
          <Route path="/" element={<Profile />} />
          <Route path="/salary" element={<MySalary />} />
          <Route path="/holidays" element={<Holidays />} />
          <Route path="/leaves" element={<Leaves />} />
          <Route path="/trainings" element={<Trainings />} />
          {hasRole(user, "HR", "DIRECTOR") && <Route path="/employees" element={<Employees />} />}
          {hasRole(user, "HR", "DIRECTOR") && <Route path="/approvals" element={<Approvals />} />}
          <Route path="*" element={<Navigate to="/" replace />} />
        </Routes>
      </Layout>
    </UserContext.Provider>
  );
}

export default function App() {
  return (
    <>
      <AuthenticatedTemplate>
        <Portal />
      </AuthenticatedTemplate>
      <UnauthenticatedTemplate>
        <Login />
      </UnauthenticatedTemplate>
    </>
  );
}
