import React from "react";
import ReactDOM from "react-dom/client";
import { BrowserRouter } from "react-router-dom";
import { MsalProvider } from "@azure/msal-react";
import { EventType } from "@azure/msal-browser";
import { msalInstance } from "./authConfig";
import App from "./App";
import "./styles.css";

msalInstance.initialize().then(async () => {
  await msalInstance.handleRedirectPromise();
  const accounts = msalInstance.getAllAccounts();
  if (accounts.length > 0) msalInstance.setActiveAccount(accounts[0]);
  msalInstance.addEventCallback((e) => {
    if (e.eventType === EventType.LOGIN_SUCCESS && e.payload?.account) {
      msalInstance.setActiveAccount(e.payload.account);
    }
  });

  ReactDOM.createRoot(document.getElementById("root")).render(
    <React.StrictMode>
      <MsalProvider instance={msalInstance}>
        <BrowserRouter>
          <App />
        </BrowserRouter>
      </MsalProvider>
    </React.StrictMode>
  );
});
