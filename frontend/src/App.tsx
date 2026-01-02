import {BrowserRouter, Route, Routes} from "react-router-dom";
import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";
import {Button} from "@/components/ui/button.tsx";

function App() {
    return (
        <><BrowserRouter>
            <Routes>
                <Route path="/" element={<div>Home</div>}/>
                <Route path="/login" element={<div>Login</div>}/>
            </Routes>
        </BrowserRouter>
            <div className="min-h-screen flex items-center justify-center p-6">
                <Card className="w-full max-w-md">
                    <CardHeader>
                        <CardTitle>FreeMarket</CardTitle>
                    </CardHeader>
                    <CardContent className="space-y-4">
                        <p className="text-sm text-muted-foreground">
                            React + Tailwind + shadcn/ui is ready.
                        </p>
                        <div className="flex gap-3">
                            <Button>Login</Button>
                            <Button variant="secondary">Register</Button>
                        </div>
                    </CardContent>
                </Card>
            </div>
        </>
    )
}

export default App
