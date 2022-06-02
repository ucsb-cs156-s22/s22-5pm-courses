import { useState } from "react";
import BasicLayout from "main/layouts/BasicLayout/BasicLayout";
import SectionSearchForm from "main/components/SectionSearch/SectionSearchForm";
import { useBackendMutation } from "main/utils/useBackend";
// import SectionsTable from "main/components/SectionSearch/SectionSearchTable";

export default function SectionSearchIndexPage() {
  const [_sectionJSON, setSectionJSON] = useState([]);
  const objectToAxiosParams = (query) => ({
    url: "/api/sections/basicsearch",
    params: {
      quarter: query.quarter,
      subjectArea: query.subject,
      courseLevel: query.level,
    },
  });

  const onSuccess = (section) => {
    console.log()
    setSectionJSON(section);
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
        {/* <SectionsTable sections={sectionJSON} /> */}
      </div>
    </BasicLayout>
  );
}
