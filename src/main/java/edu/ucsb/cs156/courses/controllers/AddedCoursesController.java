package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.errors.EntityNotFoundException;
import edu.ucsb.cs156.courses.models.CurrentUser;
import edu.ucsb.cs156.courses.repositories.AddedCourseRepository;
import edu.ucsb.cs156.courses.repositories.PersonalScheduleRepository;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.Optional;

@Api(description = "AddedCourses")
@RequestMapping("/api/addedcourses")
@RestController
@Slf4j
public class AddedCoursesController extends ApiController{
    @Autowired
    AddedCourseRepository addedCourseRepository;

    @Autowired
    PersonalScheduleRepository personalScheduleRepository;

    @ApiOperation(value = "Create a new course")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public AddedCourse postCourse(
            @ApiParam("enrollCd") @RequestParam String enrollCd,
            @ApiParam("psId") @RequestParam long psId) {

        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={} psId={}", currentUser, psId);
        Optional<PersonalSchedule> personalSchedule = personalScheduleRepository.findByIdAndUser(psId, currentUser.getUser());

        // if (!personalSchedule.isPresent())
        // {
        //     // TODO: Rejection of POST request somehow
        // }
        // TODO: Get quarter from personalSchedule and determine if enrollCode is valid thru UCSB courses API

        AddedCourse addedCourse = new AddedCourse();
        addedCourse.setEnrollCd(enrollCd);
        addedCourse.setPersonalSchedule(personalSchedule.get());
        AddedCourse savedAddedCourse = addedCourseRepository.save(addedCourse);
        return savedAddedCourse;
    }

    @ApiOperation(value = "List this Personal Schedule's Added Courses")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("/all")
    public Iterable<AddedCourse> thisUsersCourses(
            @ApiParam("psId") @RequestParam Long psId) {
                
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={} psId={}", currentUser, psId);

        PersonalSchedule personalschedule = personalscheduleRepository.findByIdAndUser(psId, currentUser)
          .orElseThrow(() -> new EntityNotFoundException(PersonalSchedule.class, psId));
        
        Iterable<AddedCourse> schedulesCourses = addedCourseRepository.findAllByPersonalSchedule(personalSchedule);

        return schedulesCourses;
    }
}
