import { useEffect, useState } from "react";
import { Link } from "react-router-dom";
import { toast } from "sonner";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Button } from "@/components/ui/button";
import { useAuth } from "@/app/auth";
import { getPosts, type PostListItem, type Page } from "@/api/postsApi";

export default function PostsPage() {
    const [page, setPage] = useState<Page<PostListItem> | null>(null);
    const [error, setError] = useState<string | null>(null);

    const { isAuthenticated } = useAuth();

    useEffect(() => {
        let cancelled = false;

        async function load() {
            try {
                setError(null);
                const data = await getPosts({ page: 0, size: 20 });
                if (!cancelled) setPage(data);
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

    const posts = page?.content ?? [];

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

            {page === null && !error && (
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
                    <CardContent className="text-sm text-muted-foreground">{error}</CardContent>
                </Card>
            )}

            {page && posts.length === 0 && (
                <Card>
                    <CardHeader>
                        <CardTitle>No posts yet</CardTitle>
                    </CardHeader>
                    <CardContent className="text-sm text-muted-foreground">
                        Be the first to create one.
                    </CardContent>
                </Card>
            )}

            {page && posts.length > 0 && (
                <div className="grid gap-4">
                    {posts.map((p) => (
                        <Link key={p.id} to={`/posts/${p.id}`} className="block">
                            <Card className="transition-shadow hover:shadow-md">
                                <CardHeader className="space-y-1">
                                    <CardTitle className="text-lg">{p.title}</CardTitle>
                                    <div className="text-xs text-muted-foreground">
                                        {p.category}
                                        {p.location ? ` • ${p.location}` : ""}
                                        {p.priceInfo ? ` • ${p.priceInfo}${p.currency ? " " + p.currency : ""}` : ""}
                                    </div>
                                </CardHeader>

                                <CardContent className="text-sm text-muted-foreground">
                                    <div className="flex items-center justify-between gap-4">
                                        <div>
                                            <div className="text-xs">
                                                {p.owner?.username ? `by ${p.owner.username}` : ""}
                                            </div>
                                            <div className="text-xs">
                                                {p.createdAt ? new Date(p.createdAt).toLocaleString() : ""}
                                            </div>
                                        </div>
                                        <div className="text-xs">{p.status}</div>
                                    </div>
                                </CardContent>
                            </Card>
                        </Link>
                    ))}
                </div>
            )}
        </div>
    );
}
