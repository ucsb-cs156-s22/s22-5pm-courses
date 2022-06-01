package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;
import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.repositories.PersonalScheduleRepository;
import edu.ucsb.cs156.courses.repositories.AddedCourseRepository;


import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.springframework.boot.test.autoconfigure.web.servlet.WebMvcTest;
import org.springframework.boot.test.mock.mockito.MockBean;
import org.springframework.context.annotation.Import;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.test.web.servlet.MvcResult;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
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

@WebMvcTest(controllers = ScheduleSectionController.class)
@Import(TestConfig.class)
public class ScheduleSectionControllerTests extends ControllerTestCase {

    @MockBean
    PersonalScheduleRepository personalscheduleRepository;

    @MockBean
    UserRepository userRepository;

    @MockBean
    AddedCourseRepository addedCourseRepository;

    @MockBean
    UCSBCurriculumService ucsbcirService;

    @Test
    public void sections_admin_all__logged_out__returns_403() throws Exception {
        mockMvc.perform(get("/api/schedulesection/admin/all?id=1"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/schedulesection/admin/all?id=1"))
                .andExpect(status().is(403));
    }


    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_logged_in__returns_404() throws Exception {
        mockMvc.perform(get("/api/schedulesection/admin/all?id=1"))
                .andExpect(status().is(404));
    }



    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_all_id_does_not_exist() throws Exception {

        when(addedCourseRepository.findById(eq(7L))).thenReturn(Optional.empty());

        MvcResult response = mockMvc.perform(get("/api/schedulesection/admin/all?id=7"))
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

        PersonalSchedule personalSchedule = PersonalSchedule.builder().id(1).name("Ryan").description("Test").quarter("2022W").user(u1).build();
        AddedCourse ac1 = AddedCourse.builder().id(1).enrollCd("123").personalSchedule(personalSchedule).build();

        when(addedCourseRepository.findById(eq(1L))).thenReturn(Optional.of(ac1));
        verify(personalscheduleRepository, times(1)).findById(eq(7L));

        MvcResult response = mockMvc.perform(get("/api/schedulesection/admin/all?id=1"))
                                .andExpect(status().is(200)).andReturn();
        
        
        Map<String, Object> json = responseToJson(response);
        assertEquals("EntityNotFoundException", json.get("type"));
        assertEquals("PersonalSchedule with id 7 not found", json.get("message"));
    }
}