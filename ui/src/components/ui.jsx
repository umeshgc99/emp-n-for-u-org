export const Loading = () => <p className="muted">Loading…</p>;

export const ErrorBox = ({ error }) =>
  error ? <p className="error">{error.message || String(error)}</p> : null;

export const Money = ({ value }) =>
  Number(value).toLocaleString(undefined, { minimumFractionDigits: 2, maximumFractionDigits: 2 });

export const Status = ({ value }) => <span className={`badge ${String(value).toLowerCase()}`}>{value}</span>;

/** Handles loading / error / empty for a list result, then renders children(data). */
export function Async({ state, children }) {
  if (state.loading && !state.data) return <Loading />;
  if (state.error) return <ErrorBox error={state.error} />;
  return children(state.data);
}

export const Field = ({ label, children }) => (
  <label className="field">
    <span>{label}</span>
    {children}
  </label>
);

export const Notice = ({ action }) => (
  <>
    {action.error && <p className="error">{action.error}</p>}
    {action.done && <p className="success">{action.done}</p>}
  </>
);
