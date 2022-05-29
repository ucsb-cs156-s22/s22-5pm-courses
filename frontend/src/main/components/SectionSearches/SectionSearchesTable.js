import React from "react";
import OurTable, { ButtonColumn } from "main/components/OurTable";
import { useBackendMutation } from "main/utils/useBackend";
import { cellToAxiosParamsDelete, onDeleteSuccess } from "main/utils/PersonalScheduleUtils"
import { useNavigate } from "react-router-dom";
import { hasRole } from "main/utils/currentUser";
import { yyyyqToQyy } from "main/utils/quarterUtilities.js";

export default function SectionSearchesTable({ section, currentUser }) {
    const columns = [
        {
            Header: 'Quarter',
            accessor: 'quarter', 
        },
        {
            Header: 'CourseId',
            accessor: 'course id',
        },
        {
            Header: 'Title',
            accessor: 'title', 
        },
        {
            Header: 'Subjectarea',
            accessor: 'subject area', 
        },
        {
            Header: 'Enrollcode',
            accessor: 'enroll code', 
        },
        {
            Header: 'EnrolledTotal',
            accessor: 'total enrolled',
        },
        {
            Header: 'MaxEnroll',
            accessor: 'max enrolled',
        },
        {
            Header: 'Room',
            accessor: 'room',
        },
        {
            Header: 'Building',
            accessor: 'building',
        },
        {
            Header: 'Days',
            accessor: 'days',
        },
        {
            Header: 'BeginTime',
            accessor: 'begin Time',
        },
        {
            Header: 'EndTime',
            accessor: 'end Time',
        },
        {
            Header: 'Instructor',
            accessor: 'instructor',
        }
    ];

    const testid = "SectionSearchesTable";

    const columnsToDisplay = hasRole(currentUser, "ROLE_USER") ? columnsIfUser : columns;

    return <OurTable
        data={section}
        columns={columnsToDisplay}
        testid={testid}
    />;
}; 