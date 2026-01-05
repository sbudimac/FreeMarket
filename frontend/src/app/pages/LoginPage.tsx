import {useNavigate} from "react-router-dom";
import {useState} from "react";
import * as React from "react";
import { http } from "@/api/http";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import {Button} from "@/components/ui/button.tsx";
import { toast } from "sonner";

type LoginResponse = {
    token: string;
    username: string;
    roles: string[];
};

export default function LoginPage() {
    const navigate = useNavigate();
    const [username, setUsername] = useState("");
    const [password, setPassword] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const data = await http<LoginResponse>("/login", {
                method: "POST",
                body: JSON.stringify({username, password}),
            });

            localStorage.setItem("fm_token", data.token);
            localStorage.setItem("fm_username", data.username);

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
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="password">Password</Label>
                            <Input
                                id="password"
                                type="password"
                                autoComplete="current-password"
                                value={password}
                                onChange={(e) => setPassword(e.target.value)}
                                required
                            />
                        </div>

                        <Button className="w-full" type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Logging in..." : "Login"}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}