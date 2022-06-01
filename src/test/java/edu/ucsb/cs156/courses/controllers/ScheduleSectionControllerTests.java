package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.repositories.UserRepository;
import edu.ucsb.cs156.courses.services.UCSBCurriculumService;
import edu.ucsb.cs156.courses.testconfig.TestConfig;
import edu.ucsb.cs156.courses.ControllerTestCase;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
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
        mockMvc.perform(get("/api/schedulesection"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "USER" })
    @Test
    public void sections_admin_all__user_logged_in__returns_403() throws Exception {
        mockMvc.perform(get("/api/schedulesection/user"))
                .andExpect(status().is(403));
    }

    @WithMockUser(roles = { "ADMIN" })
    @Test
    public void sections_admin_all__admin_logged_in__returns_200() throws Exception {
        mockMvc.perform(get("/api/schedulesection"))
                .andExpect(status().isOk());
    }

}