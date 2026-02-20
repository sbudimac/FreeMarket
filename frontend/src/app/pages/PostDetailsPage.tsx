import { useEffect, useMemo, useState } from "react";
import { Link, useNavigate, useParams } from "react-router-dom";
import { toast } from "sonner";

import { Button } from "@/components/ui/button";
import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Skeleton } from "@/components/ui/skeleton";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

import {
    getPost,
    updatePost,
    deletePost,
    type PostDetails,
    type PostCategory,
    type PostCondition,
    type UpdatePostRequest,
} from "@/api/postsApi";
import { useAuth } from "@/app/auth";

// ── constants ────────────────────────────────────────────────────────────────

const CATEGORY_OPTIONS: { value: PostCategory; label: string }[] = [
    { value: "PRODUCT_SUPPLY", label: "Product supply" },
    { value: "PRODUCT_DEMAND", label: "Product demand" },
    { value: "SERVICE_SUPPLY", label: "Service supply" },
    { value: "SERVICE_DEMAND", label: "Service demand" },
    { value: "DELIVERY_SUPPLY", label: "Delivery supply" },
    { value: "DELIVERY_DEMAND", label: "Delivery demand" },
];

const CONDITION_OPTIONS: { value: PostCondition; label: string }[] = [
    { value: "NEW", label: "New" },
    { value: "LIKE_NEW", label: "Like new" },
    { value: "USED", label: "Used" },
    { value: "FOR_PARTS", label: "For parts" },
];

// ── helpers ──────────────────────────────────────────────────────────────────

function normalizeTags(raw: string): string[] {
    return raw.split(",").map((t) => t.trim()).filter(Boolean).slice(0, 50);
}

function normalizeImages(raw: string): string[] {
    return raw.split("\n").map((s) => s.trim()).filter(Boolean).slice(0, 10);
}

function isValidHttpUrl(s: string): boolean {
    try {
        const u = new URL(s);
        return u.protocol === "http:" || u.protocol === "https:";
    } catch {
        return false;
    }
}

function Row({ label, value }: { label: string; value?: string }) {
    if (!value) return null;
    return (
        <div className="flex gap-2 text-sm">
            <div className="w-28 shrink-0 text-muted-foreground">{label}</div>
            <div className="min-w-0 break-words">{value}</div>
        </div>
    );
}

// ── edit form state ───────────────────────────────────────────────────────────

type EditState = {
    category: PostCategory;
    title: string;
    description: string;
    location: string;
    priceInfo: string;
    currency: string;
    contactInfo: string;
    condition: PostCondition | "";
    tagsRaw: string;
    imagesRaw: string;
    thumbnailUrl: string;
};

function postToEditState(post: PostDetails): EditState {
    return {
        category: post.category,
        title: post.title,
        description: post.description,
        location: post.location ?? "",
        priceInfo: post.priceInfo ?? "",
        currency: post.currency ?? "EUR",
        contactInfo: post.contactInfo ?? "",
        condition: (post.condition as PostCondition) ?? "",
        tagsRaw: post.tags?.join(", ") ?? "",
        imagesRaw: post.images?.join("\n") ?? "",
        thumbnailUrl: post.thumbnailUrl ?? "",
    };
}

// ── main component ────────────────────────────────────────────────────────────

