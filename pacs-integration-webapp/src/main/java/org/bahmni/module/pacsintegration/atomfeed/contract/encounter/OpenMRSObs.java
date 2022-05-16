package org.bahmni.module.pacsintegration.atomfeed.contract.encounter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

import java.util.List;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSObs {

    private String uuid;
    private OpenMRSConcept concept;
    private List<OpenMRSObs> groupMembers;

    public OpenMRSObs() { }

    public OpenMRSObs(String uuid, OpenMRSConcept concept, List<OpenMRSObs> groupMembers) {
        this.uuid = uuid;
        this.concept = concept;
        this.groupMembers = groupMembers;
    }

    public OpenMRSConcept getConcept() {
        return concept;
    }

    public void setConcept(OpenMRSConcept concept) {
        this.concept = concept;
    }

    public List<OpenMRSObs> getGroupMembers() {
        return groupMembers;
    }

    public void setGroupMembers(List<OpenMRSObs> groupMembers) {
        this.groupMembers = groupMembers;
    }
}
