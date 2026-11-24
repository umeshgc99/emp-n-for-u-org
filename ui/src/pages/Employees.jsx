import { useState } from "react";
import { api } from "../api";
import { useAction, useLoad } from "../hooks";
import { hasRole, useUser } from "../UserContext";
import { Async, ErrorBox, Field, Loading, Money, Notice } from "../components/ui";

/** HR + Director: list employees, view salary. HR only: add employee, set salary. */
export default function Employees() {
  const user = useUser();
  const isHr = hasRole(user, "HR");
  const list = useLoad(api.employees, []);
  const [selected, setSelected] = useState(null);

  return (
    <>
      <h1>Employees</h1>
      <Async state={list}>
        {(rows) => (
          <table>
            <thead><tr><th>Name</th><th>Email</th><th>Department</th><th>Designation</th><th>Joined</th><th></th></tr></thead>
            <tbody>
              {rows.map((e) => (
                <tr key={e.id}>
                  <td>{e.name}</td><td>{e.email}</td><td>{e.department}</td><td>{e.designation}</td><td>{e.joiningDate}</td>
                  <td><button onClick={() => setSelected(e)}>Salary</button></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Async>

      {selected && <SalaryPanel key={selected.id} employee={selected} canEdit={isHr} onClose={() => setSelected(null)} />}
      {isHr && <NewEmployee onCreated={list.reload} />}
    </>
  );
}

function SalaryPanel({ employee, canEdit, onClose }) {
  const state = useLoad(() => api.employeeSalary(employee.id), [employee.id]);
  const [form, setForm] = useState({ base: "", bonus: "0", effectiveFrom: "" });
  const action = useAction();

  const save = async (e) => {
    e.preventDefault();
    const body = { base: Number(form.base), bonus: Number(form.bonus || 0), effectiveFrom: form.effectiveFrom };
    const r = await action.run(() => api.setSalary(employee.id, body), "Salary saved");
    if (r) state.reload();
  };

  return (
    <div className="card">
      <h3>Salary: {employee.name} <button onClick={onClose}>Close</button></h3>
      {state.loading && !state.data && <Loading />}
      {state.error && state.error.status === 404 && <p className="muted">Salary not set yet.</p>}
      {state.error && state.error.status !== 404 && <ErrorBox error={state.error} />}
      {state.data && (
        <table className="kv">
          <tbody>
            <tr><th>Base</th><td><Money value={state.data.base} /></td></tr>
            <tr><th>Bonus</th><td><Money value={state.data.bonus} /></td></tr>
            <tr><th>Effective from</th><td>{state.data.effectiveFrom}</td></tr>
          </tbody>
        </table>
      )}
      {canEdit && (
        <form onSubmit={save}>
          <h4>Set salary</h4>
          <Field label="Base"><input type="number" min="0" step="0.01" required value={form.base} onChange={(e) => setForm({ ...form, base: e.target.value })} /></Field>
          <Field label="Bonus"><input type="number" min="0" step="0.01" value={form.bonus} onChange={(e) => setForm({ ...form, bonus: e.target.value })} /></Field>
          <Field label="Effective from"><input type="date" required value={form.effectiveFrom} onChange={(e) => setForm({ ...form, effectiveFrom: e.target.value })} /></Field>
          <button className="primary" disabled={action.busy}>Save</button>
          <Notice action={action} />
        </form>
      )}
    </div>
  );
}

const emptyEmp = { email: "", name: "", department: "", designation: "", joiningDate: "" };

function NewEmployee({ onCreated }) {
  const [form, setForm] = useState(emptyEmp);
  const action = useAction();
  const set = (k) => (e) => setForm({ ...form, [k]: e.target.value });

  const submit = async (e) => {
    e.preventDefault();
    const r = await action.run(() => api.createEmployee(form), "Employee created");
    if (r) {
      setForm(emptyEmp);
      onCreated();
    }
  };

  return (
    <form onSubmit={submit} className="card">
      <h3>Add employee</h3>
      <p className="muted">The email must match the person's company (Entra) sign-in.</p>
      <Field label="Email"><input type="email" required value={form.email} onChange={set("email")} /></Field>
      <Field label="Name"><input required value={form.name} onChange={set("name")} /></Field>
      <Field label="Department"><input value={form.department} onChange={set("department")} /></Field>
      <Field label="Designation"><input value={form.designation} onChange={set("designation")} /></Field>
      <Field label="Joining date"><input type="date" required value={form.joiningDate} onChange={set("joiningDate")} /></Field>
      <button className="primary" disabled={action.busy}>Create</button>
      <Notice action={action} />
    </form>
  );
}
