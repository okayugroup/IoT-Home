package com.okayugroup.IotHome.event;

import com.okayugroup.IotHome.LogController;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EventController {
    private static UserEventsObject tree = null;
    public static final Map<String, Event> EVENT_DICT = initEvents();

    private static @NotNull Map<String, Event> initEvents() {
        Map<String, Event> events = new LinkedHashMap<>();
        events.put(CommandEvent.class.getSimpleName(), CommandEvent.ConsoleCommand());
        events.put(FileExecutionEvent.class.getSimpleName(), FileExecutionEvent.ExecuteFile());
        return events;
    }

    public static UserEventsObject getTree() {
        if (tree == null) {
            try {
                tree = UserEventsObject.fromFile();
            } catch (Exception e) {
                System.err.println("ファイルの読み込み中にエラーが発生しました。");
                e.printStackTrace(System.out);
                File file = new File("events.json");
                try {
                    boolean ignored = file.createNewFile();
                    tree = new UserEventsObject(new HashMap<>(), "api");
                    tree.saveToFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
        return tree;
    }

    public static EventResult execute(String root, String name) {
        return execute(getEvents(root, name));
    }
    public static EventResult execute(List<Event> events) {
        if (events != null) {
            EventResult result = null;
            for (var event : events) {
                try {
                    result = event.execute(result);
                    if (result == EventResult.ERROR) {
                        LogController.LOGGER.log(LogController.LogLevel.DEBUG, event.name + "からエラーが返りました。中断します。");
                        return result;
                    } else {
                        LogController.LOGGER.log(LogController.LogLevel.DEBUG, event.name + "が正常に終了し、" + result.result() + "が返りました。");
                    }

                } catch (Exception e) {
                    LogController.LOGGER.log(LogController.LogLevel.ERROR, "ハンドルされていない例外: " + e.getMessage());
                }
            }
            return result;
        } else {
            return EventResult.ERROR;
        }
    }
    public static List<Event> getEvents(String root, String name){
        var tree = getTree();
        if (tree.events().containsKey(root)) {
            var node = tree.events().get(root);
            if (node.containsKey(name)) {
                return node.get(name);
            }
        }
        return null;
    }

    public static boolean containsEvent(String root, String text) {

        return getTree().events().get(root).containsKey(text);
    }
    public static void setEvent(String root, String oldName, String newName) {
        Map<String, List<Event>> rootMap = getTree().events().get(root);
        List<Event> value = rootMap.get(oldName);
        rootMap.remove(oldName, value);
        rootMap.put(newName, value);
    }
    public static void setEvent(String oldRootName, String newRootName) {
        Map<String, List<Event>> value = getTree().events().get(oldRootName);
        getTree().events().remove(oldRootName, value);
        getTree().events().put(newRootName, value);
    }

    public static boolean containsOnRoot(String root) {
        return getTree().events().containsKey(root);
    }

    public static void putNewOnRoot(String name) {
        getTree().events().put(name, new HashMap<>());
    }

    public static void putNewEvent(String root, String name) {
        getTree().events().get(root).put(name, new ArrayList<>());
    }

    public static void saveTree() {
        try {
            getTree().saveToFile();
        } catch (IOException e) {
            LogController.LOGGER.log(LogController.LogLevel.ERROR, "ファイルの書き込み中にエラーが発生しました。");
            e.printStackTrace(System.out);
        }
    }
}
