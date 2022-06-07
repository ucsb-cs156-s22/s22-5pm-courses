package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.services.CurrentUserService;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;

import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.User;

import edu.ucsb.cs156.courses.documents.CourseInfo;
import edu.ucsb.cs156.courses.documents.Section;
import edu.ucsb.cs156.courses.documents.ConvertedSection;

import edu.ucsb.cs156.courses.repositories.AddedCourseRepository;
import edu.ucsb.cs156.courses.repositories.PersonalScheduleRepository;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Collections;
import java.util.Map;
import java.util.Optional;

import com.mongodb.DuplicateKeyException;


import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


import static org.mockito.Mockito.doNothing;
import com.fasterxml.jackson.databind.ObjectMapper;

import edu.ucsb.cs156.courses.models.CurrentUser;

import org.springframework.beans.factory.annotation.Autowired;


@WebMvcTest(controllers = AddedCoursesController.class)
@Import(TestConfig.class)
public class AddedCoursesControllerTests extends ControllerTestCase {


    @MockBean
    PersonalScheduleRepository personalScheduleRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AddedCourseRepository addedCourseRepository;

    @MockBean
    private UCSBCurriculumService ucsbCurriculumService;

    @MockBean
    CurrentUserService currentUserService;

    @Autowired
    public ObjectMapper mapper;

