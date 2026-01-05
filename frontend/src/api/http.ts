export async function http<T>(
    path: string,
    options: RequestInit = {}
): Promise<T> {
    const res = await fetch(path, {
        headers: {
            "Content-Type": "application/json",
            ...(options.headers ?? {}),
        },
        ...options,
    });

    if (!res.ok) {
        const text = await res.text();
        throw new Error(text || `Request failed (${res.status}`);
    }

    const contentType = res.headers.get("content-type") ?? "";
    if (!contentType.includes("application/json")) {
        return (undefined as T);
    }
    return (await res.json() as T);
}