package org.bahmni.module.pacsintegration.atomfeed.contract.encounter;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSObs {

    private String uuid;
    private OpenMRSConcept concept;

    public OpenMRSObs() { }

    public OpenMRSObs(String uuid, OpenMRSConcept concept) {
        this.uuid = uuid;
        this.concept = concept;
    }

    public OpenMRSConcept getConcept() {
        return concept;
    }

    public void setConcept(OpenMRSConcept concept) {
        this.concept = concept;
    }

}
