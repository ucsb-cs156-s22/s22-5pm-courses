package edu.ucsb.cs156.courses.services;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.header;
import static org.springframework.test.web.client.match.MockRestRequestMatchers.requestTo;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withSuccess;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withUnauthorizedRequest;
import static org.springframework.test.web.client.response.MockRestResponseCreators.withStatus;

import java.util.List;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.test.autoconfigure.web.client.RestClientTest;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.test.web.client.MockRestServiceServer;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

import edu.ucsb.cs156.courses.documents.ConvertedSection;
import edu.ucsb.cs156.courses.documents.CoursePageFixtures;
import edu.ucsb.cs156.courses.documents.CourseInfo;
import edu.ucsb.cs156.courses.documents.Section;

@RestClientTest(UCSBCurriculumService.class)
public class UCSBCurriculumServiceTests {

    @Value("${app.ucsb.api.consumer_key}")
    private String apiKey;

    @Autowired
    private MockRestServiceServer mockRestServiceServer;

    @Mock
    private RestTemplate restTemplate;

    @Autowired
    private UCSBCurriculumService ucs;

    @Test
    public void test_getJSON_success() throws Exception {
        String expectedResult = "{expectedResult}";

        String subjectArea = "CMPSC";
        String quarter = "20201";
        String level = "L";

        String expectedParams = String.format(
            "?quarter=%s&subjectCode=%s&objLevelCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s", quarter,
            subjectArea, level, 1, 100, "true");
        String expectedURL = UCSBCurriculumService.CURRICULUM_ENDPOINT + expectedParams;

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        String result = ucs.getJSON(subjectArea, quarter, level);

        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getJSON_success_level_A() throws Exception {
        String expectedResult = "{expectedResult}";

        String subjectArea = "CMPSC";
        String quarter = "20201";

        String level = "A";

        String expectedParams = String.format(
            "?quarter=%s&subjectCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s",
            quarter, subjectArea, 1, 100, "true");
        String expectedURL = UCSBCurriculumService.CURRICULUM_ENDPOINT + expectedParams;

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        String result = ucs.getJSON(subjectArea, quarter, level);

        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getJSON_exception() throws Exception {
        String expectedResult = "{\"error\": \"401: Unauthorized\"}";

        when(restTemplate.exchange(any(String.class), eq(HttpMethod.GET), any(HttpEntity.class), eq(String.class)))
            .thenThrow(HttpClientErrorException.class);

        String subjectArea = "CMPSC";
        String quarter = "20201";
        String level = "L";

        String expectedParams = String.format(
                "?quarter=%s&subjectCode=%s&objLevelCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s", quarter,
                subjectArea, level, 1, 100, "true");
        String expectedURL = UCSBCurriculumService.CURRICULUM_ENDPOINT + expectedParams;

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withUnauthorizedRequest());

        String result = ucs.getJSON(subjectArea, quarter, level);

        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getSubjectsJSON_success() throws Exception {
        String expectedResult = "[ {deptCode: \"ANTH\"} ]";
        String expectedURL = UCSBCurriculumService.SUBJECTS_ENDPOINT;

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        String result = ucs.getSubjectsJSON();
        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getSubjectsJSON_exception() throws Exception {
        String expectedResult = "{\"error\": \"401: Unauthorized\"}";
        String expectedURL = UCSBCurriculumService.SUBJECTS_ENDPOINT;

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andRespond(withUnauthorizedRequest());

        String result = ucs.getSubjectsJSON();
        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getSectionJSON_success() throws Exception {
        String expectedResult = "[ { quarter: \"20224\", classSections: [{ enrollCode: \"18507\" }] } ]";

        String quarter = "20224";
        String enrollCode = "18507";

        String expectedURL = UCSBCurriculumService.SECTION_ENDPOINT.replace("{quarter}", quarter).replace("{enrollCode}", enrollCode);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        String result = ucs.getSectionJSON(quarter, enrollCode);
        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getSectionJSON_section_not_found() throws Exception {
        String expectedResult = "{\"error\": \"Section not found\"}";

        String quarter = "20224";
        String enrollCode = "99999";

        String expectedURL = UCSBCurriculumService.SECTION_ENDPOINT.replace("{quarter}", quarter).replace("{enrollCode}", enrollCode);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess("null", MediaType.APPLICATION_JSON));

        String result = ucs.getSectionJSON(quarter, enrollCode);
        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getSectionJSON_unauthorized_exception() throws Exception {
        String expectedResult = "{\"error\": \"401: Unauthorized\"}";

        String quarter = "20224";
        String enrollCode = "99999";

        String expectedURL = UCSBCurriculumService.SECTION_ENDPOINT.replace("{quarter}", quarter).replace("{enrollCode}", enrollCode);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andRespond(withUnauthorizedRequest());

        String result = ucs.getSectionJSON(quarter, enrollCode);
        assertEquals(expectedResult, result);
    }

    @Test
    public void test_getConvertedSections() throws Exception {
        String expectedResult = CoursePageFixtures.COURSE_PAGE_JSON_MATH3B;

        String subjectArea = "MATH";
        String quarter = "20222";
        String level = "L";

        String expectedParams = String.format(
                "?quarter=%s&subjectCode=%s&objLevelCode=%s&pageNumber=%d&pageSize=%d&includeClassSections=%s", quarter,
                subjectArea, level, 1, 100, "true");
        String expectedURL = UCSBCurriculumService.CURRICULUM_ENDPOINT + expectedParams;

        this.mockRestServiceServer.expect(requestTo(expectedURL))
                .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
                .andExpect(header("ucsb-api-version", "1.0"))
                .andExpect(header("ucsb-api-key", apiKey))
                .andRespond(withSuccess(expectedResult, MediaType.APPLICATION_JSON));

        ObjectMapper objectMapper = new ObjectMapper();
        String convertedSectionsString = ucs.getConvertedSectionsJSON(subjectArea, quarter, level);
        List<ConvertedSection> convertedSections = objectMapper.readValue(convertedSectionsString,
                new TypeReference<List<ConvertedSection>>() {
                });
        List<ConvertedSection> expected = objectMapper.readValue(CoursePageFixtures.CONVERTED_SECTIONS_JSON_MATH5B,
                new TypeReference<List<ConvertedSection>>() {
                });

        assertEquals(expected, convertedSections);
    }

    @Test
    public void test_getSection_with_ConvertedSections_success() throws Exception {

        CourseInfo courseInfo = CourseInfo.builder()
            .quarter("20221")
            .courseId("ANTH      3  ")
            .title("INTRO ARCH")
            .description("An introduction to archaeology")
            .build();
        Section section = Section.builder()
            .enrollCode("00018")
            .section("0100")
            .session("1")
            .classClosed("true")
            .courseCancelled("false")
            .gradingOptionCode("L")
            .enrolledTotal(99)
            .maxEnroll(100)
            .build();
        ConvertedSection convertedSection = ConvertedSection.builder()
            .courseInfo(courseInfo)
            .section(section)
            .build();

        String jsonAPIResponse = "{ \"quarter\": \"20221\", \"courseId\": \"ANTH      3  \", \"title\": \"INTRO ARCH\", \"description\": \"An introduction to archaeology\", \"classSections\": [{ \"enrollCode\": \"00018\", \"section\": \"0100\", \"session\": \"1\", \"classClosed\": \"true\", \"courseCancelled\": \"false\", \"gradingOptionCode\": \"L\", \"enrolledTotal\": 99, \"maxEnroll\": 100 }] }";

        String quarter = "20221";
        String enrollCode = "00018";

        String expectedURL = UCSBCurriculumService.SECTION_ENDPOINT.replace("{quarter}", quarter).replace("{enrollCode}", enrollCode);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(jsonAPIResponse, MediaType.APPLICATION_JSON));

        ConvertedSection result = ucs.getConvertedSection(quarter, enrollCode);
        assertEquals(convertedSection, result);
    }

    @Test
    public void test_getSection_with_ConvertedSections_json_exception() throws Exception {

        String jsonAPIResponse = "bad format";

        String quarter = "20221";
        String enrollCode = "00018";

        String expectedURL = UCSBCurriculumService.SECTION_ENDPOINT.replace("{quarter}", quarter).replace("{enrollCode}", enrollCode);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(jsonAPIResponse, MediaType.APPLICATION_JSON));

        ConvertedSection result = ucs.getConvertedSection(quarter, enrollCode);
        assertNull(result);
    }

    @Test
    public void test_getSection_with_ConvertedSections_enroll_code_not_found() throws Exception {

        String jsonAPIResponse = "{ \"classSections\": [ {\"enrollCode\": \"99999\"} ] }";

        String quarter = "20221";
        String enrollCode = "00018";

        String expectedURL = UCSBCurriculumService.SECTION_ENDPOINT.replace("{quarter}", quarter).replace("{enrollCode}", enrollCode);

        this.mockRestServiceServer.expect(requestTo(expectedURL))
            .andExpect(header("Accept", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("Content-Type", MediaType.APPLICATION_JSON.toString()))
            .andExpect(header("ucsb-api-version", "1.0"))
            .andExpect(header("ucsb-api-key", apiKey))
            .andRespond(withSuccess(jsonAPIResponse, MediaType.APPLICATION_JSON));

        ConvertedSection result = ucs.getConvertedSection(quarter, enrollCode);
        assertNull(result);
    }
}
