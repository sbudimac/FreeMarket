import {createBrowserRouter} from "react-router-dom";
import AppShell from "./AppShell";
import HomePage from "@/pages/HomePage";
import LoginPage from "@/pages/LoginPage";
import RegisterPage from "@/pages/RegisterPage";

export const router = createBrowserRouter([
    {
        path: "/",
        element: <AppShell/>,
        children: [
            { index: true, element: <HomePage/> },
            { path: "login", element: <LoginPage/> },
            { path: "register", element: <RegisterPage/> },
        ],
    },
]);