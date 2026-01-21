import {type FormEvent, useState} from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";

import { createPost } from "@/api/postsApi";
import { useAuth } from "@/app/auth";

export default function CreatePostPage() {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");
    const [isSubmitting, setIsSubmitting] = useState(false);

    if (!isAuthenticated) {
        return (
            <Card>
                <CardHeader>
                    <CardTitle>Login required</CardTitle>
                </CardHeader>
                <CardContent className="text-sm text-muted-foreground">
                    You must be logged in to create a post.
                </CardContent>
            </Card>
        );
    }

    async function onSubmit(e: FormEvent) {
        e.preventDefault();
        setIsSubmitting(true);

        try {
            const post = await createPost({
                title,
                description: description || undefined,
            });

            toast.success("Post created");
            navigate(`/posts/${post.id}`);
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to create post");
        } finally {
            setIsSubmitting(false);
        }
    }

    return (
        <div className="mx-auto w-full max-w-xl">
            <Card>
                <CardHeader>
                    <CardTitle>Create post</CardTitle>
                </CardHeader>
                <CardContent>
                    <form onSubmit={onSubmit} className="space-y-4">
                        <div className="space-y-2">
                            <Label htmlFor="title">Title</Label>
                            <Input
                                id="title"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                required
                            />
                        </div>

                        <div className="space-y-2">
                            <Label htmlFor="description">Description</Label>
                            <Textarea
                                id="description"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                rows={5}
                            />
                        </div>

                        <Button type="submit" disabled={isSubmitting}>
                            {isSubmitting ? "Creating..." : "Create post"}
                        </Button>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}