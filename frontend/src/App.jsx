import { useState, useEffect } from 'react';
import { apiCall } from './api';
import { ROLE_ADMIN, ROLE_COMMANDER, ROLE_LOGISTICS, makeReadable } from './constants';
import LoginPage from './components/LoginPage';
import DashboardPage from './components/DashboardPage';
import TransactionPage from './components/TransactionPage';
import TransferPage from './components/TransferPage';

// What each role is allowed to open (RBAC on the frontend side)
const ADMIN_AND_COMMANDER = [ROLE_ADMIN, ROLE_COMMANDER];
const ALL_ROLES = [ROLE_ADMIN, ROLE_COMMANDER, ROLE_LOGISTICS];

// Menu items shown in the navbar
const MENU_ITEMS = [
  { id: 'dashboard', label: 'Dashboard', roles: ADMIN_AND_COMMANDER },
  { id: 'purchases', label: 'Purchases', roles: ALL_ROLES },
  { id: 'transfers', label: 'Transfers', roles: ALL_ROLES },
  { id: 'assignments', label: 'Assignments & Expenditures', roles: ADMIN_AND_COMMANDER },
];

// What can be added on the Purchases page
const PURCHASE_KINDS = [
  { title: 'Purchases', label: 'Record purchase', endpoint: '/api/purchases', type: 'PURCHASE' },
];

// What can be added on the Assignments & Expenditures page
const ASSIGNMENT_KINDS = [
  { title: 'Assignments', label: 'Assign asset', endpoint: '/api/assignments', type: 'ASSIGNED' },
  { title: 'Expenditures', label: 'Record expenditure', endpoint: '/api/expenditures', type: 'EXPENDED' },
];

function App() {
  // if user already logged in earlier, get the details from the browser storage
  const [user, setUser] = useState(JSON.parse(localStorage.getItem('user') || 'null'));
  const [bases, setBases] = useState([]);
  const [selectedPage, setSelectedPage] = useState(null);

  // load the bases list once the user is logged in
  useEffect(() => {
    if (user) {
      apiCall('/api/bases').then(setBases);
    }
  }, [user]);

  // if not logged in, show only the login page
  if (!user) {
    return <LoginPage onLoginSuccess={setUser} />;
  }

  // only show the menu items that this role can use
  const allowedMenuItems = MENU_ITEMS.filter((item) => item.roles.includes(user.role));

  // first allowed page is opened by default
  const currentPage = selectedPage || allowedMenuItems[0]?.id;

  const handleLogout = () => {
    localStorage.clear();
    setUser(null);
    setSelectedPage(null);
  };

  return (
    <div>
      <header className="navbar">
        <h1 className="navbar-title">MAMS</h1>

        <nav className="nav-buttons">
          {allowedMenuItems.map((item) => (
            <button
              key={item.id}
              className={currentPage === item.id ? 'nav-btn active' : 'nav-btn'}
              onClick={() => setSelectedPage(item.id)}
            >
              {item.label}
            </button>
          ))}
        </nav>

        <div className="user-info">
          <span>
            {user.username} ({makeReadable(user.role)}
            {user.base?.name ? ' - ' + user.base.name : ''})
          </span>
          <button className="btn logout-btn" onClick={handleLogout}>Logout</button>
        </div>
      </header>

      <main className="page-content">
        {currentPage === 'dashboard' && <DashboardPage user={user} bases={bases} />}

        {currentPage === 'purchases' && (
          <TransactionPage kinds={PURCHASE_KINDS} user={user} bases={bases} showPersonnel={false} />
        )}

        {currentPage === 'transfers' && <TransferPage user={user} bases={bases} />}

        {currentPage === 'assignments' && (
          <TransactionPage kinds={ASSIGNMENT_KINDS} user={user} bases={bases} showPersonnel={true} />
        )}
      </main>
    </div>
  );
}

export default App;
