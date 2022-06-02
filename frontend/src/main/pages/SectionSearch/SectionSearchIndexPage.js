import { useState } from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import SectionSearchForm from "main/components/SectionSearch/SectionSearchForm";
import { useBackendMutation } from "main/utils/useBackend";


export default function SectionSearchIndexPage() {
  const [_courseJSON, setCourseJSON] = useState([]);
  const objectToAxiosParams = (query) => ({
    url: "/api/sections/basicsearch",
    params: {
      quarter: query.quarter,
      subjectArea: query.subject,
      courseLevel: query.level,
    },
  });

  const onSuccess = (courses) => {
    setCourseJSON(courses.classes);
  };

  const mutation = useBackendMutation(
    objectToAxiosParams,
    { onSuccess },
    // Stryker disable next-line all : hard to set up test for caching
    []
  );

  async function fetchBasicCourseJSON(_event, query) {
    mutation.mutate(query);
  }
  return (
    <BasicLayout>
      <div className="pt-2">
        <h5>Section Search</h5>
        <SectionSearchForm fetchJSON={fetchBasicCourseJSON} />
      </div>
    </BasicLayout>
  );
}
