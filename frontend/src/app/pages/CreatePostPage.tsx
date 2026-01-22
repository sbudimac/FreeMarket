import React, { useMemo, useState } from "react";
import { useNavigate } from "react-router-dom";
import { toast } from "sonner";

import { useAuth } from "@/app/auth";
import { createPost, type PostCategory, type PostCondition } from "@/api/postsApi";

import { Card, CardContent, CardHeader, CardTitle } from "@/components/ui/card";
import { Button } from "@/components/ui/button";
import { Input } from "@/components/ui/input";
import { Label } from "@/components/ui/label";
import { Textarea } from "@/components/ui/textarea";
import { Badge } from "@/components/ui/badge";
import {
    Select,
    SelectContent,
    SelectItem,
    SelectTrigger,
    SelectValue,
} from "@/components/ui/select";

const CATEGORY_OPTIONS: { value: PostCategory; label: string; hint: string }[] = [
    { value: "PRODUCT_SUPPLY", label: "Product supply", hint: "You offer a product" },
    { value: "PRODUCT_DEMAND", label: "Product demand", hint: "You’re looking for a product" },
    { value: "SERVICE_SUPPLY", label: "Service supply", hint: "You offer a service" },
    { value: "SERVICE_DEMAND", label: "Service demand", hint: "You need a service" },
    { value: "DELIVERY_SUPPLY", label: "Delivery supply", hint: "You can deliver something" },
    { value: "DELIVERY_DEMAND", label: "Delivery demand", hint: "You need delivery" },
];

const CONDITION_OPTIONS: { value: PostCondition; label: string }[] = [
    { value: "NEW", label: "New" },
    { value: "LIKE_NEW", label: "Like new" },
    { value: "USED", label: "Used" },
    { value: "FOR_PARTS", label: "For parts" },
];

function normalizeTags(raw: string): string[] {
    return raw
        .split(",")
        .map((t) => t.trim())
        .filter(Boolean)
        .slice(0, 50);
}

function normalizeImages(raw: string): string[] {
    return raw
        .split("\n")
        .map((s) => s.trim())
        .filter(Boolean)
        .slice(0, 10);
}

function isValidHttpUrl(s: string): boolean {
    try {
        const u = new URL(s);
        return u.protocol === "http:" || u.protocol === "https:";
    } catch {
        return false;
    }
}

