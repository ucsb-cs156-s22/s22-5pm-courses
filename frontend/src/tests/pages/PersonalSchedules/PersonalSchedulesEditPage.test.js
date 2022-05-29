import { fireEvent, render, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";
import PersonalSchedulesEditPage from "main/pages/PersonalSchedules/PersonalSchedulesEditPage";

import { apiCurrentUserFixtures } from "fixtures/currentUserFixtures";
import { systemInfoFixtures } from "fixtures/systemInfoFixtures";
import axios from "axios";
import AxiosMockAdapter from "axios-mock-adapter";

import mockConsole from "jest-mock-console";

const mockToast = jest.fn();
jest.mock('react-toastify', () => {
    const originalModule = jest.requireActual('react-toastify');
    return {
        __esModule: true,
        ...originalModule,
        toast: (x) => mockToast(x)
    };
});

const mockNavigate = jest.fn();
jest.mock('react-router-dom', () => {
    const originalModule = jest.requireActual('react-router-dom');
    return {
        __esModule: true,
        ...originalModule,
        useParams: () => ({
            id: 1
        }),
        Navigate: (x) => { mockNavigate(x); return null; }
    };
});

describe("PersonalSchedulesEditPage tests", () => {

    describe("when the backend doesn't return a personalschedule", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/personalschedules", { params: { id: 1 } }).timeout();
        });

        const queryClient = new QueryClient();
        test("renders header but table is not present", async () => {

            const restoreConsole = mockConsole();

            const {getByText, queryByTestId} = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalSchedulesEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
            await waitFor(() => expect(getByText("Edit PersonalSchedules")).toBeInTheDocument());
            expect(queryByTestId("PersonalScheduleForm-id")).not.toBeInTheDocument();
            restoreConsole();
        });
    });

    describe("tests where backend is working normally", () => {

        const axiosMock = new AxiosMockAdapter(axios);

        beforeEach(() => {
            axiosMock.reset();
            axiosMock.resetHistory();
            axiosMock.onGet("/api/currentUser").reply(200, apiCurrentUserFixtures.userOnly);
            axiosMock.onGet("/api/systemInfo").reply(200, systemInfoFixtures.showingNeither);
            axiosMock.onGet("/api/personalschedules", { params: { id: 1 } }).reply(200, {
                id: 1,
                name: "Memorial Day 2022",
                description: "Description of Memorial Day 2022",
                quarter: "20222"
            });
            axiosMock.onPut('/api/personalschedules').reply(200, {
                id: 1,
                name: "Memorial Day 2021",
                description: "Description of Memorial Day 2021",
                quarter: "20222" // Modified from 20212
            });
        });

        const queryClient = new QueryClient();
        test("renders without crashing", () => {
            render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalSchedulesEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );
        });

        test("Is populated with the data provided", async () => {

            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalSchedulesEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("PersonalScheduleForm-id")).toBeInTheDocument());

            const idField = getByTestId("PersonalScheduleForm-id");
            const nameField = getByTestId("PersonalScheduleForm-name");
            const descriptionField = getByTestId("PersonalScheduleForm-description");
            const quarterField = getByTestId("PersonalScheduleForm-quarter");

            expect(idField).toHaveValue("1");
            expect(nameField).toHaveValue("Memorial Day 2022");
            expect(descriptionField).toHaveValue("Description of Memorial Day 2022");
            expect(quarterField).toHaveValue("20222"); // Modified from 20212
        });

        test("Changes when you click Update", async () => {



            const { getByTestId } = render(
                <QueryClientProvider client={queryClient}>
                    <MemoryRouter>
                        <PersonalSchedulesEditPage />
                    </MemoryRouter>
                </QueryClientProvider>
            );

            await waitFor(() => expect(getByTestId("PersonalScheduleForm-id")).toBeInTheDocument());

            const idField = getByTestId("PersonalScheduleForm-id");
            const nameField = getByTestId("PersonalScheduleForm-name");
            const descriptionField = getByTestId("PersonalScheduleForm-description");
            const quarterField = getByTestId("PersonalScheduleForm-quarter");
            const submitButton = getByTestId("PersonalScheduleForm-submit");

            expect(idField).toHaveValue("1");
            expect(nameField).toHaveValue("Memorial Day 2022");
            expect(descriptionField).toHaveValue("Description of Memorial Day 2022");
            expect(quarterField).toHaveValue("20222");

            expect(submitButton).toBeInTheDocument();

            fireEvent.change(nameField, { target: { value: 'Memorial Day 2021' } })
            fireEvent.change(descriptionField, { target: { value: 'Description of Memorial Day 2021' } })
            fireEvent.change(quarterField, { target: { value: "20222" } })

            fireEvent.click(submitButton);

            await waitFor(() => expect(mockToast).toBeCalled);
            expect(mockToast).toBeCalledWith("PersonalSchedule Updated - id: 1 name: Memorial Day 2021");
            expect(mockNavigate).toBeCalledWith({ "to": "/personalschedules/list" });

            expect(axiosMock.history.put.length).toBe(1); // times called
            expect(axiosMock.history.put[0].params).toEqual({ id: 11 });
            expect(axiosMock.history.put[0].data).toBe(JSON.stringify({
                name: "Memorial Day 2021",
                description: "Description of Memorial Day 2021",
                quarter: "20222" // Modified from 20212
            })); // posted object

        });

       
    });
});