import React from 'react';
import { render, waitFor } from "@testing-library/react";
import userEvent from "@testing-library/user-event";
import SingleQuarterDropdown from "main/components/Quarters/SingleQuarterDropdown"

import { quarterRange } from 'main/utils/quarterUtilities';


describe("SingleQuarterSelector tests", () => {

    const quarter = jest.fn();
    const setQuarter = jest.fn();

    test("renders without crashing on one quarter", () => {
        render(<SingleQuarterDropdown
            quarters={quarterRange("20211", "20211")}
            quarter={quarter}
            setQuarter={setQuarter}
            controlId="sqd1"
        />);
    });

    test("renders without crashing on three quarters", () => {
        render(<SingleQuarterDropdown
            quarters={quarterRange("20214", "20222")}
            quarter={quarter}
            setQuarter={setQuarter}
            controlId="sqd1"
        />);
    });

    test("when I select an object, the value changes", async () => {
        const { getByLabelText } =
            render(<SingleQuarterDropdown
                quarters={quarterRange("20211", "20222")}
                quarter={quarter}
                setQuarter={setQuarter}
                controlId="sqd1"
                label="Select Quarter"
            />
            );
        await waitFor(() => expect(getByLabelText("Select Quarter")).toBeInTheDocument);
        const selectQuarter = getByLabelText("Select Quarter")
        userEvent.selectOptions(selectQuarter, "20213");
        expect(setQuarter).toBeCalledWith("20213");
    });

    test("if I pass a non-null onChange, it gets called when the value changes", async () => {
        const onChange = jest.fn();
        const { getByLabelText } =
            render(<SingleQuarterDropdown
                quarters={quarterRange("20211", "20222")}
                quarter={quarter}
                setQuarter={setQuarter}
                controlId="sqd1"
                label="Select Quarter"
                onChange={onChange}
            />
            );
        await waitFor(() => expect(getByLabelText("Select Quarter")).toBeInTheDocument);
        const selectQuarter = getByLabelText("Select Quarter")
        userEvent.selectOptions(selectQuarter, "20213");
        await waitFor(() => expect(setQuarter).toBeCalledWith("20213"));
        await waitFor(() => expect(onChange).toBeCalledTimes(1));

        // x.mock.calls[0][0] is the first argument of the first call to the jest.fn() mock x

        const event = onChange.mock.calls[0][0];
        expect(event.target.value).toBe("20213");
    });

    test("default label is Quarter", async () => {
        const { getByLabelText } =
            render(<SingleQuarterDropdown
                quarters={quarterRange("20211", "20222")}
                quarter={quarter}
                setQuarter={setQuarter}
                controlId="sqd1"
            />
            );
        await waitFor(() => expect(getByLabelText("Quarter")).toBeInTheDocument);
    });

});