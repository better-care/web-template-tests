package com.thinkehr.thinkehrwebtests.transformer;
import org.openehr.jaxb.rm.Action;
import org.openehr.jaxb.rm.Cluster;
import org.openehr.jaxb.rm.Composition;
import org.openehr.jaxb.rm.Observation;
import com.thinkehr.thinkehrwebtests.transformer.Transformer;

import java.util.Map;

public class RmObjectTest {

    public void testCreateActionFlat(Transformer transformer) throws Exception {

        Map<String, Object> actionMap = transformer.rawToFlatDefault("/rmobject/medication-administration-action-flat.json");
        Map<String, Object> compositionMap = transformer.rawToFlatDefault("/rmobject/medication-administration-composition-flat.json");


//        falt "/rmobject/medication-administration-action-flat.json"
    }

//    @Test
//    public void testCreateActionFlat() throws JAXBException, IOException {
//        WebTemplateBuilderContext builderContext = new WebTemplateBuilderContext("sl");
//
//        WebTemplate webTemplate = WTBuilder.build(getTemplate("rmobject/OPENeP - Medication Administration.xml"), builderContext);
//
//        ObjectMapper mapper = new ObjectMapper();
//        mapper.registerModule(new JodaModule());
//        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);
//
//        Map<String, Object> actionMap = mapper.readValue(
//                IOUtils.toString(PskyBuilderTest.class.getResource("/rmobject/medication-administration-action-flat.json"), StandardCharsets.UTF_8),
//                new TypeReference<Map<String, Object>>() {
//                });
//
//        Map<String, Object> compositionMap = mapper.readValue(
//                IOUtils.toString(PskyBuilderTest.class.getResource("/rmobject/medication-administration-composition-flat.json"), StandardCharsets.UTF_8),
//                new TypeReference<Map<String, Object>>() {
//                });
//
//        Composition composition = webTemplate.build(compositionMap, new BuilderContext());
//
//        NameAndNodeMatchingPathValueExtractor extractor = new NameAndNodeMatchingPathValueExtractor("/content[openEHR-EHR-ACTION.medication.v1]");
//        Action compositionAction = (Action)extractor.getValue(composition).iterator().next();
//
//        test(webTemplate.build(actionMap, PathDto.forAqlPath("/content[openEHR-EHR-ACTION.medication.v1]"), new BuilderContext()),
//             compositionAction);
//
//        test(webTemplate.build(actionMap, PathDto.forWebTemplatePath("medication_administration/medication_management"), new BuilderContext()),
//             compositionAction);
//    }
}
