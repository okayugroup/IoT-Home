package com.okayugroup.IotHome.event;

import java.util.List;
import java.util.Map;

public class EventController {
    private static Map<String, Map<String, List<Event>>> tree = null;
    public static Map<String, Map<String, List<Event>>> getTree() {
        if (tree == null) {
            tree = Map.of("get", Map.of("profile1", List.of(new WindowsPowershellEvent("ls"), new CommandEvent("dir"))));
        }
        return tree;
    }
    public static boolean execute(String root, String name) throws Exception {
        List<Event> events = getEvents(root, name);
        if (events != null) {
            EventResult result = null;
            for (var event : events) {
                result = event.execute(result);
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

    public static void main(String ...args) throws Exception {
        boolean pr = execute("get", "profile1");
        System.out.println(pr);
    }
}
