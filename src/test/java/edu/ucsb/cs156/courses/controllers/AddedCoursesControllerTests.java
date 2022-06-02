package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
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

    @MockBean
    private UCSBCurriculumService ucsbCurriculumService;

    // Authorization tests for /api/addedcourses/post
    @Test
    public void api_addedcourses_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/addedcourses/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_addedcourses_post__wrong_enrollcode__returns_400() throws Exception {
        mockMvc.perform(post("/api/addedcourses/post?enrollCd=wrong code length&psId=123")
                .with(csrf()))
            .andExpect(status().is(400));
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
}
