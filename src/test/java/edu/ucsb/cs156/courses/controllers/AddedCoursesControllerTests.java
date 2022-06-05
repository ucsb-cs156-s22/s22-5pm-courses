package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.services.CurrentUserService;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;

import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.User;

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
    PersonalScheduleRepository personalscheduleRepository;

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

    @Test
    public void sections_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/addedcourses/admin?id=1")).andExpect(status().is(403));
    }
    

    // Authorization tests for /api/addedcourses/post
    @Test
    public void api_addedcourses_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/addedcourses/post")).andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/addedcourses/admin?id=1"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__wrong_enrollcode_length__returns_400() throws Exception {
        mockMvc.perform(post("/api/addedcourses/post?enrollCd=wrong code length&psId=123")
                .with(csrf()))
            .andExpect(status().is(400));
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



    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_all_id_does_not_exist() throws Exception {

        when(addedCourseRepository.findById(eq(7L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/addedcourses/admin?id=7"))
                                .andExpect(status().is(404)).andReturn();

        verify(personalscheduleRepository, times(1)).findById(eq(7L));
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 7 not found", json.get("message"));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_return_OK() throws Exception {

        User u1 = User.builder().id(1L).build();

        PersonalSchedule personalSchedule = PersonalSchedule.builder().name("Ryan").description("Test").quarter("2022W").user(u1).id(1L).build();
        when(personalscheduleRepository.findById(1L)).thenReturn(Optional.of(personalSchedule));

        AddedCourse ac1 = AddedCourse.builder().enrollCd("123").personalSchedule(personalSchedule).id(1).build();
        List<AddedCourse> listac1 = new ArrayList<AddedCourse>();
        listac1.add(ac1);
        when(addedCourseRepository.findAllByPersonalSchedule(personalSchedule)).thenReturn(listac1);
        when(ucsbcirService.getSectionJSON("2022W","123")).thenReturn("Section Test");

        MvcResult response = mockMvc.perform(get("/api/addedcourses/admin?id=1"))
                                .andExpect(status().isOk()).andReturn();

        verify(personalscheduleRepository, times(1)).findById(eq(1L));
        String responseString = response.getResponse().getContentAsString();
        List<String> resultList =  mapper.readValue(responseString, List.class);
        System.out.println("JSON" + resultList);
        assertEquals("Section Test", resultList.get(0));
    }


    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_user_return_OK() throws Exception {
        User u1 = User.builder().id(1L).build();
        CurrentUser curUser = CurrentUser.builder().user(u1).build();
        when(currentUserService.getCurrentUser()).thenReturn(curUser);


        PersonalSchedule personalSchedule = PersonalSchedule.builder().name("Ryan").description("Test").quarter("2022W").user(u1).id(1L).build();
        when(personalscheduleRepository.findByIdAndUser(1L, u1)).thenReturn(Optional.of(personalSchedule));

        AddedCourse ac1 = AddedCourse.builder().enrollCd("123").personalSchedule(personalSchedule).id(1).build();
        List<AddedCourse> listac1 = new ArrayList<AddedCourse>();
        listac1.add(ac1);
        when(addedCourseRepository.findAllByPersonalSchedule(personalSchedule)).thenReturn(listac1);

        when(ucsbcirService.getSectionJSON("2022W","123")).thenReturn("Section Test");

        MvcResult response = mockMvc.perform(get("/api/addedcourses?id=1"))
                                .andExpect(status().isOk()).andReturn();

        verify(personalscheduleRepository, times(1)).findByIdAndUser(1L, u1);

        ObjectMapper mapper = new ObjectMapper();
        String responseString = response.getResponse().getContentAsString();
        List<String> resultList =  mapper.readValue(responseString, List.class);
        System.out.println("JSON" + resultList);
        assertEquals("Section Test", resultList.get(0));

    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__invalid_psid__returns_400() throws Exception {
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

        User thisUser = currentUserService.getCurrentUser().getUser();

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

        User thisUser = currentUserService.getCurrentUser().getUser();

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
}
