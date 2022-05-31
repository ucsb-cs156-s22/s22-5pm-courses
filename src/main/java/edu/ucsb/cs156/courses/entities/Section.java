package edu.ucsb.cs156.courses.entities;

import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;

import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import javax.persistence.Column;
import javax.persistence.ElementCollection;


import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "section")
public class Section {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  /** a unique number assigned to a section */
  private String enrollCode;
  /** section number of the course */
  private String section;
  /** session only for summer quarter */
  private String session;
  /** if the class is closed */
  private String classClosed;
  /** is course cancelled */
  private String courseCancelled;
  /**
   * Grading Options Code like Pass/No Pass (P/NP) Or Letter Grades (L).
   * 
   * @see <a href=
   *      "https://developer.ucsb.edu/content/student-record-code-lookups">
   *      https://developer.ucsb.edu/content/student-record-code-lookups</a>
   * 
   */
  private String gradingOptionCode;

  /** total number of enrollments in the course */
  private Integer enrolledTotal;
  /** max number of students can be enrolled in the section */
  private Integer maxEnroll;

  /** Secondary Status of the course */
  private String secondaryStatus;

  /** Is department approval required for enrollment in the section */
  private boolean departmentApprovalRequired;

  /** Is instructor approval required for enrollment in the section */
  private boolean instructorApprovalRequired;

  /** Is there restriction on the level of the course */
  private String restrictionLevel;

  /** Is there restriction on the major of the student */
  private String restrictionMajor;

  /** Is there restriction on the major and pass time of the student */
  private String restrictionMajorPass;

  /** Is there restriction on the minor of the student */
  private String restrictionMinor;

  /** Is there restriction on the minor and pass time of the student */
  private String restrictionMinorPass;

  /** Concurrent courses for the section */
  //@Column
  //@ElementCollection(targetClass=String.class)
  //private List<String> concurrentCourses;

  /**
   * List of {@link TimeLocation} objects for this course
   */
  //@Column
  //@ElementCollection(targetClass=TimeLocation.class)
  //private List<TimeLocation> timeLocations;
  /**
   * List of {@link Instructor} objects for this course
   */
  //@OneToMany(targetEntity=Section.class, mappedBy="college", fetch=FetchType.EAGER)
  //private List<Instructor> instructors;
}