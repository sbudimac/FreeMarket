import { useNavigate } from "react-router-dom";
import { useState } from "react";
import * as React from "react";
import { http } from "@/api/http";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button.tsx";
import { toast } from "sonner";
import { useAuth } from "@/app/auth.tsx";

type LoginResponse = {
    accessToken: string;
    username: string;
    roles: string[];
};

export default function LoginPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);
    const { login } = useAuth();

    const [touchedUsername, setTouchedUsername] = useState(false);
    const [touchedPassword, setTouchedPassword] = useState(false);

    const usernameError = touchedUsername && username.length < 3
        ? "Username must be at least 3 characters."
        : null;

    const passwordError = touchedPassword && password.length < 8
        ? "Password must be at least 8 characters."
        : null;

    const canSubmit =
        !isSubmitting &&
        username.length >= 3 &&
        password.length >= 8;

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();

        setTouchedUsername(true);
        setTouchedPassword(true);

        if (!canSubmit) return;

        setIsSubmitting(true);
        try {
            const data = await http<LoginResponse>("/auth/login", {
                method: "POST",
                body: JSON.stringify({ username, password }),
            });

            login(data.accessToken, data.username);
            toast.success(`Welcome back, ${data.username}!`);
            navigate("/");
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Login failed");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="mx-auto w-full max-w-md">
            <Card>
                <CardHeader>
                    <CardTitle>Login</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={onSubmit} className="space-y-4">

                        <div className="space-y-2">
                            <Label htmlFor="username">Username</Label>
                            <Input
                                id="username"
                                autoComplete="username"
                                value={username}
                                onChange={(e) => setUsername(e.target.value)}
                                onBlur={() => setTouchedUsername(true)}
                                required
                            />
                            {usernameError && (
                                <p className="text-xs text-destructive">{usernameError}</p>
                            )}
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Password</Label>
                            <Input
                                id="password"
                                type="password"
                                autoComplete="current-password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onBlur={() => setTouchedPassword(true)}
                                required
                            />
                            {passwordError && (
                                <p className="text-xs text-destructive">{passwordError}</p>
                            )}
                        </div>

                        <Button className="w-full" type="submit" disabled={!canSubmit}>
                            {isSubmitting ? "Logging in..." : "Login"}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}