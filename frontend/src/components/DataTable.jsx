// Simple reusable table.
// columns = list of { title, getValue } and rows = list of data objects
function DataTable({ rows, columns }) {
  return (
    <div className="table-wrapper">
      <table>
        <thead>
          <tr>
            {columns.map((column) => (
              <th key={column.title}>{column.title}</th>
            ))}
          </tr>
        </thead>

        <tbody>
          {rows.map((row, index) => (
            <tr key={row.id ?? index}>
              {columns.map((column) => (
                <td key={column.title}>{column.getValue(row)}</td>
              ))}
            </tr>
          ))}

          {/* show a message when there is no data */}
          {rows.length === 0 && (
            <tr>
              <td colSpan={columns.length} className="no-data">No records found</td>
            </tr>
          )}
        </tbody>
      </table>
    </div>
  );
}

export default DataTable;
