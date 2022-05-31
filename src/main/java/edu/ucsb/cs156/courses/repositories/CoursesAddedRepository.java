package edu.ucsb.cs156.courses.repositories;

import edu.ucsb.cs156.courses.entities.CoursesAdded;
import edu.ucsb.cs156.courses.entities.User;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;


@Repository
public interface CoursesAddedRepository extends CrudRepository<CoursesAdded, Long> {
  Optional<CoursesAdded> findByIdAndUser(long id, User user);
  Iterable<CoursesAdded> findAllByUserId(Long user_id);
}

