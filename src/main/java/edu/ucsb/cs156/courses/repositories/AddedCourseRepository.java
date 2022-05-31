package edu.ucsb.cs156.courses.repositories;

import edu.ucsb.cs156.courses.entities.AddedCourse;
import edu.ucsb.cs156.courses.entities.PersonalSchedule;

import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AddedCourseRepository extends CrudRepository<AddedCourse, Long> {
  Iterable<AddedCourse> findAllByPersonalSchedule(PersonalSchedule personalSchedule);
}
