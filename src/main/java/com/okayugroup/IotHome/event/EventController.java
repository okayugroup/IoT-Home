package com.okayugroup.IotHome.event;

import com.okayugroup.IotHome.LogController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

public class EventController {
    private static Map<String, Map<String, List<Event>>> tree = null;
    public static Map<String, Map<String, List<Event>>> getTree() {
        if (tree == null) {
            tree = Map.of("get", Map.of("profile1", new ArrayList<>(List.of(CommandEvent.CmdPromptCommand().setArgs("dir"), FileExecutionEvent.PlaySound().setArgs("C:\\Users\\yaido\\Music\\ドーン.mp3")))));
        }
        return tree;
    }

    public static boolean execute(String root, String name) {
        return execute(getEvents(root, name));
    }
    public static boolean execute(List<Event> events) {
        if (events != null) {
            EventResult result = null;
            for (var event : events) {
                try {
                    result = event.execute(result);
                    if (result == EventResult.ERROR) {
                        LogController.LOGGER.log(LogController.LogLevel.DEBUG, event.name + "からエラーが返りました。中断します。");
                        return false;
                    } else {
                        LogController.LOGGER.log(LogController.LogLevel.DEBUG, event.name + "が正常に終了し、" + result.result() + "が返りました。");
                    }

                } catch (Exception e) {
                    LogController.LOGGER.log(LogController.LogLevel.ERROR, "Unhandled exception: " + e.getMessage());
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
