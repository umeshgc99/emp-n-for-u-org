import { useState } from "react";
import { api } from "../api";
import { useAction, useLoad } from "../hooks";
import { Async, Field, Notice, Status } from "../components/ui";

export default function Trainings() {
  const state = useLoad(api.myTrainings, []);
  const [form, setForm] = useState({ title: "", reason: "" });
  const action = useAction();

  const submit = async (e) => {
    e.preventDefault();
    const r = await action.run(() => api.requestTraining(form), "Training request submitted");
    if (r) {
      setForm({ title: "", reason: "" });
      state.reload();
    }
  };

  return (
    <>
      <h1>Training</h1>

      <form onSubmit={submit} className="card">
        <h3>Request extra training</h3>
        <Field label="Title"><input required value={form.title} onChange={(e) => setForm({ ...form, title: e.target.value })} /></Field>
        <Field label="Why do you need it?"><input value={form.reason} onChange={(e) => setForm({ ...form, reason: e.target.value })} /></Field>
        <button className="primary" disabled={action.busy}>Submit</button>
        <Notice action={action} />
      </form>

      <h3>My requests</h3>
      <Async state={state}>
        {(list) => (
          <table>
            <thead><tr><th>Title</th><th>Reason</th><th>Status</th></tr></thead>
            <tbody>
              {list.length === 0 && <tr><td colSpan="3" className="muted">No training requests yet</td></tr>}
              {list.map((t) => (
                <tr key={t.id}><td>{t.title}</td><td>{t.reason}</td><td><Status value={t.status} /></td></tr>
              ))}
            </tbody>
          </table>
        )}
      </Async>
    </>
  );
}
