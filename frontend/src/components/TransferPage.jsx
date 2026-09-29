import { useState, useEffect } from 'react';
import { apiCall } from '../api';
import { EQUIPMENT_TYPES, ROLE_ADMIN } from '../constants';
import DataTable from './DataTable';

// columns for the transfer history table
const transferColumns = [
  { title: 'Time', getValue: (row) => new Date(row.createdAt).toLocaleString() },
  { title: 'From', getValue: (row) => row.fromBase?.name },
  { title: 'To', getValue: (row) => row.toBase?.name },
  { title: 'Equipment', getValue: (row) => row.equipmentType },
  { title: 'Asset', getValue: (row) => row.assetName },
  { title: 'Qty', getValue: (row) => row.quantity },
  { title: 'By', getValue: (row) => row.createdBy },
];

// Transfers page - move assets from one base to another and see the history
function TransferPage({ user, bases }) {
  const isAdmin = user.role === ROLE_ADMIN;

  const [form, setForm] = useState({ equipmentType: 'VEHICLE', quantity: 1 });
  const [transfers, setTransfers] = useState([]);
  const [errorMessage, setErrorMessage] = useState('');

  const loadTransfers = () => {
    apiCall('/api/transfers').then(setTransfers);
  };

  // load history when the page opens
  useEffect(() => {
    loadTransfers();
  }, []);

  const handleChange = (fieldName) => (event) => {
    setForm({ ...form, [fieldName]: event.target.value });
  };

  const handleSubmit = (event) => {
    event.preventDefault();

    const requestBody = {
      ...form,
      quantity: Number(form.quantity),
      // admin can pick the "from" base, other users can only send from their own base
      fromBaseId: isAdmin ? Number(form.fromBaseId) : user.base.id,
      toBaseId: Number(form.toBaseId),
    };

    apiCall('/api/transfers', { method: 'POST', body: requestBody })
      .then(() => {
        setErrorMessage('');
        loadTransfers();
      })
      .catch((error) => setErrorMessage(error.message));
  };

  return (
    <div>
      <h2>Transfers</h2>

      <form className="card form-row" onSubmit={handleSubmit}>
        {isAdmin ? (
          <select required value={form.fromBaseId || ''} onChange={handleChange('fromBaseId')}>
            <option value="">From base</option>
            {bases.map((base) => (
              <option key={base.id} value={base.id}>{base.name}</option>
            ))}
          </select>
        ) : (
          <input type="text" disabled value={'From: ' + user.base.name} />
        )}

        <select required value={form.toBaseId || ''} onChange={handleChange('toBaseId')}>
          <option value="">To base</option>
          {bases.map((base) => (
            <option key={base.id} value={base.id}>{base.name}</option>
          ))}
        </select>

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

        <button type="submit" className="btn">Transfer</button>

        {errorMessage && <p className="error-text">{errorMessage}</p>}
      </form>

      <h3>Transfer History</h3>
      <DataTable rows={transfers} columns={transferColumns} />
    </div>
  );
}

export default TransferPage;
