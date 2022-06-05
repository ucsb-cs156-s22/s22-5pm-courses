package edu.ucsb.cs156.courses.documents;

import java.util.List;
import java.util.Objects;
import java.util.ArrayList;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.mongodb.core.mapping.Document;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Document(collection = "courses")
public class Course {
    private String quarter;
    private String courseId;
    private String title;
    private String description;
    private List<Section> classSections;
    private List<GeneralEducation> generalEducation;
    private FinalExam finalExam;

    public List<ConvertedSection> convertedSections() {

        List<ConvertedSection> result = new ArrayList<ConvertedSection>();

        CourseInfo courseInfo = CourseInfo.builder()
                .quarter(this.getQuarter())
                .courseId(this.getCourseId())
                .title(this.getTitle())
                .description(this.getDescription())
                .build();

        for (Section section : this.getClassSections()) {
            ConvertedSection cs = ConvertedSection.builder()
                    .courseInfo(courseInfo)
                    .section(section)
                    .build();
            result.add(cs);

        }
        return result;
    }
}
