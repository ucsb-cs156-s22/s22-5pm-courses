import React from 'react';

import SectionsTable from 'main/components/Sections/SectionsTable';
import { sectionsFixtures } from 'fixtures/sectionsFixtures';
import { currentUserFixtures } from 'fixtures/currentUserFixtures';

export default {
    title: 'components/Sections/SectionsTable',
    component: SectionsTable
};

const Template = (args) => {
    return (
        <SectionsTable {...args} />
    )
};

export const Empty = Template.bind({});

Empty.args = {
    sections: []
};

export const ThreeSections = Template.bind({});

ThreeSections.args = {
    sections: sectionsFixtures.threeSections
};


export const ThreeSectionsUser = Template.bind({});
ThreeSectionsUser.args = {
    sections: sectionsFixtures.threeSections,
    currentUser: currentUserFixtures.adminUser
};

