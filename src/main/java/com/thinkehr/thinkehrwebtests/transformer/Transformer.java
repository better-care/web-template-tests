package com.thinkehr.thinkehrwebtests.transformer;


import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.datatype.joda.JodaModule;

// todo T should be modev out and proper types implented, as they will all be the same
public interface Transformer {

    // JSON and xml correspond to flat and structured, name methods accordingly
    public String rawToFlat(String input);

    public String rawToStructured(String input);

    public String structuredToFlat(String input);

    public String structuredToRaw(String input);

    public String flatToRaw(String input);

    public String flatToStructured(String input);

    public String readFile(String input);


    default Map<String, Object> rawToFlatDefault(String input) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        Map<String, Object> stringObjectMap = mapper.readValue(
                readFile(input),
                new TypeReference<Map<String, Object>>() {
                });

        return stringObjectMap;
    }

    default JsonNode rawToStructuredDefault(String input) throws JsonProcessingException {
        ObjectMapper mapper = new ObjectMapper();
        mapper.registerModule(new JodaModule());
        mapper.disable(SerializationFeature.WRITE_DATES_AS_TIMESTAMPS);

        JsonNode jsonNode = mapper.readValue(
                readFile(input),
                JsonNode.class);

        return jsonNode;
    }

}
