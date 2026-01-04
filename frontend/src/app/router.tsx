import {createBrowserRouter} from "react-router-dom";
import AppShell from "./AppShell";
import HomePage from "@/app/pages/HomePage";
import LoginPage from "@/app/pages/LoginPage";
import RegisterPage from "@/app/pages/RegisterPage";

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