export default function PostDetailsPage() {
    const { id } = useParams<{ id: string }>();
    const navigate = useNavigate();
    const { auth } = useAuth();

    const [post, setPost] = useState<PostDetails | null>(null);
    const [error, setError] = useState<string | null>(null);

    const [isEditing, setIsEditing] = useState(false);
    const [isSaving, setIsSaving] = useState(false);
    const [isDeleting, setIsDeleting] = useState(false);
    const [confirmDelete, setConfirmDelete] = useState(false);

    const [edit, setEdit] = useState<EditState | null>(null);

    // ── load ────────────────────────────────────────────────────────────────

    useEffect(() => {
        if (!id) { setError("Missing post id"); return; }
        let cancelled = false;

        async function load() {
            try {
                setError(null);
                const data = await getPost(id!);
                if (!cancelled) setPost(data);
            } catch (err) {
                const msg = err instanceof Error ? err.message : "Failed to load post";
                if (!cancelled) setError(msg);
                toast.error(msg);
            }
        }

        load();
        return () => { cancelled = true; };
    }, [id]);

    // ── derived ──────────────────────────────────────────────────────────────

    const isOwner = !!auth.username && auth.username === post?.owner?.username;

    const tags = useMemo(() => normalizeTags(edit?.tagsRaw ?? ""), [edit?.tagsRaw]);
    const images = useMemo(() => normalizeImages(edit?.imagesRaw ?? ""), [edit?.imagesRaw]);

    // simple field-level errors while editing
    const editErrors = edit
        ? {
            title:
                edit.title.length < 5 ? "Min 5 characters" :
                    edit.title.length > 200 ? "Max 200 characters" : null,
            description:
                edit.description.length < 10 ? "Min 10 characters" :
                    edit.description.length > 5000 ? "Max 5000 characters" : null,
            location: edit.location.length > 100 ? "Max 100 characters" : null,
            priceInfo: edit.priceInfo.length > 100 ? "Max 100 characters" : null,
            currency: edit.currency.length > 5 ? "Max 5 characters" : null,
            images: images.length > 10 ? "Max 10 images" :
                images.find((u) => !isValidHttpUrl(u)) ? `Invalid URL: ${images.find((u) => !isValidHttpUrl(u))}` : null,
            thumbnail: edit.thumbnailUrl && !isValidHttpUrl(edit.thumbnailUrl) ? "Invalid URL" : null,
        }
        : null;

    const canSave =
        edit != null &&
        edit.title.trim().length >= 5 &&
        edit.description.trim().length >= 10 &&
        !editErrors?.title &&
        !editErrors?.description &&
        !editErrors?.location &&
        !editErrors?.priceInfo &&
        !editErrors?.currency &&
        !editErrors?.images &&
        !editErrors?.thumbnail &&
        !isSaving;

    // ── handlers ─────────────────────────────────────────────────────────────

    function startEditing() {
        if (!post) return;
        setEdit(postToEditState(post));
        setIsEditing(true);
        setConfirmDelete(false);
    }

    function cancelEditing() {
        setIsEditing(false);
        setEdit(null);
    }

    function setField<K extends keyof EditState>(key: K, value: EditState[K]) {
        setEdit((prev) => prev ? { ...prev, [key]: value } : prev);
    }

    async function handleSave() {
        if (!post || !edit || !canSave) return;
        setIsSaving(true);
        try {
            const payload: UpdatePostRequest = {
                category: edit.category,
                title: edit.title.trim(),
                description: edit.description.trim(),
                location: edit.location.trim() || undefined,
                priceInfo: edit.priceInfo.trim() || undefined,
                currency: edit.currency.trim() || undefined,
                contactInfo: edit.contactInfo.trim() || undefined,
                condition: edit.condition || undefined,
                tags: tags.length ? tags : undefined,
                images: images.length ? images : undefined,
                thumbnailUrl: edit.thumbnailUrl.trim() || undefined,
            };
            const updated = await updatePost(post.id, payload);
            setPost(updated);
            setIsEditing(false);
            setEdit(null);
            toast.success("Post updated.");
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to update post");
        } finally {
            setIsSaving(false);
        }
    }

    async function handleDelete() {
        if (!post) return;
        if (!confirmDelete) {
            setConfirmDelete(true);
            return;
        }
        setIsDeleting(true);
        try {
            await deletePost(post.id);
            toast.success("Post deleted.");
            navigate("/posts");
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to delete post");
            setIsDeleting(false);
            setConfirmDelete(false);
        }
    }

    // ── render ────────────────────────────────────────────────────────────────

    return (
        <div className="space-y-4">
            <div>
                <Button asChild variant="ghost">
                    <Link to="/posts">← Back to posts</Link>
                </Button>
            </div>

            {/* Loading */}
            {!post && !error && (
                <div className="space-y-3">
                    <Skeleton className="h-10 w-1/2" />
                    <Skeleton className="h-40 w-full" />
                </div>
            )}

            {/* Error */}
            {error && (
                <Card>
                    <CardHeader><CardTitle>Could not load post</CardTitle></CardHeader>
                    <CardContent className="text-sm text-muted-foreground">{error}</CardContent>
                </Card>
            )}

            {/* Content */}
            {post && (
                <div className="space-y-4">
                    <Card>
                        <CardHeader className="space-y-1">
                            {/* ── VIEW: title ── */}
                            {!isEditing ? (
                                <>
                                    <CardTitle className="text-2xl">{post.title}</CardTitle>
                                    <div className="text-sm text-muted-foreground">
                                        {post.category} • {post.status}
                                        {post.owner?.username ? ` • by ${post.owner.username}` : ""}
                                    </div>
                                </>
                            ) : (
                                /* ── EDIT: title + category ── */
                                <div className="space-y-3">
                                    <div className="space-y-1">
                                        <Label htmlFor="edit-title">Title *</Label>
                                        <Input
                                            id="edit-title"
                                            value={edit!.title}
                                            onChange={(e) => setField("title", e.target.value)}
                                            maxLength={200}
                                        />
                                        {editErrors?.title && (
                                            <p className="text-xs text-destructive">{editErrors.title}</p>
                                        )}
                                    </div>

                                    <div className="space-y-1">
                                        <Label>Category *</Label>
                                        <Select
                                            value={edit!.category}
                                            onValueChange={(v) => setField("category", v as PostCategory)}
                                        >
                                            <SelectTrigger>
                                                <SelectValue />
                                            </SelectTrigger>
                                            <SelectContent>
                                                {CATEGORY_OPTIONS.map((c) => (
                                                    <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                                                ))}
                                            </SelectContent>
                                        </Select>
                                    </div>
                                </div>
                            )}

                            {/* Owner actions — only shown to post owner */}
                            {isOwner && (
                                <div className="flex items-center gap-2 pt-2">
                                    {!isEditing ? (
                                        <>
                                            <Button size="sm" variant="outline" onClick={startEditing}>
                                                Edit
                                            </Button>
                                            <Button
                                                size="sm"
                                                variant={confirmDelete ? "destructive" : "ghost"}
                                                onClick={handleDelete}
                                                disabled={isDeleting}
                                            >
                                                {isDeleting ? "Deleting…" : confirmDelete ? "Confirm delete?" : "Delete"}
                                            </Button>
                                            {confirmDelete && (
                                                <Button size="sm" variant="ghost" onClick={() => setConfirmDelete(false)}>
                                                    Cancel
                                                </Button>
                                            )}
                                        </>
                                    ) : (
                                        <>
                                            <Button size="sm" onClick={handleSave} disabled={!canSave}>
                                                {isSaving ? "Saving…" : "Save"}
                                            </Button>
                                            <Button size="sm" variant="ghost" onClick={cancelEditing} disabled={isSaving}>
                                                Cancel
                                            </Button>
                                        </>
                                    )}
                                </div>
                            )}
                        </CardHeader>

                        <CardContent className="space-y-4">
                            {/* Images */}
                            {post.images?.length > 0 && !isEditing && (
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

                            {/* ── VIEW: description ── */}
                            {!isEditing ? (
                                <div className="text-sm whitespace-pre-wrap">
                                    {post.description ?? "No description"}
                                </div>
                            ) : (
                                /* ── EDIT: all fields ── */
                                <div className="space-y-4">
                                    <div className="space-y-1">
                                        <Label htmlFor="edit-description">Description *</Label>
                                        <Textarea
                                            id="edit-description"
                                            value={edit!.description}
                                            onChange={(e) => setField("description", e.target.value)}
                                            rows={6}
                                        />
                                        {editErrors?.description && (
                                            <p className="text-xs text-destructive">{editErrors.description}</p>
                                        )}
                                    </div>

                                    <div className="grid gap-4 md:grid-cols-2">
                                        <div className="space-y-1">
                                            <Label htmlFor="edit-location">Location</Label>
                                            <Input
                                                id="edit-location"
                                                value={edit!.location}
                                                onChange={(e) => setField("location", e.target.value)}
                                                maxLength={100}
                                            />
                                            {editErrors?.location && (
                                                <p className="text-xs text-destructive">{editErrors.location}</p>
                                            )}
                                        </div>

                                        <div className="space-y-1">
                                            <Label htmlFor="edit-priceInfo">Price info</Label>
                                            <Input
                                                id="edit-priceInfo"
                                                value={edit!.priceInfo}
                                                onChange={(e) => setField("priceInfo", e.target.value)}
                                                maxLength={100}
                                            />
                                            {editErrors?.priceInfo && (
                                                <p className="text-xs text-destructive">{editErrors.priceInfo}</p>
                                            )}
                                        </div>

                                        <div className="space-y-1">
                                            <Label htmlFor="edit-currency">Currency</Label>
                                            <Input
                                                id="edit-currency"
                                                value={edit!.currency}
                                                onChange={(e) => setField("currency", e.target.value)}
                                                maxLength={5}
                                            />
                                            {editErrors?.currency && (
                                                <p className="text-xs text-destructive">{editErrors.currency}</p>
                                            )}
                                        </div>

                                        <div className="space-y-1">
                                            <Label>Condition</Label>
                                            <Select
                                                value={edit!.condition}
                                                onValueChange={(v) => setField("condition", v as PostCondition)}
                                            >
                                                <SelectTrigger>
                                                    <SelectValue placeholder="(Optional)" />
                                                </SelectTrigger>
                                                <SelectContent>
                                                    {CONDITION_OPTIONS.map((c) => (
                                                        <SelectItem key={c.value} value={c.value}>{c.label}</SelectItem>
                                                    ))}
                                                </SelectContent>
                                            </Select>
                                        </div>
                                    </div>

                                    <div className="space-y-1">
                                        <Label htmlFor="edit-contactInfo">Contact info</Label>
                                        <Textarea
                                            id="edit-contactInfo"
                                            value={edit!.contactInfo}
                                            onChange={(e) => setField("contactInfo", e.target.value)}
                                            rows={3}
                                        />
                                    </div>

                                    <div className="space-y-1">
                                        <Label htmlFor="edit-tags">Tags (comma-separated)</Label>
                                        <Input
                                            id="edit-tags"
                                            value={edit!.tagsRaw}
                                            onChange={(e) => setField("tagsRaw", e.target.value)}
                                        />
                                        <p className="text-xs text-muted-foreground">
                                            Parsed: {tags.length ? tags.join(", ") : "none"}
                                        </p>
                                    </div>

                                    <div className="space-y-1">
                                        <Label htmlFor="edit-images">Image URLs (one per line, max 10)</Label>
                                        <Textarea
                                            id="edit-images"
                                            value={edit!.imagesRaw}
                                            onChange={(e) => setField("imagesRaw", e.target.value)}
                                            rows={4}
                                        />
                                        {editErrors?.images && (
                                            <p className="text-xs text-destructive">{editErrors.images}</p>
                                        )}
                                        <p className="text-xs text-muted-foreground">{images.length}/10</p>
                                    </div>

                                    <div className="space-y-1">
                                        <Label htmlFor="edit-thumbnail">Thumbnail URL</Label>
                                        <Input
                                            id="edit-thumbnail"
                                            value={edit!.thumbnailUrl}
                                            onChange={(e) => setField("thumbnailUrl", e.target.value)}
                                        />
                                        {editErrors?.thumbnail && (
                                            <p className="text-xs text-destructive">{editErrors.thumbnail}</p>
                                        )}
                                    </div>
                                </div>
                            )}

                            {/* ── VIEW: meta rows ── */}
                            {!isEditing && (
                                <div className="space-y-2">
                                    <Row label="Location" value={post.location} />
                                    <Row
                                        label="Price"
                                        value={post.priceInfo
                                            ? `${post.priceInfo}${post.currency ? " " + post.currency : ""}`
                                            : undefined}
                                    />
                                    <Row label="Condition" value={post.condition ?? undefined} />
                                    <Row label="Contact" value={post.contactInfo ?? undefined} />
                                    <Row label="Views" value={String(post.viewCount)} />
                                    <Row label="Created" value={post.createdAt ? new Date(post.createdAt).toLocaleString() : undefined} />
                                    <Row label="Updated" value={post.updatedAt ? new Date(post.updatedAt).toLocaleString() : undefined} />
                                </div>
                            )}

                            {/* ── VIEW: tags ── */}
                            {!isEditing && post.tags?.length > 0 && (
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