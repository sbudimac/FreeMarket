import {toast} from "sonner";

function redirectToLogin() {
    window.location.assign("/auth/login");
}

function clearAuth() {
    localStorage.removeItem("fm_token");
}

export async function http<T>(path: string, options: RequestInit = {}): Promise<T> {
    const token = localStorage.getItem("fm_token");

    const res = await fetch(path, {
        headers: {
            "Content-Type": "application/json",
            ...(token ? { Authorization: `Bearer ${token}` } : {}),
            ...(options.headers ?? {}),
        },
        ...options,
    });

    if (res.status === 401) {
        clearAuth();
        toast.error("Session expired. Please log in again.");
        redirectToLogin();
        throw new Error("Unauthorized");
    }

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Request failed (${res.status})`);
    }

    if (!res.ok) {
        const contentType = res.headers.get("content-type") ?? "";
        if (contentType.includes("application/json")) {
            const body = await res.json().catch(() => null);
            const msg =
                body?.message ??
                body?.error ??
                `Request failed (${res.status})`;
            throw new Error(msg);
        } else {
            const text = await res.text().catch(() => "");
            throw new Error(text || `Request failed (${res.status})`);
        }
    }

    const contentType = res.headers.get("content-type") ?? "";
    if (!contentType.includes("application/json")) {
        return undefined as T;
    }

    return (await res.json()) as T;
}
