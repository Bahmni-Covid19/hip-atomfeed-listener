package org.bahmni.module.hipfeedintegration.atomfeed.mappers;

import com.fasterxml.jackson.databind.ObjectMapper;
import org.bahmni.module.hipfeedintegration.atomfeed.contract.globalProperty.OpenMRSProperties;
import org.bahmni.webclients.ObjectMapperRepository;

import java.io.IOException;

public class OpenMRSPropertiesMapper {
    private ObjectMapper objectMapper;

    public OpenMRSPropertiesMapper() {
        this.objectMapper = ObjectMapperRepository.objectMapper;
    }

    public OpenMRSProperties map(String JSON) throws IOException {
        return objectMapper.readValue(JSON, OpenMRSProperties.class);
    }
}
