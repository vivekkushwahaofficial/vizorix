const API_BASE_URL = 'http://localhost:8080/api';

// Captures auth tokens passed via query parameters for dev/testing flows
if (typeof window !== 'undefined') {
  const queryToken = new URLSearchParams(window.location.search).get('token');
  if (queryToken) {
    localStorage.setItem('vizorix-token', queryToken);
  }
}

interface RequestOptions extends RequestInit {
  params?: Record<string, string | number>;
}

/**
 * Standard API client configured with basic base URL and automatically
 * injecting JWT credentials if available in localStorage.
 * Uses native fetch to avoid external dependencies.
 */
export const apiClient = {
  async request<T>(path: string, options: RequestOptions = {}): Promise<{ data: T }> {
    const token = localStorage.getItem('vizorix-token');
    const headers = new Headers(options.headers);
    
    if (token) {
      headers.set('Authorization', `Bearer ${token}`);
    }
    
    if (options.body && !(options.body instanceof FormData)) {
      headers.set('Content-Type', 'application/json');
    }

    let url = `${API_BASE_URL}${path}`;
    if (options.params) {
      const searchParams = new URLSearchParams();
      Object.entries(options.params).forEach(([key, val]) => {
        searchParams.append(key, String(val));
      });
      url += `?${searchParams.toString()}`;
    }

    const response = await fetch(url, {
      ...options,
      headers,
    });

    if (!response.ok) {
      // Create a mock error response to maintain interface compatibility with catch-blocks
      const errorData = await response.json().catch(() => ({}));
      const error = new Error(errorData.message || 'Request failed');
      (error as any).response = { data: errorData, status: response.status };
      throw error;
    }

    // 204 No Content has no body
    if (response.status === 204) {
      return { data: null as unknown as T };
    }

    const data = await response.json();
    return { data };
  },

  get<T>(path: string, options?: RequestOptions) {
    return this.request<T>(path, { ...options, method: 'GET' });
  },

  post<T>(path: string, body?: any, options?: RequestOptions) {
    return this.request<T>(path, {
      ...options,
      method: 'POST',
      body: body ? JSON.stringify(body) : undefined,
    });
  },

  put<T>(path: string, body?: any, options?: RequestOptions) {
    return this.request<T>(path, {
      ...options,
      method: 'PUT',
      body: body ? JSON.stringify(body) : undefined,
    });
  },

  delete<T>(path: string, options?: RequestOptions) {
    return this.request<T>(path, { ...options, method: 'DELETE' });
  },
};
