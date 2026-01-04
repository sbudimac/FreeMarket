import {Card, CardContent, CardHeader, CardTitle} from "@/components/ui/card.tsx";

export default function HomePage() {
    return (
        <Card>
            <CardHeader>
                <CardTitle>Home</CardTitle>
            </CardHeader>
            <CardContent className="text-sm text-muted-foreground">
                AppShell is working. Next: login/register pages.
            </CardContent>
        </Card>
    );
}