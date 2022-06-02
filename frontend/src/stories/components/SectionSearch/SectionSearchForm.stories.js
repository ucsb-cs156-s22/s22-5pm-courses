import React from "react";

import SectionSearchForm from "main/components/SectionSearch/SectionSearchForm";

export default {
  title: "components/SectionSearch/SectionSearchForm",
  component: SectionSearchForm,
};

const Template = (args) => {
  return <SectionSearchForm {...args} />;
};

export const Default = Template.bind({});

Default.args = {
  submitText: "Create",
  submitAction: () => {
    console.log("Submit was clicked");
  },
};
