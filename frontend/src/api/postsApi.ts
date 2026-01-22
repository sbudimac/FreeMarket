import {http} from "@/api/http.ts";

export type Post = {
    id: string;
    type: PostCategory;
    title: string;
    description: string;
    location?: string;
    priceInfo?: string;
    contactInfo?: string;
    tags?: string[];
    images?: string[];
    createdAt?: string;
    updatedAt?: string;
    isActive?: boolean;
};

export type PostCategory =
    | "PRODUCT_DEMAND"
    | "PRODUCT_SUPPLY"
    | "SERVICE_DEMAND"
    | "SERVICE_SUPPLY"
    | "DELIVERY_DEMAND"
    | "DELIVERY_SUPPLY";

export type PostCondition = "NEW" | "LIKE_NEW" | "USED" | "FOR_PARTS";

export type CreatePostRequest = {
    category: PostCategory;
    title: string;
    description: string;
    location?: string;
    priceInfo?: string;
    contactInfo?: string;
    currency?: string;
    condition?: PostCondition;
    tags?: string[];
    images?: string[];
    thumbnailUrl?: string;
};

type IdResponse = {
    id: string;
};

export type Page<T> = {
    content: T[];
    totalElements: number;
    totalPages: number;
    number: number;
    size: number;
    first: boolean;
    last: boolean;
};

export type PostListItem = {
    id: string;
    title: string;
    category: PostCategory;
    status: string;
    location?: string;
    priceInfo?: string;
    currency?: string;
    thumbnailUrl?: string;
    createdAt?: string;
    owner?: { id: string; username: string };
};

export type UserPublic = {
    id: string;
    username: string;
};

export type PostDetails = {
    id: string;
    category: PostCategory;
    title: string;
    description: string;
    location?: string;
    priceInfo?: string;
    currency?: string;
    condition?: string;
    contactInfo?: string;
    status?: string;
    thumbnailUrl?: string;
    images: string[];
    tags: string[];
    viewCount: number;
    createdAt?: string;
    updatedAt?: string;
    owner: UserPublic;
};

export async function getPosts(params?: { page?: number; size?: number }) {
    const page = params?.page ?? 0;
    const size = params?.size ?? 20;

    return await http<Page<PostListItem>>(`/api/posts?page=${page}&size=${size}`);
}

export async function getPost(id: string): Promise<PostDetails> {
    return await http<PostDetails>(`/api/posts/${id}`);
}

export async function createPost(payload: CreatePostRequest): Promise<IdResponse> {
    return http<IdResponse>("/api/posts", {
        method: "POST",
        body: JSON.stringify(payload),
    });
}