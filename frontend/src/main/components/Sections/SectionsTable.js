import React from "react";
import OurTable from "main/components/OurTable";

export default function SectionsTable({ sections }) {

    const columns = [
        {
            Header: 'Quarter',
            accessor: 'courseInfo.quarter',
        },
        {
            Header: 'Course Number',
            accessor: 'courseInfo.courseId',
        },
        {
            Header: 'Course Title',
            accessor: 'courseInfo.title',
        },
        {
            Header: 'Days',
            accessor: 'section.timeLocations[0].days',
        },
        {
            Header: 'Begin Time',
            accessor: 'section.timeLocations[0].beginTime',
        },
        {
            Header: 'End Time',
            accessor: 'section.timeLocations[0].endTime',
        },
        {
            Header: 'Enrolled',
            accessor: 'section.enrolledTotal',
        },
        {
            Header: 'Max. Enrollment',
            accessor: 'section.maxEnroll',
        },
    ];

    return <OurTable
        data={sections}
        columns={columns}
        testid={"SectionsTable"}
    />;
};