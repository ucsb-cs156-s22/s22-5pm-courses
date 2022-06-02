import React from "react";
import OurTable from "main/components/OurTable";

export default function SectionsTable({ sections }) {

    const columns = [
        {
            Header: 'Course Level',
            accessor: 'courseLevel',
        },
        {
            Header: 'Quarter',
            accessor: 'quarter',
        },
        {
            Header: 'Subject Area',
            accessor: 'subjectArea',
        },
    ];

    return <OurTable
        data={sections}
        columns={columns}
        testid={"SectionsTable"}
    />;
};

//courseInfo.quarter -> Quarter
// courseInfo.courseId -> Subject Area
// courseInfo.

