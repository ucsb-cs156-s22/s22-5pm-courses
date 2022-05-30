package edu.ucsb.cs156.courses.controllers;

import edu.ucsb.cs156.courses.entities.CoursesAdded;
import edu.ucsb.cs156.courses.entities.User;
import edu.ucsb.cs156.courses.errors.EntityNotFoundException;
import edu.ucsb.cs156.courses.models.CurrentUser;
import edu.ucsb.cs156.courses.repositories.CoursesAddedRepository;
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

@Api(description = "CoursesAdded")
@RequestMapping("/api/coursesadded")
@RestController
@Slf4j
public class CoursesAddedController extends ApiController{
    @Autowired
    CoursesAddedRepository coursesaddedRepository;

    @ApiOperation(value = "Create a new course")
    @PreAuthorize("hasRole('ROLE_USER')")
    @PostMapping("/post")
    public CoursesAdded postCourse(
            @ApiParam("enrollCd") @RequestParam String enrollCd,
            @ApiParam("psId") @RequestParam long psId,
            @ApiParam("quarter") @RequestParam String quarter) {
        CurrentUser currentUser = getCurrentUser();
        log.info("currentUser={}", currentUser);

        CoursesAdded coursesadded = new CoursesAdded();
        coursesadded.setUser(currentUser.getUser());
        coursesadded.setEnrollCd(enrollCd);
        coursesadded.setPsId(psId);
        coursesadded.setQuarter(quarter);
        CoursesAdded savedCoursesAdded = coursesaddedRepository.save(coursesadded);
        return savedCoursesAdded;
    }
}
