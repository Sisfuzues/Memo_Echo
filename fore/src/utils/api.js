import { getAuthToken } from '@/utils/auth';

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

  return fetch(path, requestOptions);
};
