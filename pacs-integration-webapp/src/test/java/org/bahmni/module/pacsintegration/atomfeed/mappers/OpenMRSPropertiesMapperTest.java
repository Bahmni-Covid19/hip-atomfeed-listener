package org.bahmni.module.pacsintegration.atomfeed.mappers;

import org.bahmni.module.pacsintegration.atomfeed.contract.globalProperty.OpenMRSProperties;
import org.junit.Assert;
import org.junit.Test;

import java.io.IOException;

public class OpenMRSPropertiesMapperTest {

        @Test
        public void shouldMapJsonIntoOpenMRSProperties() throws IOException {
        String encounterJson = "{\"uuid\":\"40b2ff39-b115-4ea2-9719\"," +
                "\"property\":\"encounterTypesToBeIgnored\"," +
                "\"value\":\"registration,admission,transfer\"}";
        OpenMRSPropertiesMapper openMRSPropertiesMapper = new OpenMRSPropertiesMapper();
        OpenMRSProperties openMRSProperties = openMRSPropertiesMapper.map(encounterJson);
        Assert.assertNotNull(openMRSProperties);

        Assert.assertEquals("registration,admission,transfer", openMRSProperties.getValue());
        }

}