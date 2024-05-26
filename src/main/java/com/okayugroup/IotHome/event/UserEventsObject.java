package com.okayugroup.IotHome.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public record UserEventsObject(Map<String, Map<String, List<Event>>> events, String rootName) {
    public static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static UserEventsObject fromFile() throws IOException {
        JsonNode node = objectMapper.readTree(new File("events.json"));
        String rootName = "api"; //node.get("root").asText()
        JsonNode children = node.get("children");
        Map<String, Map<String, List<Event>>> tree = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> rootNames = children.fields();
        while (rootNames.hasNext()) {
            Map.Entry<String, JsonNode> childE = rootNames.next();
            Map<String, List<Event>> child = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> childNames = childE.getValue().fields();
            while (childNames.hasNext()) {
                Map.Entry<String, JsonNode> childNameE = childNames.next();
                List<Event> events = new ArrayList<>();
                for (JsonNode value : childNameE.getValue()) {
                    Event event = EventController.EVENT_DICT.get(value.get("type").asText()).getCopy(value.get("id").asInt());
                    ArrayNode argsNode = ((ArrayNode) value.get("args"));
                    String[] args = new String[argsNode.size()];
                    for (int i = 0; i < argsNode.size(); i++) {
                        args[i] = argsNode.get(i).asText();
                    }
                    event.setArgs(args);
                    events.add(event);
                }
                child.put(childNameE.getKey(), events);
            }
            tree.put(childE.getKey(), child);
        }

        return new UserEventsObject(tree, rootName);
    }

    public void saveToFile() throws IOException {
        ObjectNode root = objectMapper.createObjectNode();
        root.put("root", rootName);
        ObjectNode children = objectMapper.createObjectNode();
        for (Map.Entry<String, Map<String, List<Event>>> entry : events.entrySet()) {
            ObjectNode child = objectMapper.createObjectNode();
            for (Map.Entry<String, List<Event>> entry2 : entry.getValue().entrySet()) {
                ArrayNode values = objectMapper.createArrayNode();
                for (Event event : entry2.getValue()) {
                    ObjectNode value = objectMapper.createObjectNode();
                    value.put("type", event.getClass().getSimpleName());
                    value.put("id", event.typeIndex);
                    ArrayNode args = objectMapper.createArrayNode();
                    for (String arg : event.getArgs()) {
                        args.add(arg);
                    }
                    value.set("args", args);
                    values.add(value);
                }
                child.set(entry2.getKey(), values);
            }
            children.set(entry.getKey(), child);
        }
        root.set("children", children);
        objectMapper.writeValue(new File("events.json"), root);
    }
}
