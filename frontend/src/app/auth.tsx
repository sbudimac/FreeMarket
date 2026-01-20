import React, { createContext, useContext, useMemo, useState } from "react";

type AuthState = {
    token: string | null;
    username: string | null;
};

type AuthContextValue = {
    auth: AuthState;
    isAuthenticated: boolean;
    login: (token: string, username: string) => void;
    logout: () => void;
};

const AuthContext = createContext<AuthContextValue | null>(null);

function readInitialAuth(): AuthState {
    return {
        token: localStorage.getItem("fm_token"),
        username: localStorage.getItem("fm_username"),
    };
}

export function AuthProvider({ children }: { children: React.ReactNode }) {
    const [auth, setAuth] = useState<AuthState>(() => readInitialAuth());

    const value = useMemo<AuthContextValue>(() => {
        const isAuthenticated = !!auth.token;

        function login(token: string, username: string) {
            localStorage.setItem("fm_token", token);
            localStorage.setItem("fm_username", username);
            setAuth({ token, username });
        }

        function logout() {
            localStorage.removeItem("fm_token");
            localStorage.removeItem("fm_username");
            setAuth({ token: null, username: null });
        }

        return { auth, isAuthenticated, login, logout };
    }, [auth]);

    return <AuthContext.Provider value={value}>{children}</AuthContext.Provider>
}

export function useAuth() {
    const ctx = useContext(AuthContext);
    if (!ctx) {
        throw new Error("useAuth must be used within AuthProvider");
    }
    return ctx;
}