import { useEffect, useState } from "react";
import { Link, useParams } from "react-router-dom";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { getPost, type PostDetails } from "@/api/postsApi";

function Row({ label, value }: { label: string; value?: string }) {
    if (!value) return null;
    return (
        <div className="flex gap-2 text-sm">
            <div className="w-28 text-muted-foreground">{label}</div>
            <div className="min-w-0 break-words">{value}</div>
        </div>
    );
}

export default function PostDetailsPage() {
    const { id } = useParams<{ id: string }>();

    const [post, setPost] = useState<PostDetails | null>(null);
    const [error, setError] = useState<string | null>(null);

    useEffect(() => {
        if (!id) {
            setError("Missing post id");
            return;
        }

        const postId = id;
        let cancelled = false;

        async function load() {
            try {
                setError(null);
                const data = await getPost(postId);
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
                <div className="space-y-4">
                    <Card>
                        <CardHeader className="space-y-1">
                            <CardTitle className="text-2xl">{post.title}</CardTitle>
                            <div className="text-sm text-muted-foreground">
                                {post.category} • {post.status}
                                {post.owner?.username ? ` • by ${post.owner.username}` : ""}
                            </div>
                        </CardHeader>

                        <CardContent className="space-y-4">
                            {/* Images */}
                            {post.images?.length > 0 && (
                                <div className="grid grid-cols-2 sm:grid-cols-3 gap-2">
                                    {post.images.map((url, idx) => (
                                        <img
                                            key={idx}
                                            src={url}
                                            alt=""
                                            className="h-40 w-full rounded-md object-cover border"
                                        />
                                    ))}
                                </div>
                            )}

                            {/* Description */}
                            <div className="text-sm whitespace-pre-wrap">
                                {post.description ?? "No description"}
                            </div>

                            {/* Meta */}
                            <div className="space-y-2">
                                <Row label="Location" value={post.location} />
                                <Row
                                    label="Price"
                                    value={post.priceInfo ? `${post.priceInfo}${post.currency ? " " + post.currency : ""}` : undefined}
                                />
                                <Row label="Condition" value={post.condition ?? undefined} />
                                <Row label="Contact" value={post.contactInfo ?? undefined} />
                                <Row label="Views" value={String(post.viewCount)} />
                                <Row label="Created" value={post.createdAt ? new Date(post.createdAt).toLocaleString() : undefined} />
                                <Row label="Updated" value={post.updatedAt ? new Date(post.updatedAt).toLocaleString() : undefined} />
                            </div>

                            {/* Tags */}
                            {post.tags?.length > 0 && (
                                <div className="flex flex-wrap gap-2">
                                    {post.tags.map((t) => (
                                        <span
                                            key={t}
                                            className="rounded-full border px-2 py-1 text-xs text-muted-foreground"
                                        >
                      {t}
                    </span>
                                    ))}
                                </div>
                            )}
                        </CardContent>
                    </Card>
                </div>
            )}
        </div>
    );
}
