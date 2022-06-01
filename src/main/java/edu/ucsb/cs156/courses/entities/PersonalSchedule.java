package edu.ucsb.cs156.courses.entities;

import java.util.List;
import javax.persistence.Entity;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.GeneratedValue;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
@Entity(name = "personalschedule")
public class PersonalSchedule {
  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private long id;

  @ManyToOne
  @JoinColumn(name = "user_id")
  private User user;

  private String name;
  private String description;
  private String quarter;

  // @OneToMany(mappedBy="personalSchedule")
  // private List<AddedCourse> addedCourses;
  
}