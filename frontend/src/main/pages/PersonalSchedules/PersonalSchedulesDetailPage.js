import React from 'react'
import { useParams } from "react-router-dom";

import BasicLayout from "main/layouts/BasicLayout/BasicLayout";

export default function PersonalSchedulesDetailPage() {

    let { id } = useParams();

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
