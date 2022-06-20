package org.bahmni.module.hipfeedintegration.atomfeed.contract.globalProperty;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@JsonIgnoreProperties(ignoreUnknown = true)
public class OpenMRSProperties {

    private String uuid;
    private String property;
    private String value;

    public OpenMRSProperties(){
    }

    public String getValue() {
        return value;
    }

    public void setValue(String value) {
        this.value = value;
    }
}
