import { useState, useEffect } from 'react';
import { apiCall, buildQueryString } from '../api';
import { ROLE_ADMIN, makeReadable } from '../constants';
import FilterBar from './FilterBar';
import DataTable from './DataTable';
import { transactionColumns } from './transactionColumns';

// Dashboard page - shows the balance boxes and the Net Movement popup
function DashboardPage({ user, bases }) {
  const [filters, setFilters] = useState({});
  const [dashboardData, setDashboardData] = useState(null);
  const [popupRows, setPopupRows] = useState(null); // null means popup is closed

  const isAdmin = user.role === ROLE_ADMIN;

  // filters in the format that backend expects
  const queryFilters = {
    base: filters.base,
    equipmentType: filters.eq,
    from: filters.from,
    to: filters.to,
  };

  // load dashboard numbers whenever a filter changes
  useEffect(() => {
    apiCall('/api/dashboard' + buildQueryString(queryFilters)).then(setDashboardData);
  }, [filters]);

  // when user clicks on Net Movement box, get purchases and transfers and show them in a popup
  const openNetMovementPopup = async () => {
    const allRows = await apiCall('/api/txns' + buildQueryString(queryFilters));
    const movementTypes = ['PURCHASE', 'TRANSFER_IN', 'TRANSFER_OUT'];
    setPopupRows(allRows.filter((row) => movementTypes.includes(row.type)));
  };

  // one summary box. If onClick is given then the box is clickable
  const renderBox = (title, key, onClick) => (
    <div className={onClick ? 'summary-box clickable' : 'summary-box'} onClick={onClick}>
      <span>{title}</span>
      <b>{dashboardData ? dashboardData[key] : '...'}</b>
    </div>
  );

  return (
    <div>
      <h2>Dashboard</h2>

      <FilterBar filters={filters} setFilters={setFilters} bases={bases} isAdmin={isAdmin} />

      <div className="summary-boxes">
        {renderBox('Opening Balance', 'openingBalance')}
        {renderBox('Net Movement (click to see details)', 'netMovement', openNetMovementPopup)}
        {renderBox('Assigned', 'assigned')}
        {renderBox('Expended', 'expended')}
        {renderBox('Closing Balance', 'closingBalance')}
      </div>

      {/* popup - only shown when popupRows is not null */}
      {popupRows && (
        <div className="popup-overlay" onClick={() => setPopupRows(null)}>
          <div className="popup-box" onClick={(e) => e.stopPropagation()}>
            <h3>Net Movement Details</h3>

            {dashboardData && (
              <p>
                Purchases <b>{dashboardData.purchases}</b> + Transfer In <b>{dashboardData.transferIn}</b> −
                Transfer Out <b>{dashboardData.transferOut}</b> = <b>{dashboardData.netMovement}</b>
              </p>
            )}

            {['PURCHASE', 'TRANSFER_IN', 'TRANSFER_OUT'].map((type) => (
              <div key={type}>
                <h4>{makeReadable(type)}</h4>
                <DataTable
                  rows={popupRows.filter((row) => row.type === type)}
                  columns={transactionColumns}
                />
              </div>
            ))}

            <button className="btn" onClick={() => setPopupRows(null)}>Close</button>
          </div>
        </div>
      )}
    </div>
  );
}

export default DashboardPage;
