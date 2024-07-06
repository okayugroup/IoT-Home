package com.okayugroup.IotHome.event;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;

import java.io.File;
import java.io.IOException;
import java.util.*;

public record UserEventsObject(Map<String, Map<String, LinkedEvent>> inputs, List<LinkedEvent> events) {
    public static final ObjectMapper objectMapper = new ObjectMapper().enable(SerializationFeature.INDENT_OUTPUT);

    public static UserEventsObject fromFile() throws Exception {
        JsonNode node = objectMapper.readTree(new File("inputs.json"));
        List<LinkedEvent> events = new ArrayList<>();
        List<List<Integer>> indices = new ArrayList<>();
        for (JsonNode node1 : node.get("events")) {
            var value = node1.get("event");
            Event<?> event = EventController.EVENT_DICT.get(value.get("type").asText()).getNew();
            ArrayNode argsNode = ((ArrayNode) value.get("args"));
            String[] args = new String[argsNode.size()];
            for (int i = 0; i < argsNode.size(); i++) {
                args[i] = argsNode.get(i).asText();
            }
            event.setArgs(args);
            List<Integer> indices2 = new ArrayList<>();
            for (JsonNode link : node1.get("links")) {
                indices2.add(link.asInt());
            }

            events.add(new LinkedEvent(event, node1.get("x").asDouble(), node1.get("y").asDouble(), node1.get("width").asDouble(), node1.get("height").asDouble()));
            indices.add(indices2);
        }
        for (int i = 0; i < events.size(); i++) {
            Collections.addAll(events.get(i).getEvents(), indices.get(i).stream().map(events::get).toArray(LinkedEvent[]::new));
        }
        JsonNode children = node.get("input");
        Map<String, Map<String, LinkedEvent>> tree = new HashMap<>();
        Iterator<Map.Entry<String, JsonNode>> rootNames = children.fields();
        while (rootNames.hasNext()) {
            Map.Entry<String, JsonNode> childE = rootNames.next();
            Map<String, LinkedEvent> child = new HashMap<>();
            Iterator<Map.Entry<String, JsonNode>> childNames = childE.getValue().fields();
            while (childNames.hasNext()) {
                Map.Entry<String, JsonNode> childNameE = childNames.next();
                int index = childNameE.getValue().intValue();

                child.put(childNameE.getKey(), events.get(index));
            }
            tree.put(childE.getKey(), child);
        }
        return new UserEventsObject(tree, events);
    }

    public void saveToFile() throws IOException {
        ObjectNode root = objectMapper.createObjectNode();
        ObjectNode children = objectMapper.createObjectNode();
        for (Map.Entry<String, Map<String, LinkedEvent>> entry : inputs.entrySet()) {
            ObjectNode child = objectMapper.createObjectNode();
            for (Map.Entry<String, LinkedEvent> entry2 : entry.getValue().entrySet()) {
                child.put(entry2.getKey(), events.indexOf(entry2.getValue()));
            }
            children.set(entry.getKey(), child);
        }
        root.set("input", children);
        ArrayNode values = objectMapper.createArrayNode();
        for (LinkedEvent event : events) {
            ObjectNode eventNode = objectMapper.createObjectNode();
            ArrayNode links = objectMapper.createArrayNode();
            for (LinkedEvent linkedEvent : event.getEvents()) {
                links.add(events.indexOf(linkedEvent));
            }
            eventNode.set("links", links);
            ObjectNode value = objectMapper.createObjectNode();
            value.put("type", event.getEvent().getClass().getTypeName());
            ArrayNode args = objectMapper.createArrayNode();
            for (String arg : event.getEvent().getArgs()) {
                args.add(arg);
            }
            value.set("args", args);
            values.add(value);
        }
        root.set("events", values);
        objectMapper.writeValue(new File("inputs.json"), root);
    }
}
