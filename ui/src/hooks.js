import { useCallback, useEffect, useState } from "react";

/** Loads data from an api call. Returns { data, error, loading, reload }. */
export function useLoad(fn, deps = []) {
  const [state, setState] = useState({ data: null, error: null, loading: true });

  const reload = useCallback(() => {
    setState((s) => ({ ...s, loading: true }));
    return fn()
      .then((data) => setState({ data, error: null, loading: false }))
      .catch((error) => setState({ data: null, error, loading: false }));
    // eslint-disable-next-line react-hooks/exhaustive-deps
  }, deps);

  useEffect(() => {
    reload();
  }, [reload]);

  return { ...state, reload };
}

/** Runs a submit action, tracking busy / error / success message. */
export function useAction() {
  const [busy, setBusy] = useState(false);
  const [error, setError] = useState(null);
  const [done, setDone] = useState(null);

  const run = async (fn, successMessage = "Saved") => {
    setBusy(true);
    setError(null);
    setDone(null);
    try {
      const r = await fn();
      setDone(successMessage);
      return r;
    } catch (e) {
      setError(e.message);
      return undefined;
    } finally {
      setBusy(false);
    }
  };
  return { busy, error, done, run };
}
