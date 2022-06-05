package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.entities.PersonalSchedule;
import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.errors.EntityNotFoundException;
import edu.ucsb.cs156.courses.models.CurrentUser;
import edu.ucsb.cs156.courses.repositories.PersonalScheduleRepository;

import edu.ucsb.cs156.courses.repositories.AddedCourseRepository;

import edu.ucsb.cs156.courses.entities.AddedCourse;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import lombok.extern.slf4j.Slf4j;

import java.util.List;
import java.util.Collections;
import java.util.ArrayList;

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
import org.springframework.web.server.ResponseStatusException;
import org.springframework.http.HttpStatus;

import javax.validation.Valid;
import java.util.Optional;

import edu.ucsb.cs156.courses.services.UCSBCurriculumService;

@Api(description = "AddedCourses")
@RequestMapping("/api/addedcourses")
@RestController
@Slf4j
public class AddedCoursesController extends ApiController {

    @Autowired
    PersonalScheduleRepository personalScheduleRepository;

    @Autowired
    AddedCourseRepository addedCourseRepository;

    @Autowired
    UCSBCurriculumService ucsbCurriculumService;

    @ApiOperation(value = "Create a new course")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public AddedCourse postCourse(
            @ApiParam("enrollCd") @RequestParam String enrollCd,
            @ApiParam("psId") @RequestParam Long psId) {

        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={} psId={}", currentUser, psId);
        Optional<PersonalSchedule> personalSchedule = personalScheduleRepository.findByIdAndUser(psId, currentUser.getUser());


        if (String.valueOf(enrollCd).length() > 5){
            //Reject POST request if enrollCd is more than five digits
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "enrollCd should be no more than five digits");
        }

        if (!personalSchedule.isPresent())
        {
            //Reject POST request if the psId is not the psId of an existing Personal Schedule
            throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "PersonalSchedule doesn't exist!");
        }
        else
        {
            // Reject POST request if the quarter doesn't match the one on the psId
            PersonalSchedule currentSchedule = personalSchedule.get();
            String retVal = ucsbCurriculumService.getSectionJSON(currentSchedule.getQuarter(), enrollCd);
            if  (retVal.equals("{\"error\": \"Section not found\"}")){
                throw new ResponseStatusException(HttpStatus.BAD_REQUEST, "The enrollCd does not exist in the given quarter");
            }
        }


        AddedCourse addedCourse = new AddedCourse();
        addedCourse.setEnrollCd(enrollCd);
        addedCourse.setPersonalSchedule(personalSchedule.get());
        AddedCourse savedAddedCourse = addedCourseRepository.save(addedCourse);
        return savedAddedCourse;
    }

    @ApiOperation(value = "Get sections from a single schedule")
    @PreAuthorize("hasRole('ROLE_ADMIN')")
    @GetMapping("/admin")
    public ResponseEntity<List<String>> thisScheduleSections(
            @ApiParam("id") @RequestParam Long id
    ) {
        PersonalSchedule personalSchedule = personalScheduleRepository.findById(id)
        .orElseThrow(() -> new EntityNotFoundException(PersonalSchedule.class, id));

        var quarter = personalSchedule.getQuarter();
        Iterable<AddedCourse> classesAdded = addedCourseRepository.findAllByPersonalSchedule(personalSchedule);
        List<String> listOfJSON = new ArrayList<>();

        for(AddedCourse currentClass : classesAdded)
        {
            String enrollCode = currentClass.getEnrollCd();
            String currentSection = ucsbCurriculumService.getSectionJSON(quarter, enrollCode);
            listOfJSON.add(currentSection);
        }

        return ResponseEntity.ok().body(listOfJSON);
    }


    @ApiOperation(value = "Get sections from a single schedule (if it belongs to current user)")
    @PreAuthorize("hasRole('ROLE_USER')")
    @GetMapping("")
    public ResponseEntity<List<String>> thisUserSections(
            @ApiParam("id") @RequestParam Long id) {
        User currentUser = getCurrentUser().getUser();
        PersonalSchedule personalSchedule = personalScheduleRepository.findByIdAndUser(id, currentUser)
          .orElseThrow(() -> new EntityNotFoundException(PersonalSchedule.class, id));

        var quarter = personalSchedule.getQuarter();
        Iterable<AddedCourse> classesAdded = addedCourseRepository.findAllByPersonalSchedule(personalSchedule);
        List<String> listOfJSON = new ArrayList<>();
        for(AddedCourse currentClass : classesAdded)
        {
            String enrollCode = currentClass.getEnrollCd();
            String currentSection = ucsbCurriculumService.getSectionJSON(quarter, enrollCode);
            listOfJSON.add(currentSection);
        }

        return ResponseEntity.ok().body(listOfJSON);
    }

}
