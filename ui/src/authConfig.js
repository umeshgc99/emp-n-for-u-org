import { PublicClientApplication } from "@azure/msal-browser";

const tenant = import.meta.env.VITE_ENTRA_TENANT_ID;

export const msalInstance = new PublicClientApplication({
  auth: {
    clientId: import.meta.env.VITE_ENTRA_CLIENT_ID,
    authority: `https://login.microsoftonline.com/${tenant}`,
    redirectUri: window.location.origin,
  },
  // sessionStorage: tokens are cleared when the tab closes
  cache: { cacheLocation: "sessionStorage" },
});

export const apiScopes = [import.meta.env.VITE_API_SCOPE];
