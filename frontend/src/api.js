// Base url of the backend.
// In development it is empty because vite proxy handles /api calls.
const BASE_URL = import.meta.env.VITE_API_URL || '';

// Converts an object to query string.
// Example: { base: 1, from: '' } -> "?base=1"   (empty values are skipped)
export function buildQueryString(filters) {
  const params = new URLSearchParams();

  Object.entries(filters).forEach(([key, value]) => {
    if (value) {
      params.append(key, value);
    }
  });

  const queryString = params.toString();
  return queryString ? '?' + queryString : '';
}

// Common function to call the backend. It adds the token automatically.
export async function apiCall(path, options = {}) {
  const token = localStorage.getItem('token');

  const headers = { 'Content-Type': 'application/json' };
  if (token) {
    headers['Authorization'] = 'Bearer ' + token;
  }

  const response = await fetch(BASE_URL + path, {
    ...options,
    headers: headers,
    body: options.body ? JSON.stringify(options.body) : undefined,
  });

  // token expired or wrong, so send the user back to login page
  if (response.status === 401 && path !== '/api/auth/login') {
    localStorage.clear();
    window.location.reload();
  }

  if (!response.ok) {
    let errorMessage = response.statusText;
    try {
      const errorData = await response.json();
      errorMessage = errorData.message || errorMessage;
    } catch (e) {
      // response had no json, so we use the default message
    }
    throw new Error(errorMessage);
  }

  return response.json();
}
