import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { getPosts, type Post } from "@/api/postsApi";
import {useAuth} from "@/app/auth.tsx";
import {Button} from "@/components/ui/button.tsx";

export default function PostsPage() {
    const [posts, setPosts] = useState<Post[] | null>(null);
    const [error, setError] = useState<string | null>(null);

    const { isAuthenticated } = useAuth();

    useEffect(() => {
        let cancelled = false;

        async function load() {
            try {
                setError(null);
                const data = await getPosts();
                if (!cancelled) setPosts(data);
            } catch (err) {
                const msg = err instanceof Error ? err.message : "Failed to load posts";
                if (!cancelled) setError(msg);
                toast.error(msg);
            }
        }

        load();

        return () => {
            cancelled = true;
        };
    }, []);

    return (
        <div className="space-y-4">
            <div className="flex items-end justify-between">
                <div>
                    <h1 className="text-2xl font-semibold">Posts</h1>
                    <p className="text-sm text-muted-foreground">
                        Browse what people are sharing on FreeMarket.
                    </p>
                </div>
                {isAuthenticated && (
                    <Button asChild>
                        <Link to="/posts/new">Create post</Link>
                    </Button>
                )}
            </div>

            {posts === null && !error && (
                <div className="space-y-3">
                    <Skeleton className="h-24 w-full" />
                    <Skeleton className="h-24 w-full" />
                    <Skeleton className="h-24 w-full" />
                </div>
            )}

            {error && (
                <Card>
                    <CardHeader>
                        <CardTitle>Could not load posts</CardTitle>
                    </CardHeader>
                    <CardContent className="text-sm text-muted-foreground">
                        {error}
                    </CardContent>
                </Card>
            )}

            {posts && posts.length === 0 && (
                <Card>
                    <CardHeader>
                        <CardTitle>No posts yet</CardTitle>
                    </CardHeader>
                    <CardContent className="text-sm text-muted-foreground">
                        Be the first to create one.
                    </CardContent>
                </Card>
            )}

            {posts && posts.length > 0 && (
                <div className="grid gap-4">
                    {posts.map((p) => (
                        <Link key={p.id} to={`/posts/${p.id}`} className="block">
                            <Card className="transition-shadow hover:shadow-md">
                                <CardHeader>
                                    <CardTitle className="text-lg">{p.title}</CardTitle>
                                </CardHeader>
                                <CardContent className="text-sm text-muted-foreground">
                                    {p.description ? p.description : "No description"}
                                </CardContent>
                            </Card>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
}