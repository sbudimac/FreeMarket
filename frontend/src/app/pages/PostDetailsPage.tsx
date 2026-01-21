import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { getPost, type Post } from "@/api/postsApi";

export default function PostDetailsPage() {
    const params = useParams();
    const id = Number(params.id);

    const [post, setPost] = useState<Post | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!Number.isFinite(id)) {
            setError("Invalid post id");
        }

        let cancelled = false;

        async function load() {
            try {
                setError(null);
                const data = await getPost(id);
                if (!cancelled) setPost(data);
            } catch (err) {
                const msg = err instanceof Error ? err.message : "Failed to load post";
                if (!cancelled) setError(msg);
                toast.error(msg);
            }
        }

        load();

        return () => {
            cancelled = true;
        };
    }, [id]);

    return (
        <div className="space-y-4">
            <div>
                <Button asChild variant="ghost">
                    <Link to="/posts">← Back to posts</Link>
                </Button>
            </div>

            {!post && !error && (
                <div className="space-y-3">
                    <Skeleton className="h-10 w-1/2" />
                    <Skeleton className="h-40 w-full" />
                </div>
            )}

            {error && (
                <Card>
                    <CardHeader>
                        <CardTitle>Could not load post</CardTitle>
                    </CardHeader>
                    <CardContent className="text-sm text-muted-foreground">{error}</CardContent>
                </Card>
            )}

            {post && (
                <Card>
                    <CardHeader>
                        <CardTitle>{post.title}</CardTitle>
                    </CardHeader>
                    <CardContent className="text-sm text-muted-foreground">
                        {post.description ?? "No description"}
                    </CardContent>
                </Card>
            )}
        </div>
    );
}