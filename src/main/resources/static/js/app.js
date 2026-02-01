/**
 * Urban Waste Manager - Core Frontend Architecture
 * Handles Authentication, API interception, and Global State.
 */

const App = (function() {
    // --- Configuration ---
    const STORAGE_KEYS = {
        TOKEN: 'uwm_token',
        ROLE: 'uwm_role',
        EMAIL: 'uwm_email',
        USER_ID: 'uwm_user_id'
    };

    const ROUTES = {
        LOGIN: '/ui/login',
        ADMIN_DASH: '/ui/admin/dashboard',
        DRIVER_DASH: '/ui/driver/dashboard',
        USER_DASH: '/ui/user/dashboard',
        HOME: '/'
    };

    // --- Auth Service ---
    const AuthService = {
        saveSession(data) {
            localStorage.setItem(STORAGE_KEYS.TOKEN, data.token);
            localStorage.setItem(STORAGE_KEYS.ROLE, data.role);
            localStorage.setItem(STORAGE_KEYS.EMAIL, data.email);
            if (data.id) localStorage.setItem(STORAGE_KEYS.USER_ID, data.id);
        },

        clearSession() {
            localStorage.removeItem(STORAGE_KEYS.TOKEN);
            localStorage.removeItem(STORAGE_KEYS.ROLE);
            localStorage.removeItem(STORAGE_KEYS.EMAIL);
            localStorage.removeItem(STORAGE_KEYS.USER_ID);
        },

        getToken() {
            return localStorage.getItem(STORAGE_KEYS.TOKEN);
        },

        getRole() {
            return localStorage.getItem(STORAGE_KEYS.ROLE);
        },

        getEmail() {
            return localStorage.getItem(STORAGE_KEYS.EMAIL);
        },

        getUserId() {
            return localStorage.getItem(STORAGE_KEYS.USER_ID);
        },

        isAuthenticated() {
            return !!this.getToken();
        },

        requireRole(requiredRole) {
            if (!this.isAuthenticated()) {
                window.location.href = ROUTES.LOGIN;
                return;
            }
            const currentRole = this.getRole();
            if (currentRole !== requiredRole) {
                if (confirm(`Access Denied. You are logged in as ${currentRole}. Do you want to logout and switch accounts?`)) {
                    this.logout();
                } else {
                    this.redirectBasedOnRole();
                }
            }
        },

        redirectBasedOnRole() {
            const role = this.getRole();
            if (role === 'ADMIN') window.location.href = ROUTES.ADMIN_DASH;
            else if (role === 'DRIVER') window.location.href = ROUTES.DRIVER_DASH;
            else window.location.href = ROUTES.USER_DASH;
        },

        logout() {
            this.clearSession();
            window.location.href = ROUTES.LOGIN;
        }
    };

    // --- API Client (Interceptor) ---
    const ApiClient = {
        async request(endpoint, options = {}) {
            const headers = {
                'Content-Type': 'application/json',
                ...options.headers
            };

            // 1. Auto-attach Token
            const token = AuthService.getToken();
            if (token) {
                headers['Authorization'] = `Bearer ${token}`;
            }

            const config = {
                ...options,
                headers
            };

            try {
                const response = await fetch(endpoint, config);

                // 2. Global Error Handling
                if (response.status === 401 && !options.skipAuthCheck) {
                    console.warn('Session expired. Redirecting to login.');
                    AuthService.logout();
                    return null;
                }

                if (response.status === 403) {
                    alert('You do not have permission to perform this action.');
                    throw new Error('Forbidden');
                }

                if (!response.ok) {
                    const errorData = await response.json().catch(() => ({}));
                    throw new Error(errorData.message || `API Error: ${response.status}`);
                }

                // Handle 204 No Content
                if (response.status === 204) {
                    return null;
                }

                const text = await response.text();
                return text ? JSON.parse(text) : null;

            } catch (error) {
                console.error('API Request Failed:', error);
                throw error;
            }
        },

        get(endpoint, options = {}) {
            return this.request(endpoint, { method: 'GET', ...options });
        },

        post(endpoint, body, options = {}) {
            return this.request(endpoint, { method: 'POST', body: JSON.stringify(body), ...options });
        },

        put(endpoint, body, options = {}) {
            return this.request(endpoint, { method: 'PUT', body: JSON.stringify(body), ...options });
        },
        
        delete(endpoint, options = {}) {
            return this.request(endpoint, { method: 'DELETE', ...options });
        }
    };

    return {
        Auth: AuthService,
        Api: ApiClient
    };
})();
