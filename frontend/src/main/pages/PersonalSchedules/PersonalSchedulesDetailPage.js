import React from 'react'
import { useParams } from "react-router-dom";
//import { useBackend } from 'main/utils/useBackend'

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
//import { useCurrentUser } from 'main/utils/currentUser';
//import { useBackend } from 'main/utils/useBackend';
//import PersonalSchedulesDetailTable from 'main/components/PersonalSchedulesDetailTable';
// Remember to ask -1 team for components for DetailTable and edit based on that
//import { useCurrentUser } from 'main/utils/currentUser'

export default function PersonalSchedulesDetailPage() {

    let { id } = useParams();

    // const currentUser = useCurrentUser();

    // const { data: personalSchedulesDetail, error: _error, status: _status } =
    //     useBackend(
    //         // Stryker disable next-line all : don't test internal caching of React Query
    //         ["/api/coursesadded/all"], // Edit based on -2 team api endpoint
    //         { method: "GET", url: "/api/courseadded/all" },
    //         []
    //     );

    return (
        <BasicLayout>
            <div className="pt-2">
                <h1>PersonalSchedules Detail</h1>
                <p>
                    This is where the detail page will go (id: {id})
                </p>
            </div>
        </BasicLayout>
    )
}
