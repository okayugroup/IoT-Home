package com.okayugroup.IotHome.event;

import com.okayugroup.IotHome.LogController;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class EventController {
    private static Map<String, Map<String, List<Event>>> tree = null;
    public static Map<String, Map<String, List<Event>>> getTree() {
        if (tree == null) {
            tree = Map.of("get", Map.of("profile1", new ArrayList<>(List.of(CommandEvent.CmdPromptCommand().setArgs("dir")))));
        }
        return tree;
    }
    public static boolean execute(String root, String name) {
        List<Event> events = getEvents(root, name);
        if (events != null) {
            EventResult result = null;
            for (var event : events) {
                try {
                    result = event.execute(result);
                } catch (Exception e) {
                    LogController.LOGGER.log("Unhandled exception: " + e.getMessage());
                }
            }
            return true;
        } else {
            return false;
        }
    }
    public static List<Event> getEvents(String root, String name){
        var tree = getTree();
        if (tree.containsKey(root)) {
            var node = tree.get(root);
            if (node.containsKey(name)) {
                return node.get(name);
            }
        }
        return null;
    }

    public static void main(String ...args) {
        boolean pr = execute("get", "profile1");
        System.out.println(pr);
    }
}