    // Authorization tests for /api/addedcourses/post
    @Test
    public void api_addedcourses_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/addedcourses/post")).andExpect(status().is(403));
    }
    // Authorization tests for /api/addedcourses/all/?psID=x
    @Test
    public void api_addedcourses_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/addedcourses/all?psId=1"))
                .andExpect(status().is(403));
    }
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__wrong_enrollcode_length__returns_400() throws Exception {
        User u1 = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(u1).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);

        mockMvc.perform(post("/api/addedcourses/post?enrollCd=wrong code length&psId=123")
                .with(csrf()))
            .andExpect(status().is(400));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__invalid_psid__returns_400() throws Exception {
        User u1 = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(u1).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);

        mockMvc.perform(post("/api/addedcourses/post?enrollCd=11111&psId=123")
                .with(csrf()))
            .andExpect(status().is(400));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__invalid_enrollcode__returns_400() throws Exception {
        String quarter = "20224";
        Long psId = 123L;
        String enrollCode = "99999";

        User thisUser = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(thisUser).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);

        PersonalSchedule personalSchedule = PersonalSchedule.builder().user(thisUser).name("Test schedule").description("A test personal schedule").quarter(quarter).id(psId).build();
        when(personalScheduleRepository.findByIdAndUser(eq(123L), eq(thisUser))).thenReturn(Optional.of(personalSchedule));

        when(ucsbCurriculumService.getSectionJSON(quarter, enrollCode)).thenReturn("{\"error\": \"Section not found\"}");

        mockMvc.perform(post("/api/addedcourses/post?enrollCd={enrollCode}&psId={psId}".replace("{enrollCode}", enrollCode).replace("{psId}", String.valueOf(psId)))
                .with(csrf()))
            .andExpect(status().is(400));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__user_logged_in() throws Exception {
        // arrange
        String quarter = "20224";
        Long psId = 123L;
        String enrollCode = "00018";

        User thisUser = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(thisUser).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);

        PersonalSchedule personalSchedule = PersonalSchedule.builder().user(thisUser).name("Test schedule").description("A test personal schedule").quarter(quarter).id(psId).build();
        when(personalScheduleRepository.findByIdAndUser(eq(123L), eq(thisUser))).thenReturn(Optional.of(personalSchedule));

        AddedCourse expectedCourses = AddedCourse.builder().enrollCd(enrollCode).personalSchedule(personalSchedule).id(0L).build();
        when(addedCourseRepository.save(eq(expectedCourses))).thenReturn(expectedCourses);

        when(ucsbCurriculumService.getSectionJSON(quarter, enrollCode)).thenReturn("{}");

        // act
        MvcResult response = mockMvc.perform(
                post("/api/addedcourses/post?enrollCd={enrollCode}&psId={psId}".replace("{enrollCode}", enrollCode).replace("{psId}", String.valueOf(psId)))
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(addedCourseRepository, times(1)).save(expectedCourses);

        String expectedJson = mapper.writeValueAsString(expectedCourses);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_all__user_logged_in__returns_only_schedules_for_user() throws Exception {

        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        PersonalSchedule p1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(thisUser).id(1L).build();
        when(personalScheduleRepository.findByIdAndUser(eq(1L), eq(thisUser))).thenReturn(Optional.of(p1));

        AddedCourse c1 = AddedCourse.builder().enrollCd("1211").personalSchedule(p1).id(1L).build();
        AddedCourse c2 = AddedCourse.builder().enrollCd("1212").personalSchedule(p1).id(2L).build();
        AddedCourse c3 = AddedCourse.builder().enrollCd("1213").personalSchedule(p1).id(3L).build();
        ArrayList<AddedCourse> expectedCourses = new ArrayList<>();
        expectedCourses.addAll(Arrays.asList(c1, c2, c3));

        when(addedCourseRepository.findAllByPersonalSchedule(p1)).thenReturn(expectedCourses);

        // act
        MvcResult response = mockMvc.perform(get("/api/addedcourses/all?psId=1"))
                .andExpect(status().isOk()).andReturn();

        // assert

        verify(addedCourseRepository, times(1)).findAllByPersonalSchedule(eq(p1));
        String expectedJson = mapper.writeValueAsString(expectedCourses);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
    
    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_user_logged_in_search_for_courses_to_a_schedule_that_does_not_belong_to_user() throws Exception {

        // arrange

         User thisUser = currentUserService.getCurrentUser().getUser();
         User otherUser = User.builder().id(999L).build();

         PersonalSchedule p1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20221").user(otherUser).id(1L).build();

         when(personalScheduleRepository.findByIdAndUser(eq(1L), eq(otherUser))).thenReturn(Optional.of(p1));

        // act
        MvcResult response = mockMvc.perform(get("/api/addedcourses/all?psId=29"))
                .andExpect(status().isNotFound()).andReturn();

        // assert

        verify(addedCourseRepository, times(0)).findAllByPersonalSchedule(p1);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 29 not found", json.get("message"));
    }
    
    // Authorization tests for /api/addedcourses/

    @Test
    public void sections_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/addedcourses/admin?id=1")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/addedcourses/admin?id=1"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void sections__user_logged_in__returns_404() throws Exception {
        mockMvc.perform(get("/api/addedcourses2?id=1"))
                .andExpect(status().is(404));
    }


    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_logged_in__returns_404() throws Exception {
        mockMvc.perform(get("/api/addedcourses/admin?id=1"))
                .andExpect(status().is(404));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_user_all_id_does_not_exist() throws Exception {

        User thisUser = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(thisUser).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);

        when(personalScheduleRepository.findByIdAndUser(7L, thisUser)).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/addedcourses?id=7"))
                                .andExpect(status().is(404)).andReturn();

        verify(personalScheduleRepository, times(1)).findByIdAndUser(7L, thisUser);
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_all_id_does_not_exist() throws Exception {

        when(personalScheduleRepository.findById(eq(7L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/addedcourses/admin?id=7"))
                                .andExpect(status().is(404)).andReturn();

        verify(personalScheduleRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_return_OK() throws Exception {

        User u1 = User.builder().id(1L).build();

        PersonalSchedule personalSchedule = PersonalSchedule.builder().name("Ryan").description("Test").quarter("20221").user(u1).id(1L).build();
        when(personalScheduleRepository.findById(1L)).thenReturn(Optional.of(personalSchedule));

        AddedCourse ac1 = AddedCourse.builder().enrollCd("123").personalSchedule(personalSchedule).id(1).build();
        List<AddedCourse> listac1 = new ArrayList<AddedCourse>();
        listac1.add(ac1);
        when(addedCourseRepository.findAllByPersonalSchedule(personalSchedule)).thenReturn(listac1);

        CourseInfo courseInfo = CourseInfo.builder()
            .quarter("20221")
            .courseId("ANTH      3  ")
            .title("INTRO ARCH")
            .description("An introduction to archaeology")
            .build();
        Section section = Section.builder()
            .enrollCode("123")
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

        String convertedSectionString = mapper.writeValueAsString(Arrays.asList(convertedSection));

        when(ucsbCurriculumService.getConvertedSection("20221","123")).thenReturn(convertedSection);

        MvcResult response = mockMvc.perform(get("/api/addedcourses/admin?id=1"))
                                .andExpect(status().isOk()).andReturn();

        verify(personalScheduleRepository, times(1)).findById(eq(1L));
        String responseString = response.getResponse().getContentAsString();
        System.out.println("JSON" + responseString);
        assertEquals(convertedSectionString, responseString);
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_user_return_OK() throws Exception {
        User u1 = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(u1).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);


        PersonalSchedule personalSchedule = PersonalSchedule.builder().name("Ryan").description("Test").quarter("20221").user(u1).id(1L).build();
        when(personalScheduleRepository.findByIdAndUser(1L, u1)).thenReturn(Optional.of(personalSchedule));

        AddedCourse ac1 = AddedCourse.builder().enrollCd("123").personalSchedule(personalSchedule).id(1).build();
        List<AddedCourse> listac1 = new ArrayList<AddedCourse>();
        listac1.add(ac1);
        when(addedCourseRepository.findAllByPersonalSchedule(personalSchedule)).thenReturn(listac1);

        CourseInfo courseInfo = CourseInfo.builder()
            .quarter("20221")
            .courseId("ANTH      3  ")
            .title("INTRO ARCH")
            .description("An introduction to archaeology")
            .build();
        Section section = Section.builder()
            .enrollCode("123")
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

        when(ucsbCurriculumService.getConvertedSection("20221","123")).thenReturn(convertedSection);

        String convertedSectionString = mapper.writeValueAsString(Arrays.asList(convertedSection));

        MvcResult response = mockMvc.perform(get("/api/addedcourses?id=1"))
                                .andExpect(status().isOk()).andReturn();

        verify(personalScheduleRepository, times(1)).findByIdAndUser(1L, u1);

        ObjectMapper mapper = new ObjectMapper();
        String responseString = response.getResponse().getContentAsString();
        System.out.println("JSON" + responseString);
        assertEquals(convertedSectionString, responseString);

    }

}
