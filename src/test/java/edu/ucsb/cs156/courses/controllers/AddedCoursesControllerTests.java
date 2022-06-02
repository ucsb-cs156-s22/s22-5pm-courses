package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.repositories.AddedCourseRepository;
import edu.ucsb.cs156.courses.repositories.PersonalScheduleRepository;

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
import java.util.Map;
import java.util.Optional;

import static org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestPostProcessors.csrf;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.eq;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@WebMvcTest(controllers = AddedCoursesController.class)
@Import(TestConfig.class)
public class AddedCoursesControllerTests extends ControllerTestCase {
    @MockBean
    AddedCourseRepository addedCourseRepository;

    @MockBean
    PersonalScheduleRepository personalScheduleRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/addedcourses/post
    @Test
    public void api_addedcourses_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/addedcourses/post"))
                .andExpect(status().is(403));
    }
    // Authorization tests for /api/addedcourses/all/?psID=x
    @Test
    public void api_addedcourses_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/addedcourses/all?psId=1"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_all__user_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/addedcourses/all?psId=1"))
                .andExpect(status().isOk());
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__user_logged_in() throws Exception {
        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        PersonalSchedule personalSchedule = PersonalSchedule.builder().user(thisUser).name("Test schedule").description("A test personal schedule").quarter("20224").id(123L).build();

        when(personalScheduleRepository.findByIdAndUser(eq(123L), eq(thisUser))).thenReturn(Optional.of(personalSchedule));

        AddedCourse expectedCourses = AddedCourse.builder().enrollCd("Test code").personalSchedule(personalSchedule).id(0L).build();

        when(addedCourseRepository.save(eq(expectedCourses))).thenReturn(expectedCourses);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/addedcourses/post?enrollCd=Test code&psId=123")
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
        PersonalSchedule p1 = PersonalSchedule.builder().name("Name 1").description("Description 1").quarter("20222").user(thisUser).id(2L).build();

        AddedCourse c1 = AddedCourse.builder().enrollCd("1211").personalSchedule(p1).user(thisUser).id(1L).build();
        AddedCourse c2 = AddedCourse.builder().enrollCd("1212").personalSchedule(p1).user(thisUser).id(2L).build();
        AddedCourse c3 = AddedCourse.builder().enrollCd("1213").personalSchedule(p1).user(thisUser).id(3L).build();
        
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
}
