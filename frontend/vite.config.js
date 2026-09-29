import { defineConfig } from 'vite';
import react from '@vitejs/plugin-react';

// The proxy sends every /api call to the spring boot backend.
// Because of this we don't get CORS / "Failed to fetch" problems in development.
export default defineConfig({
  plugins: [react()],
  server: {
    proxy: {
      '/api': 'http://localhost:8080',
    },
  },
});
