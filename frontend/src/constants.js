// Equipment types (same names as in the backend enum)
export const EQUIPMENT_TYPES = ['VEHICLE', 'WEAPON', 'AMMUNITION'];

// Roles (same names as in the backend enum)
export const ROLE_ADMIN = 'ADMIN';
export const ROLE_COMMANDER = 'BASE_COMMANDER';
export const ROLE_LOGISTICS = 'LOGISTICS_OFFICER';

// Small helper: today's date in yyyy-mm-dd format (needed for date input)
export function getTodayDate() {
  return new Date().toISOString().slice(0, 10);
}

// Small helper: TRANSFER_IN -> TRANSFER IN
export function makeReadable(text) {
  return text ? text.replaceAll('_', ' ') : '';
}
