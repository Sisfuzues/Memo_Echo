import { clearAuth, getAuthToken } from '@/utils/auth';

export const apiFetch = async (path, options = {}) => {
  const {
    auth = true,
    json,
    headers: customHeaders,
    ...fetchOptions
  } = options;

  const headers = new Headers(customHeaders || {});
  const requestOptions = {
    ...fetchOptions,
    headers
  };

  if (json !== undefined) {
    headers.set('Content-Type', 'application/json');
    requestOptions.body = JSON.stringify(json);
  }

  if (auth) {
    const token = getAuthToken();
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
  }

  const response = await fetch(path, requestOptions);

  if (auth && response.status === 401) {
    clearAuth();
    if (window.location.pathname !== '/') {
      window.location.assign('/');
    }
  }

  return response;
};
