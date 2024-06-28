package com.okayugroup.IotHome.event;

import com.okayugroup.IotHome.LogController;
import com.okayugroup.IotHome.event.temporary.CommandEvent;
import com.okayugroup.IotHome.event.temporary.FileExecutionEvent;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EventController {
    private static UserEventsObject tree = null;
    public static final Map<String, Event<?>> EVENT_DICT = initEvents();

    private static @NotNull Map<String, Event<?>> initEvents() {
        Map<String, Event<?>> events = new LinkedHashMap<>();
        events.put(CommandEvent.ConsoleCommand.class.getSimpleName(), new CommandEvent.ConsoleCommand());
        events.put(CommandEvent.CommandPromptCommand.class.getSimpleName(), new CommandEvent.CommandPromptCommand());
        events.put(CommandEvent.PowershellCommand.class.getSimpleName(), new CommandEvent.PowershellCommand());
        events.put(FileExecutionEvent.ExecuteFile.class.getSimpleName(), new FileExecutionEvent.ExecuteFile());
        events.put(FileExecutionEvent.PlaySound.class.getSimpleName(), new FileExecutionEvent.PlaySound());
        return events;
    }

    public static UserEventsObject getTree() {
        if (tree == null) {
            try {
                tree = UserEventsObject.fromFile();
            } catch (Exception e) {
                System.err.println("ファイルの読み込み中にエラーが発生しました。");
                e.printStackTrace(System.out);
                File file = new File("inputs.json");
                try {
                    boolean ignored = file.createNewFile();
                    tree = new UserEventsObject(new HashMap<>(), new ArrayList<>());
                    tree.saveToFile();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }

            }
        }
        return tree;
    }

    @Nullable
    public static EventResult<?> execute(String root, String name) {
        LinkedEvent event = getEvents(root, name);
        if (event == null) return null;
        return event.execute();
    }

    public static LinkedEvent getEvents(String root, String name){
        var tree = getTree();
        if (tree.inputs().containsKey(root)) {
            var node = tree.inputs().get(root);
            if (node.containsKey(name)) {
                return node.get(name);
            }
        }
        return null;
    }

    /*public static void addEvent(Event<?> event) {
        if (event instanceof InputEvent<?> input) {
            if (!tree.inputs().containsKey(input.getParentName())) {
                HashMap<String, List<Event<?>>> h = new HashMap<>();
                List<Event<?>> l = new ArrayList<>();
                l.add(event);
                h.put(input.getChildName(), l);
                tree.inputs().put(input.getParentName(), h);
            } else if (!tree.inputs().get(input.getParentName()).containsKey(input.getChildName())) {
                List<Event<?>> l = new ArrayList<>();
                l.add(event);
                tree.inputs().get(input.getParentName()).put(input.getChildName(), l);
            } else {
                tree.inputs().get(input.getParentName()).get(input.getChildName()).add(event);
            }
        }
        tree.events().add(event);
    }
    public static boolean containsEvent(String root, String text) {

        return getTree().inputs().get(root).containsKey(text);
    }
    public static void setEvent(String root, String oldName, String newName) {
        Map<String, List<Event<?>>> rootMap = getTree().inputs().get(root);
        List<Event<?>> value = rootMap.get(oldName);
        rootMap.remove(oldName, value);
        rootMap.put(newName, value);
    }
    public static void setEvent(String oldRootName, String newRootName) {
        Map<String, List<Event<?>>> value = getTree().inputs().get(oldRootName);
        getTree().inputs().remove(oldRootName, value);
        getTree().inputs().put(newRootName, value);
    }

    public static boolean containsOnRoot(String root) {
        return getTree().inputs().containsKey(root);
    }

    public static void putNewOnRoot(String name) {
        getTree().inputs().put(name, new HashMap<>());
    }

    public static void putNewEvent(String root, String name) {
        getTree().inputs().get(root).put(name, new ArrayList<>());
    }*/

    public static void saveTree() {
        try {
            getTree().saveToFile();
        } catch (IOException e) {
            LogController.LOGGER.log(LogController.LogLevel.ERROR, "ファイルの書き込み中にエラーが発生しました。");
            e.printStackTrace(System.out);
        }
    }
}
