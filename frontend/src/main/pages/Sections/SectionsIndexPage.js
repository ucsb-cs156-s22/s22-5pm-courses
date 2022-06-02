import React from 'react'
import { useBackend } from 'main/utils/useBackend';

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import SectionsTable from 'main/components/Sections/SectionsTable';
import { useCurrentUser } from 'main/utils/currentUser'

export default function SectionsIndexPage() {

    const currentUser = useCurrentUser();

    const { data: sections, error: _error, status: _status } =
        useBackend(
            // Stryker disable next-line all : don't test internal caching of React Query
            ["/api/sections/all"],
            { method: "GET", url: "/api/sections/all" },
            []
        );

    return (
        <BasicLayout>
            <div className="pt-2">
                <h1>Sections</h1>
                <SectionsTable sections={sections} currentUser={currentUser} />
            </div>
        </BasicLayout>
    )
}