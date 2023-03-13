package org.bahmni.module.hipfeedintegration.atomfeed.contract.patient;

public class CareContext {
    private String Display;
    private String ReferenceNumber;

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


}
