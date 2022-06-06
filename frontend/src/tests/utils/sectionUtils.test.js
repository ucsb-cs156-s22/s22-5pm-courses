import { boldIfNotSection } from "main/utils/sectionUtils";

describe("To bold tests", () => {
    test("If correct number bolds", () => {
        expect(boldIfNotSection("2000")).toStrictEqual(<div style={{fontWeight: "bold"}}>2000</div>);
        expect(() => {boldIfNotSection("abc"); }).toThrow("param should be a number");
        expect(boldIfNotSection("2002")).toBe("2002");
    });
});