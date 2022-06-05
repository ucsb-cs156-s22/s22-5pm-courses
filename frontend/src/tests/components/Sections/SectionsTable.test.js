import { _fireEvent, render, screen, waitFor } from "@testing-library/react";
import { QueryClient, QueryClientProvider } from "react-query";
import { MemoryRouter } from "react-router-dom";

import SectionsTable from "main/components/Sections/SectionsTable";
import { currentUserFixtures } from "fixtures/currentUserFixtures";
import { sectionsFixtures } from "fixtures/sectionsFixtures";

const mockedNavigate = jest.fn();

jest.mock('react-router-dom', () => ({
    ...jest.requireActual('react-router-dom'),
    useNavigate: () => mockedNavigate
}));

const mockedMutate = jest.fn();

jest.mock('main/utils/useBackend', () => ({
    ...jest.requireActual('main/utils/useBackend'),
    useBackendMutation: () => ({ mutate: mockedMutate })
}));

describe("UserTable tests", () => {
    const queryClient = new QueryClient();

    test("renders without crashing for empty table with user not logged in", () => {
        const currentUser = null;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <SectionsTable sections={[]} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );
    });

    test("renders without crashing for empty table for ordinary user", () => {
        const currentUser = currentUserFixtures.userOnly;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <SectionsTable sections={[]} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );
    });

    test("renders without crashing for empty table for admin", () => {
        const currentUser = currentUserFixtures.adminUser;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <SectionsTable sections={[]} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );
    });

    test("Has the expected column headers and content for Ordinary User", async() => {

        const currentUser = currentUserFixtures.userOnly;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <SectionsTable sections={sectionsFixtures.threeSections} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );

        const expectedHeaders = ["Quarter", "Section", "Course Number", "Course Title", "Days", "Begin Time", "End Time", 
        "Enrolled", "Max. Enrollment"];
        const expectedFields = ["quarter", "section.section", "courseInfo.courseId", "courseInfo.title", "section.timeLocations[0].days", 
        "section.timeLocations[0].beginTime", "section.timeLocations[0].endTime", "section.enrolledTotal", "section.maxEnroll"];
        const testId = "SectionsTable";

        expectedHeaders.forEach((headerText) => {
            const header = screen.getByText(headerText);
            expect(header).toBeInTheDocument();
        });

        expectedFields.forEach((field) => {
            const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
            expect(header).toBeInTheDocument();
        });

        await waitFor( ()=> expect(screen.getByTestId(`${testId}-cell-row-0-col-courseInfo.courseId`)).toHaveTextContent("CMPSC 8"));
        expect(screen.getByTestId(`${testId}-cell-row-0-col-quarter`)).toHaveTextContent("F20");
        expect(screen.getByTestId(`${testId}-cell-row-0-col-section.section`)).toHaveTextContent("0101");

    });

    test("Has the expected column headers and content for adminUser", async () => {

        const currentUser = currentUserFixtures.adminUser;

        render(
            <QueryClientProvider client={queryClient}>
                <MemoryRouter>
                    <SectionsTable sections={sectionsFixtures.threeSections} currentUser={currentUser} />
                </MemoryRouter>
            </QueryClientProvider>
        );

        const expectedHeaders = ["Quarter", "Section", "Course Number", "Course Title", "Days", "Begin Time", "End Time", 
        "Enrolled", "Max. Enrollment"];
        const expectedFields = ["quarter", "section.section", "courseInfo.courseId", "courseInfo.title", "section.timeLocations[0].days",                        
        "section.timeLocations[0].beginTime", "section.timeLocations[0].endTime", "section.enrolledTotal", "section.maxEnroll"];
        const testId = "SectionsTable";

        expectedHeaders.forEach((headerText) => {
            const header = screen.getByText(headerText);
            expect(header).toBeInTheDocument();
        });

        expectedFields.forEach((field) => {
            const header = screen.getByTestId(`${testId}-cell-row-0-col-${field}`);
            expect(header).toBeInTheDocument();
        });

        await waitFor( () => expect(screen.getByTestId(`${testId}-cell-row-0-col-courseInfo.courseId`)).toHaveTextContent("CMPSC 8"));
        expect(screen.getByTestId(`${testId}-cell-row-0-col-quarter`)).toHaveTextContent("F20");
        expect(screen.getByTestId(`${testId}-cell-row-0-col-section.section`)).toHaveTextContent("0101");

    });
    
});