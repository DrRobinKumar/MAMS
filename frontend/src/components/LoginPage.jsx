import { useState } from 'react';
import { apiCall } from '../api';

// Login page - shown when the user is not logged in
function LoginPage({ onLoginSuccess }) {
  const [username, setUsername] = useState('');
  const [password, setPassword] = useState('');
  const [errorMessage, setErrorMessage] = useState('');

  const handleLogin = async (event) => {
    event.preventDefault(); // stop page refresh

    try {
      const data = await apiCall('/api/auth/login', {
        method: 'POST',
        body: { username: username, password: password },
      });

      // save token and user details in browser so login stays after refresh
      localStorage.setItem('token', data.token);
      localStorage.setItem('user', JSON.stringify(data));

      onLoginSuccess(data);
    } catch (error) {
      setErrorMessage(error.message);
    }
  };

  return (
    <form className="login-box" onSubmit={handleLogin}>
      <h2>MAMS Login</h2>
      <p className="login-subtitle">Military Asset Management System</p>

      <label>Username</label>
      <input
        type="text"
        placeholder="Enter username"
        value={username}
        onChange={(e) => setUsername(e.target.value)}
      />

      <label>Password</label>
      <input
        type="password"
        placeholder="Enter password"
        value={password}
        onChange={(e) => setPassword(e.target.value)}
      />

      <button type="submit" className="btn">Login</button>

      {errorMessage && <p className="error-text">{errorMessage}</p>}
    </form>
  );
}

export default LoginPage;
