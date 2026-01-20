import {createBrowserRouter} from "react-router-dom";
import AppShell from "./AppShell";
import HomePage from "@/app/pages/HomePage";
import LoginPage from "@/app/pages/LoginPage";
import RegisterPage from "@/app/pages/RegisterPage";
import PostsPage from "@/app/pages/PostsPage.tsx";
import PostDetailsPage from "@/app/pages/PostDetailsPage.tsx";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <AppShell/>,
        children: [
            { index: true, element: <HomePage/> },
            { path: "login", element: <LoginPage/> },
            { path: "register", element: <RegisterPage/> },
            { path: "posts", element: <PostsPage/> },
            { path: "posts/:id", element: <PostDetailsPage/>},
        ],
    },
]);