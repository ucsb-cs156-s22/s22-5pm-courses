package edu.ucsb.cs156.courses.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class Instructor {
    private String instructor;
    private String functionCode;
}
