import { EQUIPMENT_TYPES } from '../constants';

// Filters used on the dashboard and on the list pages: dates, base and equipment type.
// "filters" is the current filter values and "setFilters" updates them.
function FilterBar({ filters, setFilters, bases, isAdmin }) {
  // update only one filter and keep the others as they are
  const updateFilter = (name, value) => {
    setFilters({ ...filters, [name]: value });
  };

  return (
    <div className="filter-row">
      <div className="filter-item">
        <label>From</label>
        <input
          type="date"
          value={filters.from || ''}
          onChange={(e) => updateFilter('from', e.target.value)}
        />
      </div>

      <div className="filter-item">
        <label>To</label>
        <input
          type="date"
          value={filters.to || ''}
          onChange={(e) => updateFilter('to', e.target.value)}
        />
      </div>

      {/* only admin can choose a base, other users are fixed to their own base */}
      {isAdmin && (
        <div className="filter-item">
          <label>Base</label>
          <select value={filters.base || ''} onChange={(e) => updateFilter('base', e.target.value)}>
            <option value="">All bases</option>
            {bases.map((base) => (
              <option key={base.id} value={base.id}>{base.name}</option>
            ))}
          </select>
        </div>
      )}

      <div className="filter-item">
        <label>Equipment</label>
        <select value={filters.eq || ''} onChange={(e) => updateFilter('eq', e.target.value)}>
          <option value="">All equipment</option>
          {EQUIPMENT_TYPES.map((type) => (
            <option key={type} value={type}>{type}</option>
          ))}
        </select>
      </div>
    </div>
  );
}

export default FilterBar;
