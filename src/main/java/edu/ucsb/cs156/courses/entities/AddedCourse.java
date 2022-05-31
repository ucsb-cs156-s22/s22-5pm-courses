<<<<<<< HEAD:src/main/java/edu/ucsb/cs156/courses/entities/CoursesAdded.java
package edu.ucsb.cs156.courses.entities;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "coursesadded")
public class CoursesAdded {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String enrollCd;

  @ManyToOne
  @JoinColumn(name = "ps_id")
  private long psId;

  private String quarter;
}

// id
// enrollCd (enrollment code)
// psId (personal schedule id)
=======
package edu.ucsb.cs156.courses.entities;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "added_course")
public class AddedCourse {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  private String enrollCd;

  @ManyToOne
  @JoinColumn(name="ps_id", nullable=false)
  private PersonalSchedule personalSchedule;
}

// id
// enrollCd (enrollment code)
// psId (personal schedule id)
>>>>>>> cdfe474ca25ab59d714928664db71a07a25e00d5:src/main/java/edu/ucsb/cs156/courses/entities/AddedCourse.java
// yyyyq (the quarter in yyyyq format)