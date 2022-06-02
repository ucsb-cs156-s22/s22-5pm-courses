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
// yyyyq (the quarter in yyyyq format)