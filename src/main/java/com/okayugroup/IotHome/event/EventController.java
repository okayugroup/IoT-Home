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
    public static final Map<String, TemplatedEvent> EVENT_DICT = initEvents();

    private static @NotNull Map<String, TemplatedEvent> initEvents() {
        Map<String, TemplatedEvent> events = new LinkedHashMap<>();
        add(events, new CommandEvent.ConsoleCommand(), "実行するコマンド");
        add(events, new CommandEvent.CmdPromptCommand(), "実行するWindowsコマンド");
        add(events, new CommandEvent.PowershellCommand(), "実行するPowerShellコマンド");
        add(events, new FileExecutionEvent.ExecuteFile(), "実行するファイル", "実行元のディレクトリ");
        add(events, new FileExecutionEvent.PlaySound(), "再生する音声ファイル (*.wav,*.mp3)");
        return Map.copyOf(events);
    }
    public static TemplatedEvent getTemplate(Event<?> event) {
        return EVENT_DICT.get(event.getTypicalName());
    }
    private static void add(Map<String, TemplatedEvent> events, Event<?> event, String... argDescriptions) {
        events.put(event.getTypicalName(), new TemplatedEvent(event, argDescriptions));
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
            tree.events().add(new LinkedEvent(new CommandEvent.CmdPromptCommand("dir"), 0, 50, 200, 100));
            tree.events().add(new LinkedEvent(new FileExecutionEvent.PlaySound("C:\\Users\\yaido\\Music\\ミンミンゼミが鳴く雑木林.mp3"), 100, 70, 200, 100));
            tree.events().add(new LinkedEvent(new FileExecutionEvent.ExecuteFile("notepad.exe"), 120, 90, 250, 120));
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
