import { useState, useEffect, useCallback } from 'react';
import { apiCall, buildQueryString } from '../api';
import { EQUIPMENT_TYPES, ROLE_ADMIN, getTodayDate } from '../constants';
import FilterBar from './FilterBar';
import DataTable from './DataTable';
import { transactionColumns } from './transactionColumns';

// This one page is used for both "Purchases" and "Assignments & Expenditures".
// - kinds: what the user can add on this page (for example purchase, or assign + expend)
// - showPersonnel: show the personnel input or not
function TransactionPage({ kinds, user, bases, showPersonnel }) {
  const isAdmin = user.role === ROLE_ADMIN;

  const [selectedKind, setSelectedKind] = useState(0); // index of the "kinds" array
  const [form, setForm] = useState({
    equipmentType: 'VEHICLE',
    quantity: 1,
    date: getTodayDate(),
  });
  const [filters, setFilters] = useState({});
  const [rows, setRows] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  // get the records of all types shown on this page and show the newest first
  const loadRows = useCallback(async () => {
    const results = await Promise.all(
      kinds.map((kind) =>
        apiCall(
          '/api/txns' +
            buildQueryString({
              type: kind.type,
              base: filters.base,
              equipmentType: filters.eq,
              from: filters.from,
              to: filters.to,
            })
        )
      )
    );
    const allRows = results.flat();
    allRows.sort((a, b) => b.id - a.id);
    setRows(allRows);
  }, [filters]);

  useEffect(() => {
    loadRows();
  }, [loadRows]);

  // update one field of the form
  const handleChange = (fieldName) => (event) => {
    setForm({ ...form, [fieldName]: event.target.value });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const requestBody = {
      ...form,
      quantity: Number(form.quantity),
      // admin picks the base, other users always use their own base
      baseId: isAdmin ? Number(form.baseId) : user.base.id,
    };

    apiCall(kinds[selectedKind].endpoint, { method: 'POST', body: requestBody })
      .then(() => {
        setErrorMessage('');
        loadRows(); // refresh the table
      })
      .catch((error) => setErrorMessage(error.message));
  };

  return (
    <div>
      <h2>{kinds.map((kind) => kind.title).join(' & ')}</h2>

      <form className="card form-row" onSubmit={handleSubmit}>
        {/* only shown when the page has more than one option (assign / expend) */}
        {kinds.length > 1 && (
          <select value={selectedKind} onChange={(e) => setSelectedKind(Number(e.target.value))}>
            {kinds.map((kind, index) => (
              <option key={index} value={index}>{kind.label}</option>
            ))}
          </select>
        )}

        {isAdmin ? (
          <select required value={form.baseId || ''} onChange={handleChange('baseId')}>
            <option value="">Select base</option>
            {bases.map((base) => (
              <option key={base.id} value={base.id}>{base.name}</option>
            ))}
          </select>
        ) : (
          <input type="text" disabled value={user.base.name} />
        )}

        <select value={form.equipmentType} onChange={handleChange('equipmentType')}>
          {EQUIPMENT_TYPES.map((type) => (
            <option key={type} value={type}>{type}</option>
          ))}
        </select>

        <input
          type="text"
          required
          placeholder="Asset name"
          value={form.assetName || ''}
          onChange={handleChange('assetName')}
        />

        <input
          type="number"
          required
          min="1"
          value={form.quantity}
          onChange={handleChange('quantity')}
        />

        <input type="date" value={form.date} onChange={handleChange('date')} />

        {showPersonnel && (
          <input
            type="text"
            placeholder="Personnel"
            value={form.personnel || ''}
            onChange={handleChange('personnel')}
          />
        )}

        <button type="submit" className="btn">{kinds[selectedKind].label}</button>

        {errorMessage && <p className="error-text">{errorMessage}</p>}
      </form>

      <FilterBar filters={filters} setFilters={setFilters} bases={bases} isAdmin={isAdmin} />

      <DataTable rows={rows} columns={transactionColumns} />
    </div>
  );
}

export default TransactionPage;
