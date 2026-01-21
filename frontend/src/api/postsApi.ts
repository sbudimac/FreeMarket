import {http} from "@/api/http.ts";

export type Post = {
    id: number;
    title: string;
    description?: string;
    createdAt?: string;
    authorUsername?: string;
};

export type CreatePostRequest = {
    title: string;
    description?: string;
}

export async function getPosts(): Promise<Post[]> {
    return await http<Post[]>("/api/posts");
}

export async function getPost(id: number): Promise<Post> {
    return await http<Post>(`/api/posts/${id}`);
}

export async function createPost(req: CreatePostRequest): Promise<Post> {
    return await http<Post>("/api/posts", {
        method: "POST",
        body: JSON.stringify(req),
    });
}