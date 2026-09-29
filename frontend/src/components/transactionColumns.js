import { makeReadable } from '../constants';

// Columns for the transaction tables (used in dashboard popup, purchases, assignments)
export const transactionColumns = [
  { title: 'Date', getValue: (row) => row.txnDate },
  { title: 'Base', getValue: (row) => row.base?.name },
  { title: 'Type', getValue: (row) => makeReadable(row.type) },
  { title: 'Equipment', getValue: (row) => row.equipmentType },
  { title: 'Asset', getValue: (row) => row.assetName },
  { title: 'Qty', getValue: (row) => row.quantity },
  { title: 'Personnel', getValue: (row) => row.personnel || '-' },
  { title: 'By', getValue: (row) => row.createdBy },
];
