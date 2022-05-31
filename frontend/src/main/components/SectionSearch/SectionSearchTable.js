import React from "react";
import OurTable from "main/components/OurTable";

export default function SectionSearchTable({ section, _currentUser}) {

    const columns = [
        {
            Header: 'Enroll Code',
            id: 'enrollCode',
        },
        {
            Header: 'Section',
            accessor: 'section',
        },
        {
            Header: 'Session',
            accessor: 'session',
        },
        {
            Header: 'Class Closed',
            accessor: 'classClosed',
        },
        {
            Header: 'Course Cancelled',
            accessor: 'courseCancelled',
        },
        {
            Header: 'Grading Option Code',
            accessor: 'gradingOptionCode',
        },
        {
            Header: 'Enrolled Total',
            accessor: 'enrolledTotal',
        },
        {
            Header: 'Max Enroll',
            accessor: 'maxEnroll',
        },
        {
            Header: 'Secondary Status',
            accessor: 'secondaryStatus',
        },
        {
            Header: 'Department Approval Required',
            accessor: 'departmentApprovalRequired',
        },
        {
            Header: 'Instructor Approval Required',
            accessor: 'instructorApprovalRequired',
        },
        {
            Header: 'Restriction Level',
            accessor: 'restrictionLevel',
        },
        {
            Header: 'Restriction Major',
            accessor: 'restrictionMajor',
        },
        {
            Header: 'Restriction Major Pass',
            accessor: 'restrictionMajorPass',
        },
        {
            Header: 'Restriction Minor',
            accessor: 'restrictionMinor',
        },
        {
            Header: 'Restriction Minor Pass',
            accessor: 'restrictionMinorPass',
        },
        {
            Header: 'Concurrent Courses',
            accessor: 'concurrentCourses',
        },
    ];

    const testid = "SectionSearchTable"

    return <OurTable
        data={section}
        columns={columns}
        testid={testid}
    />;
};