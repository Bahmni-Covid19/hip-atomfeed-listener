package org.bahmni.module.hipfeedintegration.atomfeed.mappers;

import org.bahmni.module.hipfeedintegration.atomfeed.contract.encounter.OpenMRSEncounter;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;

public class OpenMRSEncounterMapper {
    private ObjectMapper objectMapper;

    public OpenMRSEncounterMapper(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
    }

    public OpenMRSEncounter map(String encounterJSON) throws IOException {
        return objectMapper.readValue(encounterJSON, OpenMRSEncounter.class);
    }
}
