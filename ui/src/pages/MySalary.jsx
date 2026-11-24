import { api } from "../api";
import { useLoad } from "../hooks";
import { Async, Money } from "../components/ui";

export default function MySalary() {
  const state = useLoad(api.mySalary, []);
  return (
    <>
      <h1>My Salary</h1>
      <Async state={state}>
        {(s) => (
          <table className="kv">
            <tbody>
              <tr><th>Base</th><td><Money value={s.base} /></td></tr>
              <tr><th>Bonus</th><td><Money value={s.bonus} /></td></tr>
              <tr><th>Effective from</th><td>{s.effectiveFrom}</td></tr>
            </tbody>
          </table>
        )}
      </Async>
    </>
  );
}
