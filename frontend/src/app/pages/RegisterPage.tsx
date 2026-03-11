import { useNavigate } from "react-router-dom";
import { useState } from "react";
import * as React from "react";
import { http } from "@/api/http";
import { toast } from "sonner";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card.tsx";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Button } from "@/components/ui/button.tsx";


function getUsernameError(username: string): string | null {
    if (username.length === 0) return null;
    if (username.length < 3) return "Username must be at least 3 characters.";
    if (username.length > 50) return "Username must be at most 50 characters.";
    if (!/^[a-zA-Z0-9_]+$/.test(username)) return "Username can only contain letters, numbers, and underscores.";
    return null;
}

function getPasswordErrors(password: string): string[] {
    if (password.length === 0) return [];
    const errors: string[] = [];
    if (password.length < 8) errors.push("At least 8 characters");
    if (!/[A-Z]/.test(password)) errors.push("At least one uppercase letter");
    if (!/[a-z]/.test(password)) errors.push("At least one lowercase letter");
    if (!/\d/.test(password)) errors.push("At least one number");
    return errors;
}

function getEmailError(email: string): string | null {
    if (email.length === 0) return null;
    if (!/^[A-Za-z0-9+_.-]+@(.+)$/.test(email)) return "Please enter a valid email address.";
    return null;
}


export default function RegisterPage() {
    const navigate = useNavigate();

    const [username, setUsername] = useState("");
    const [email, setEmail] = useState("");
    const [password, setPassword] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    const [touchedUsername, setTouchedUsername] = useState(false);
    const [touchedEmail, setTouchedEmail] = useState(false);
    const [touchedPassword, setTouchedPassword] = useState(false);

    const usernameError = getUsernameError(username);
    const emailError = getEmailError(email);
    const passwordErrors = getPasswordErrors(password);

    const canSubmit =
        !isSubmitting &&
        username.length >= 3 &&
        email.length > 0 &&
        !usernameError &&
        !emailError &&
        passwordErrors.length === 0;

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();

        setTouchedUsername(true);
        setTouchedEmail(true);
        setTouchedPassword(true);

        if (!canSubmit) return;

        setIsSubmitting(true);
        try {
            await http("/auth/register", {
                method: "POST",
                body: JSON.stringify({ username, email, password }),
            });

            toast.success("Account created, you may log in now.");
            navigate("/login");
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Registration failed");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="mx-auto w-full max-w-md">
            <Card>
                <CardHeader>
                    <CardTitle>Create account</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={onSubmit} className="space-y-4">

                        {/* Username */}
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
                            {touchedUsername && usernameError && (
                                <p className="text-xs text-destructive">{usernameError}</p>
                            )}
                        </div>

                        {/* Email */}
                        <div className="space-y-2">
                            <Label htmlFor="email">Email</Label>
                            <Input
                                id="email"
                                type="email"
                                autoComplete="email"
                                value={email}
                                onChange={(e) => setEmail(e.target.value)}
                                onBlur={() => setTouchedEmail(true)}
                                required
                            />
                            {touchedEmail && emailError && (
                                <p className="text-xs text-destructive">{emailError}</p>
                            )}
                        </div>

                        {/* Password */}
                        <div className="space-y-2">
                            <Label htmlFor="password">Password</Label>
                            <Input
                                id="password"
                                type="password"
                                autoComplete="new-password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                onBlur={() => setTouchedPassword(true)}
                                required
                            />
                            {touchedPassword && passwordErrors.length > 0 && (
                                <ul className="space-y-1">
                                    {passwordErrors.map((err) => (
                                        <li key={err} className="text-xs text-destructive">
                                            • {err}
                                        </li>
                                    ))}
                                </ul>
                            )}
                            {(!touchedPassword || passwordErrors.length === 0) && (
                                <p className="text-xs text-muted-foreground">
                                    Min 8 characters, must include uppercase, lowercase, and a number.
                                </p>
                            )}
                        </div>

                        <Button className="w-full" type="submit" disabled={!canSubmit}>
                            {isSubmitting ? "Creating..." : "Register"}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}