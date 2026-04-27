const TOKEN_KEY = 'memoecho_auth_token';
const USER_ID_KEY = 'memoecho_user_id';

const getStorage = (persistent) => (persistent ? window.localStorage : window.sessionStorage);

const removeFromAllStorages = (key) => {
  window.localStorage.removeItem(key);
  window.sessionStorage.removeItem(key);
};

const decodeJwtPayload = (token) => {
  const payload = token.split('.')[1];
  if (!payload) {
    return null;
  }

  const base64 = payload.replace(/-/g, '+').replace(/_/g, '/');
  const paddedBase64 = base64.padEnd(base64.length + ((4 - (base64.length % 4)) % 4), '=');
  return JSON.parse(window.atob(paddedBase64));
};

export const setAuthToken = (token, rememberMe = true) => {
  removeFromAllStorages(TOKEN_KEY);
  getStorage(rememberMe).setItem(TOKEN_KEY, token);
};

export const isTokenExpired = (token) => {
  if (!token) {
    return true;
  }

  try {
    const payload = decodeJwtPayload(token);
    if (!payload?.exp) {
      return false;
    }

    return payload.exp * 1000 <= Date.now();
  } catch {
    return true;
  }
};

export const getAuthToken = () => {
  const token = window.localStorage.getItem(TOKEN_KEY) || window.sessionStorage.getItem(TOKEN_KEY) || '';
  if (token && isTokenExpired(token)) {
    clearAuth();
    return '';
  }

  return token;
};

export const setCurrentUserId = (userId, rememberMe = true) => {
  removeFromAllStorages(USER_ID_KEY);
  getStorage(rememberMe).setItem(USER_ID_KEY, userId);
};

export const getCurrentUserId = () =>
  window.localStorage.getItem(USER_ID_KEY) || window.sessionStorage.getItem(USER_ID_KEY) || '';

export const clearAuth = () => {
  removeFromAllStorages(TOKEN_KEY);
  removeFromAllStorages(USER_ID_KEY);
};
