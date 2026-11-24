import { useUser } from "../UserContext";

export default function Profile() {
  const { employee, roles } = useUser();
  const rows = [
    ["Name", employee.name],
    ["Email", employee.email],
    ["Department", employee.department],
    ["Designation", employee.designation],
    ["Joining date", employee.joiningDate],
    ["Role", roles.join(", ")],
  ];
  return (
    <>
      <h1>My Profile</h1>
      <table className="kv">
        <tbody>
          {rows.map(([k, v]) => (
            <tr key={k}>
              <th>{k}</th>
              <td>{v || "-"}</td>
            </tr>
          ))}
        </tbody>
      </table>
    </>
  );
}
