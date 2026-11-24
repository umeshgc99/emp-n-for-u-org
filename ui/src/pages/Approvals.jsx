import { api } from "../api";
import { useAction, useLoad } from "../hooks";
import { Async, Notice } from "../components/ui";

function DecisionButtons({ onDecide, busy }) {
  return (
    <>
      <button className="primary" disabled={busy} onClick={() => onDecide(true)}>Approve</button>{" "}
      <button disabled={busy} onClick={() => onDecide(false)}>Reject</button>
    </>
  );
}

/** HR / Director: pending leave and training requests. */
export default function Approvals() {
  const leaves = useLoad(api.pendingLeaves, []);
  const trainings = useLoad(api.pendingTrainings, []);
  const action = useAction();

  const decideLeave = (id, approved) =>
    action.run(() => api.decideLeave(id, approved), approved ? "Leave approved" : "Leave rejected").then(leaves.reload);
  const decideTraining = (id, approved) =>
    action.run(() => api.decideTraining(id, approved), approved ? "Training approved" : "Training rejected").then(trainings.reload);

  return (
    <>
      <h1>Approvals</h1>
      <Notice action={action} />

      <h3>Leave requests</h3>
      <Async state={leaves}>
        {(list) => (
          <table>
            <thead><tr><th>Employee #</th><th>Type</th><th>From</th><th>To</th><th>Days</th><th>Reason</th><th></th></tr></thead>
            <tbody>
              {list.length === 0 && <tr><td colSpan="7" className="muted">Nothing pending</td></tr>}
              {list.map((l) => (
                <tr key={l.id}>
                  <td>{l.employeeId}</td><td>{l.type}</td><td>{l.fromDate}</td><td>{l.toDate}</td>
                  <td>{l.days}</td><td>{l.reason}</td>
                  <td><DecisionButtons busy={action.busy} onDecide={(a) => decideLeave(l.id, a)} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Async>

      <h3>Training requests</h3>
      <Async state={trainings}>
        {(list) => (
          <table>
            <thead><tr><th>Employee #</th><th>Title</th><th>Reason</th><th></th></tr></thead>
            <tbody>
              {list.length === 0 && <tr><td colSpan="4" className="muted">Nothing pending</td></tr>}
              {list.map((t) => (
                <tr key={t.id}>
                  <td>{t.employeeId}</td><td>{t.title}</td><td>{t.reason}</td>
                  <td><DecisionButtons busy={action.busy} onDecide={(a) => decideTraining(t.id, a)} /></td>
                </tr>
              ))}
            </tbody>
          </table>
        )}
      </Async>
    </>
  );
}
