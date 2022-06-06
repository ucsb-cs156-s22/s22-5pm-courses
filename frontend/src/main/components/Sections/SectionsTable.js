import React from "react";
import OurTable from "main/components/OurTable";

import { yyyyqToQyy } from "main/utils/quarterUtilities";
import { boldIfNotSection } from "main/utils/sectionUtils";

export default function SectionsTable({ sections }) {

    const columns = [
        {
            Header: 'Quarter',
            accessor: (row, _rowIndex) => yyyyqToQyy(row.courseInfo.quarter),
            id: 'quarter',
        },
        {
            Header: 'Section',
            accessor: (row, _rowIndex) => boldIfNotSection(row.section.section),
            id: 'section.section',
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