export default function CreatePostPage() {
    const navigate = useNavigate();
    const { isAuthenticated } = useAuth();

    // Required
    const [category, setCategory] = useState<PostCategory>("PRODUCT_SUPPLY");
    const [title, setTitle] = useState("");
    const [description, setDescription] = useState("");

    // Optional (but useful)
    const [location, setLocation] = useState("");
    const [priceInfo, setPriceInfo] = useState("");
    const [contactInfo, setContactInfo] = useState("");
    const [currency, setCurrency] = useState("EUR");
    const [condition, setCondition] = useState<PostCondition | "">("");

    // Collections
    const [tagsRaw, setTagsRaw] = useState("");
    const [imagesRaw, setImagesRaw] = useState("");

    // Thumbnail
    const [thumbnailUrl, setThumbnailUrl] = useState("");

    const [isSubmitting, setIsSubmitting] = useState(false);

    const tags = useMemo(() => normalizeTags(tagsRaw), [tagsRaw]);
    const images = useMemo(() => normalizeImages(imagesRaw), [imagesRaw]);

    const titleError =
        title.length === 0
            ? null
            : title.length < 5
                ? "Title must be at least 5 characters."
                : title.length > 200
                    ? "Title must be at most 200 characters."
                    : null;

    const descriptionError =
        description.length === 0
            ? null
            : description.length < 10
                ? "Description must be at least 10 characters."
                : description.length > 5000
                    ? "Description must be at most 5000 characters."
                    : null;

    const locationError =
        location.length > 100 ? "Location must be at most 100 characters." : null;

    const priceInfoError =
        priceInfo.length > 100 ? "Price info must be at most 100 characters." : null;

    const contactInfoError =
        contactInfo.length > 5000
            ? "Contact info must be at most 5000 characters."
            : null;

    const currencyError =
        currency.length > 5 ? "Currency must be at most 5 characters." : null;

    const imagesError =
        images.length > 10 ? "You can provide at most 10 images." : null;

    const thumbnailError =
        thumbnailUrl && !isValidHttpUrl(thumbnailUrl)
            ? "Thumbnail URL must be a valid http/https URL."
            : null;

    const imagesUrlError = (() => {
        const invalid = images.find((u) => !isValidHttpUrl(u));
        return invalid ? `Invalid image URL: ${invalid}` : null;
    })();

    const canSubmit =
        isAuthenticated &&
        title.trim().length >= 5 &&
        description.trim().length >= 10 &&
        !titleError &&
        !descriptionError &&
        !locationError &&
        !priceInfoError &&
        !contactInfoError &&
        !currencyError &&
        !imagesError &&
        !thumbnailError &&
        !imagesUrlError &&
        !isSubmitting;

    async function onSubmit(e: React.FormEvent) {
        e.preventDefault();

        if (!isAuthenticated) {
            toast.error("You must be logged in to create a post.");
            return;
        }

        if (!canSubmit) {
            toast.error("Please fix the form errors before submitting.");
            return;
        }

        setIsSubmitting(true);
        try {
            const payload = {
                category,
                title: title.trim(),
                description: description.trim(),
                location: location.trim() || undefined,
                priceInfo: priceInfo.trim() || undefined,
                contactInfo: contactInfo.trim() || undefined,
                currency: currency.trim() || undefined,
                condition: condition || undefined,
                tags: tags.length ? tags : undefined,
                images: images.length ? images : undefined,
                thumbnailUrl: thumbnailUrl.trim() || undefined,
            };

            const created = await createPost(payload);

            toast.success("Post created!");
            navigate(`/posts/${created.id}`);
        } catch (err) {
            toast.error(err instanceof Error ? err.message : "Failed to create post");
        } finally {
            setIsSubmitting(false);
        }
    }

    if (!isAuthenticated) {
        return (
            <div className="mx-auto w-full max-w-2xl">
                <Card>
                    <CardHeader>
                        <CardTitle>Login required</CardTitle>
                    </CardHeader>
                    <CardContent className="text-sm text-muted-foreground">
                        You must be logged in to create a post.
                    </CardContent>
                </Card>
            </div>
        );
    }

    const selectedCategory = CATEGORY_OPTIONS.find((c) => c.value === category);

    return (
        <div className="mx-auto w-full max-w-2xl space-y-4">
            <div className="flex items-start justify-between gap-4">
                <div>
                    <h1 className="text-2xl font-semibold">Create post</h1>
                    <p className="text-sm text-muted-foreground">
                        Fill in the details. Required fields: category, title, description.
                    </p>
                </div>
                <Badge variant="secondary">MVP</Badge>
            </div>

            <Card>
                <CardHeader>
                    <CardTitle>Post details</CardTitle>
                </CardHeader>

                <CardContent>
                    <form onSubmit={onSubmit} className="space-y-6">
                        {/* Category */}
                        <div className="space-y-2">
                            <Label>Category *</Label>
                            <Select
                                value={category}
                                onValueChange={(v) => setCategory(v as PostCategory)}
                            >
                                <SelectTrigger>
                                    <SelectValue placeholder="Select a category" />
                                </SelectTrigger>
                                <SelectContent>
                                    {CATEGORY_OPTIONS.map((c) => (
                                        <SelectItem key={c.value} value={c.value}>
                                            {c.label}
                                        </SelectItem>
                                    ))}
                                </SelectContent>
                            </Select>
                            {selectedCategory?.hint && (
                                <p className="text-xs text-muted-foreground">{selectedCategory.hint}</p>
                            )}
                        </div>

                        {/* Title */}
                        <div className="space-y-2">
                            <Label htmlFor="title">Title *</Label>
                            <Input
                                id="title"
                                value={title}
                                onChange={(e) => setTitle(e.target.value)}
                                required
                                minLength={5}
                                maxLength={200}
                            />
                            {titleError && <p className="text-sm text-destructive">{titleError}</p>}
                        </div>

                        {/* Description */}
                        <div className="space-y-2">
                            <Label htmlFor="description">Description *</Label>
                            <Textarea
                                id="description"
                                value={description}
                                onChange={(e) => setDescription(e.target.value)}
                                required
                                rows={6}
                            />
                            {descriptionError && (
                                <p className="text-sm text-destructive">{descriptionError}</p>
                            )}
                        </div>

                        {/* Optional fields */}
                        <div className="grid gap-4 md:grid-cols-2">
                            <div className="space-y-2">
                                <Label htmlFor="location">Location</Label>
                                <Input
                                    id="location"
                                    value={location}
                                    onChange={(e) => setLocation(e.target.value)}
                                    maxLength={100}
                                />
                                {locationError && (
                                    <p className="text-sm text-destructive">{locationError}</p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="priceInfo">Price info</Label>
                                <Input
                                    id="priceInfo"
                                    value={priceInfo}
                                    onChange={(e) => setPriceInfo(e.target.value)}
                                    maxLength={100}
                                />
                                {priceInfoError && (
                                    <p className="text-sm text-destructive">{priceInfoError}</p>
                                )}
                            </div>

                            <div className="space-y-2">
                                <Label htmlFor="currency">Currency</Label>
                                <Input
                                    id="currency"
                                    value={currency}
                                    onChange={(e) => setCurrency(e.target.value)}
                                    maxLength={5}
                                />
                                {currencyError && (
                                    <p className="text-sm text-destructive">{currencyError}</p>
                                )}
                                <p className="text-xs text-muted-foreground">Default: EUR</p>
                            </div>

                            <div className="space-y-2">
                                <Label>Condition</Label>
                                <Select
                                    value={condition}
                                    onValueChange={(v) => setCondition(v as PostCondition)}
                                >
                                    <SelectTrigger>
                                        <SelectValue placeholder="(Optional) Select condition" />
                                    </SelectTrigger>
                                    <SelectContent>
                                        {CONDITION_OPTIONS.map((c) => (
                                            <SelectItem key={c.value} value={c.value}>
                                                {c.label}
                                            </SelectItem>
                                        ))}
                                    </SelectContent>
                                </Select>
                            </div>
                        </div>

                        {/* Contact info */}
                        <div className="space-y-2">
                            <Label htmlFor="contactInfo">Contact info</Label>
                            <Textarea
                                id="contactInfo"
                                value={contactInfo}
                                onChange={(e) => setContactInfo(e.target.value)}
                                rows={4}
                            />
                            {contactInfoError && (
                                <p className="text-sm text-destructive">{contactInfoError}</p>
                            )}
                            <p className="text-xs text-muted-foreground">
                                Example: phone, email, pickup times, preferred contact method.
                            </p>
                        </div>

                        {/* Tags */}
                        <div className="space-y-2">
                            <Label htmlFor="tags">Tags (comma-separated)</Label>
                            <Input
                                id="tags"
                                value={tagsRaw}
                                onChange={(e) => setTagsRaw(e.target.value)}
                                placeholder="e.g. electronics, iphone, student"
                            />
                            <p className="text-xs text-muted-foreground">
                                Parsed tags:{" "}
                                {tags.length ? tags.join(", ") : <span>none</span>}
                            </p>
                        </div>

                        {/* Images */}
                        <div className="space-y-2">
                            <Label htmlFor="images">Image URLs (one per line, max 10)</Label>
                            <Textarea
                                id="images"
                                value={imagesRaw}
                                onChange={(e) => setImagesRaw(e.target.value)}
                                rows={5}
                                placeholder={`https://example.com/image1.jpg\nhttps://example.com/image2.png`}
                            />
                            {(imagesError || imagesUrlError) && (
                                <p className="text-sm text-destructive">
                                    {imagesError ?? imagesUrlError}
                                </p>
                            )}
                            <p className="text-xs text-muted-foreground">
                                Current: {images.length}/10
                            </p>
                        </div>

                        {/* Thumbnail */}
                        <div className="space-y-2">
                            <Label htmlFor="thumbnailUrl">Thumbnail URL</Label>
                            <Input
                                id="thumbnailUrl"
                                value={thumbnailUrl}
                                onChange={(e) => setThumbnailUrl(e.target.value)}
                                placeholder="https://example.com/thumb.jpg"
                            />
                            {thumbnailError && (
                                <p className="text-sm text-destructive">{thumbnailError}</p>
                            )}
                        </div>

                        <div className="flex items-center justify-end gap-2">
                            <Button type="submit" disabled={!canSubmit}>
                                {isSubmitting ? "Creating..." : "Create post"}
                            </Button>
                        </div>
                    </form>
                </CardContent>
            </Card>
        </div>
    );
}
