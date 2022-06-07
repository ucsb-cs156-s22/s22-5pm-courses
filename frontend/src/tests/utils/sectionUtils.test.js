import { boldIfNotSection, fraction_w_percent } from "main/utils/sectionUtils";

describe("Section Util tests", () => {
    test("If correct number bolds", () => {
        expect(boldIfNotSection("2000")).toStrictEqual(<div style={{fontWeight: "bold"}}>2000</div>);
        expect(() => {boldIfNotSection("abc"); }).toThrow("param should be a number");
        expect(boldIfNotSection("2002")).toBe("2002");
    });
    test("fraction with percent test", () => {
        expect(fraction_w_percent(null, null)).toBe("");
        expect(fraction_w_percent(null, "44")).toBe("44");
        expect(fraction_w_percent("10", "101")).toBe("10/101 (10%)");
        expect(fraction_w_percent('2', null)).toBe("");
    });
});