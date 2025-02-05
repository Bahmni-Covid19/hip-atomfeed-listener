package org.bahmni.module.hipfeedintegration.atomfeed.contract.patient;

import java.util.List;

public class CareContext {
    private String Display;
    private String ReferenceNumber;
    private List<String> HiTypes;

    public String getReferenceNumber() {
        return ReferenceNumber;
    }

    public void setReferenceNumber(String referenceNumber) {
        this.ReferenceNumber = referenceNumber;
    }

    public String getDisplay() {
        return Display;
    }

    public void setDisplay(String display) {
        this.Display = display;
    }

    public List<String> getHiTypes() {
        return HiTypes;
    }

    public void setHiTypes(List<String> hiTypes) {
        HiTypes = hiTypes;
    }
}
