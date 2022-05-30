package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.CoursesAdded;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.repositories.CoursesAddedRepository;

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

@WebMvcTest(controllers = CoursesAddedController.class)
@Import(TestConfig.class)
public class CoursesAddedControllerTests extends ControllerTestCase {
    @MockBean
    CoursesAddedRepository coursesaddedRepository;

    @MockBean
    UserRepository userRepository;

    // Authorization tests for /api/coursesadded/post
    @Test
    public void api_coursesadded_post__logged_out__returns_403() throws Exception {
        mockMvc.perform(post("/api/coursesadded/post"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void api_coursesadded_post__user_logged_in() throws Exception {
        // arrange

        User thisUser = currentUserService.getCurrentUser().getUser();

        CoursesAdded expectedCourses = CoursesAdded.builder().enrollCd("Test code").psId(123).quarter("20222").user(thisUser).id(0L).build();

        when(coursesaddedRepository.save(eq(expectedCourses))).thenReturn(expectedCourses);

        // act
        MvcResult response = mockMvc.perform(
                post("/api/coursesadded/post?enrollCd=Test code&psId=123&quarter=20222")
                        .with(csrf()))
                .andExpect(status().isOk()).andReturn();

        // assert
        verify(coursesaddedRepository, times(1)).save(expectedCourses);
        String expectedJson = mapper.writeValueAsString(expectedCourses);
        String responseString = response.getResponse().getContentAsString();
        assertEquals(expectedJson, responseString);
    }
}
