import { Menu } from "lucide-react";
import { Link, NavLink, Outlet } from "react-router-dom";

import { Sheet, SheetContent, SheetTrigger } from "@/components/ui/sheet";
import { Button } from "@/components/ui/button";
import { Separator } from "@/components/ui/separator";
import { Toaster } from "@/components/ui/sonner";

import {
    DropdownMenu,
    DropdownMenuContent,
    DropdownMenuItem,
    DropdownMenuTrigger,
} from "@/components/ui/dropdown-menu";

import { Avatar, AvatarFallback } from "@/components/ui/avatar";
import {useAuth} from "@/app/auth.tsx";
import {toast} from "sonner";


function NavItem({ to, label }: { to: string, label: string }) {
    return (
        <NavLink to={to}
                 className={({ isActive }) =>
            `text-sm transition-colors ${
                     isActive ? "text-foreground": "text-muted-foreground hover:text-foreground"
            }`
        }
        >
            {label}
        </NavLink>
    );
}

export default function AppShell() {
    const { auth, isAuthenticated, logout } = useAuth();

    return (
        <div className="min-h-screen bg-background">
            <header className="sticky top-0 z-50 w-full border-b bg-background/80 backdrop-blur">
                <div className="mx-auto flex h-14 max-w-6xl items-center justify-between px-4">
                    <div className="flex items-center gap-3">
                        <div className="md:hidden">
                            <Sheet>
                                <SheetTrigger asChild>
                                    <Button variant="ghost" size="icon" aria-label="Open menu">
                                        <Menu className="h-5 w-5"/>
                                    </Button>
                                </SheetTrigger>
                                <SheetContent side="left" className="w-72">
                                    <div className="flex items-center justify-between">
                                        <Link to="/" className="font-semibold">
                                            FreeMarket
                                        </Link>
                                    </div>
                                    <Separator className="my-4"/>
                                    <nav className="flex flex-col gap-3">
                                        <Link to="/" className="text-sm">
                                            Home
                                        </Link>
                                        <Link to="/login" className="text-sm">
                                            Login
                                        </Link>
                                        <Link to="/register" className="text-sm">
                                            Register
                                        </Link>
                                        <Link to="/posts" className="text-sm">
                                            Posts
                                        </Link>
                                    </nav>
                                </SheetContent>
                            </Sheet>
                        </div>

                        <Link to="/" className="font-semibold tracking-tight">
                            FreeMarket
                        </Link>

                        <nav className="hidden items-center gap-6 md:flex">
                            <NavItem to="/" label="Home"/>
                            <NavItem to="/posts" label="Posts"/>
                        </nav>
                    </div>

                    <div className="flex items-center gap-2">
                        <div className="hidden md:flex items-center gap-2">
                            {!isAuthenticated ? (
                                <>
                                    <Button asChild variant="ghost">
                                        <Link to="/login">Login</Link>
                                    </Button>
                                    <Button asChild>
                                        <Link to="/register">Register</Link>
                                    </Button>
                                </>
                            ) : (
                                <div className="text-sm text-muted-foreground">
                                    Signed in as <span className="text-foreground font-medium">{auth.username}</span>
                                </div>
                            )}
                        </div>


                        <DropdownMenu>
                            <DropdownMenuTrigger asChild>
                                <Button variant="ghost" size="icon" className="rounded-full">
                                    <Avatar className="h-8 w-8">
                                        <AvatarFallback>
                                            {auth.username ? auth.username.slice(0, 2).toUpperCase() : "FM"}
                                        </AvatarFallback>
                                    </Avatar>
                                </Button>
                            </DropdownMenuTrigger>
                            <DropdownMenuContent align="end">
                                <DropdownMenuItem asChild>
                                    <Link to="/">Profile (soon)</Link>
                                </DropdownMenuItem>
                                {isAuthenticated ? (
                                    <DropdownMenuItem
                                        onSelect={() => {
                                            logout();
                                            toast.success("Logged out");
                                        }}
                                    >
                                        Logout
                                    </DropdownMenuItem>
                                ) : (
                                    <DropdownMenuItem asChild>
                                        <Link to="/login">Login</Link>
                                    </DropdownMenuItem>
                                )}

                            </DropdownMenuContent>
                        </DropdownMenu>
                    </div>
                </div>
            </header>

            <main className="mx-auto max-w-6xl px-4 py-8">
                <Outlet />
            </main>
            <Toaster richColors />
        </div>
    );
}