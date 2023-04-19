package org.bahmni.module.hipfeedintegration.atomfeed.builders;

import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.*;

import java.util.*;

public class OpenMRSEncounterBuilder {
    private OpenMRSEncounter openMRSEncounter;

    public OpenMRSEncounterBuilder() {
        openMRSEncounter = new OpenMRSEncounter();
    }

    public OpenMRSEncounterBuilder withEncounterUuid(String encounterUuid) {
        openMRSEncounter.setEncounterUuid(encounterUuid);
        return this;
    }

    public OpenMRSEncounterBuilder withPatientUuid(String patientUuid) {
        openMRSEncounter.setPatientUuid(patientUuid);
        return this;
    }

    public OpenMRSEncounterBuilder withVisitUuid(String visitUuid) {
        openMRSEncounter.setVisitUuid(visitUuid);
        return this;
    }

    public OpenMRSEncounterBuilder withTestOrder(OpenMRSOrder order) {
        openMRSEncounter.addTestOrder(order);
        return this;
    }

    public OpenMRSEncounterBuilder withProvider(OpenMRSProvider openMRSProvider){
        openMRSEncounter.setProviders(Arrays.asList(openMRSProvider));
        return this;
    }

    public OpenMRSEncounterBuilder withEncounterType(String encounterType){
        openMRSEncounter.setEncounterType(encounterType);
        return this;
    }

    public OpenMRSEncounterBuilder withObservation(List<OpenMRSObs> obs){
        openMRSEncounter.setObservations(obs);
        return this;
    }

    public OpenMRSEncounter build() {
        return openMRSEncounter;
    }
}
