import { useBackend } from 'main/utils/useBackend';

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import SectionSearchTable from 'main/components/SectionSearch/SectionSearchTable';
import { useCurrentUser } from 'main/utils/currentUser'

export default function SectionSearchIndexPage() {

    const currentUser = useCurrentUser();

    const { data: SectionSearch, error: _error, status: _status } =
        useBackend(
            // Stryker disable next-line all : don't test internal caching of React Query
            ["/api/public/basicsearch"],
            { method: "GET", url: "/api/public/basicsearch" },
            []
        );

    return (
        <BasicLayout>
            <div className="pt-2">
                <h1>SectionSearch</h1>
                <SectionSearchTable SectionSearch={SectionSearch} currentUser={currentUser} />
            </div>
        </BasicLayout>
    )
}