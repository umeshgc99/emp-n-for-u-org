import { useState } from "react";
import { api } from "../api";
import { useAction, useLoad } from "../hooks";
import { Async, Field, Notice, Status } from "../components/ui";

const empty = { type: "ANNUAL", fromDate: "", toDate: "", reason: "" };

export default function Leaves() {
  const state = useLoad(api.myLeaves, []);
  const [form, setForm] = useState(empty);
  const action = useAction();
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    const r = await action.run(() => api.applyLeave(form), "Leave request submitted");
    if (r) {
      setForm(empty);
      state.reload();
    }
  };

  return (
    <>
      <h1>Leave</h1>

      <form onSubmit={submit} className="card">
        <h3>Apply for leave</h3>
        <Field label="Type">
          <select value={form.type} onChange={set("type")}>
            <option value="ANNUAL">Annual</option>
            <option value="SICK">Sick</option>
            <option value="CASUAL">Casual</option>
          </select>
        </Field>
        <Field label="From"><input type="date" required value={form.fromDate} onChange={set("fromDate")} /></Field>
        <Field label="To"><input type="date" required value={form.toDate} onChange={set("toDate")} /></Field>
        <Field label="Reason"><input value={form.reason} onChange={set("reason")} /></Field>
        <button className="primary" disabled={action.busy}>Submit</button>
        <Notice action={action} />
      </form>

      <h3>My requests</h3>
      <Async state={state}>
        {(list) => (
          <table>
            <thead><tr><th>Type</th><th>From</th><th>To</th><th>Days</th><th>Reason</th><th>Status</th></tr></thead>
            <tbody>
              {list.length === 0 && <tr><td colSpan="6" className="muted">No leave requests yet</td></tr>}
              {list.map((l) => (
                <tr key={l.id}>
                  <td>{l.type}</td><td>{l.fromDate}</td><td>{l.toDate}</td><td>{l.days}</td>
                  <td>{l.reason}</td><td><Status value={l.status} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Async>
    </>
  );
}
