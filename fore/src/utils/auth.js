const TOKEN_KEY = 'memoecho_auth_token';
const USER_ID_KEY = 'memoecho_user_id';

const getStorage = (persistent) => (persistent ? window.localStorage : window.sessionStorage);

const removeFromAllStorages = (key) => {
  window.localStorage.removeItem(key);
  window.sessionStorage.removeItem(key);
};

export const setAuthToken = (token, rememberMe = true) => {
  removeFromAllStorages(TOKEN_KEY);
  getStorage(rememberMe).setItem(TOKEN_KEY, token);
};

export const getAuthToken = () =>
  window.localStorage.getItem(TOKEN_KEY) || window.sessionStorage.getItem(TOKEN_KEY) || '';

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
