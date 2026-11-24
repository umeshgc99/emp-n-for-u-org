import { useState } from "react";
import { api } from "../api";
import { useAction, useLoad } from "../hooks";
import { hasRole, useUser } from "../UserContext";
import { Async, Field, Notice } from "../components/ui";

export default function Holidays() {
  const user = useUser();
  const state = useLoad(api.holidays, []);
  const canManage = hasRole(user, "HR", "ADMIN");
  const [form, setForm] = useState({ date: "", name: "" });
  const action = useAction();

  const submit = async (e) => {
    e.preventDefault();
    const r = await action.run(() => api.addHoliday(form), "Holiday added");
    if (r) {
      setForm({ date: "", name: "" });
      state.reload();
    }
  };

  return (
    <>
      <h1>Public Holidays</h1>
      <Async state={state}>
        {(list) => (
          <table>
            <thead><tr><th>Date</th><th>Holiday</th></tr></thead>
            <tbody>
              {list.length === 0 && <tr><td colSpan="2" className="muted">No holidays yet</td></tr>}
              {list.map((h) => (
                <tr key={h.id}><td>{h.date}</td><td>{h.name}</td></tr>
              ))}
            </tbody>
          </table>
        )}
      </Async>

      {canManage && (
        <form onSubmit={submit} className="card">
          <h3>Add holiday</h3>
          <Field label="Date">
            <input type="date" required value={form.date} onChange={(e) => setForm({ ...form, date: e.target.value })} />
          </Field>
          <Field label="Name">
            <input required value={form.name} onChange={(e) => setForm({ ...form, name: e.target.value })} />
          </Field>
          <button className="primary" disabled={action.busy}>Add</button>
          <Notice action={action} />
        </form>
      )}
    </>
  );
}
