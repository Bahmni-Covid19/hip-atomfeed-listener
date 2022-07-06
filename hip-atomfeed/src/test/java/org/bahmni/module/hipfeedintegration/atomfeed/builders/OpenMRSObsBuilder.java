package org.bahmni.module.hipfeedintegration.atomfeed.builders;

import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.*;

import java.util.Arrays;
import java.util.List;

public class OpenMRSObsBuilder {
    private OpenMRSObs openMRSObs;

    public OpenMRSObsBuilder() {
        openMRSObs = new OpenMRSObs();
    }

    public OpenMRSObsBuilder withConcept(OpenMRSConcept openMRSConcept) {
        openMRSObs.setConcept(openMRSConcept);
        return this;
    }

    public OpenMRSObsBuilder withGroupMembers(List<OpenMRSObs> groupMembers) {
        openMRSObs.setGroupMembers(groupMembers);
        return this;
    }

    public OpenMRSObs build() {
        return openMRSObs;
    }
}
