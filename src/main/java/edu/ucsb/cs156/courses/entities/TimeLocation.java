package edu.ucsb.cs156.courses.entities;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;
import lombok.Builder;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class TimeLocation {
    private String room;
    private String building;
    private String roomCapacity;
    private String days; 
    private String beginTime; 
    private String endTime;
}