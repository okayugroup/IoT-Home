/*
 * This file is part of Iot-Home.
 *
 * Iot-Home is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Lesser General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * Iot-Home is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public License
 * along with Iot-Home. If not, see <https://www.gnu.org/licenses/>.
 *
 * Copyright (C) 2024 OkayuGroup
 */

package com.okayugroup.IotHome.event;

import com.okayugroup.IotHome.event.input.RequestEvent;
import com.okayugroup.IotHome.event.temporary.WebRequestEvent;
import com.okayugroup.IotHome.event.temporary.CommandEvent;
import com.okayugroup.IotHome.event.temporary.FileExecutionEvent;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.util.*;

public class EventController {
    private static UserEventsObject tree = null;

    public static final List<Map<String, List<String>>> MENU = initMenu();

    private static List<Map<String, List<String>>> initMenu() {
        Map<String, List<String>> a = new LinkedHashMap<>();
        a.put("インターネット", List.of(Event.getTypicalName(RequestEvent.class)));
        Map<String, List<String>> b = new LinkedHashMap<>();
        b.put("コンソール", Event.getTypicalName(CommandEvent.ConsoleCommand.class, CommandEvent.PowershellCommand.class, CommandEvent.CmdPromptCommand.class));
        b.put("ファイル実行", Event.getTypicalName(FileExecutionEvent.ExecuteFile.class, FileExecutionEvent.PlaySound.class));
        b.put("ウェブリクエスト", Event.getTypicalName(WebRequestEvent.GetRequest.class, WebRequestEvent.PostRequest.class));
        return List.of(a, b);
    }

    public static final Map<String, TemplatedEvent> EVENT_DICT = initEvents();

    private static @NotNull Map<String, TemplatedEvent> initEvents() {
        Map<String, TemplatedEvent> events = new LinkedHashMap<>();
        add(events, new CommandEvent.ConsoleCommand(),    "実行するコマンド");
        add(events, new CommandEvent.CmdPromptCommand(),  "実行するWindowsコマンド");
        add(events, new CommandEvent.PowershellCommand(), "実行するPowerShellコマンド");
        add(events, new FileExecutionEvent.ExecuteFile(), "実行するファイル", "実行元のディレクトリ");
        add(events, new FileExecutionEvent.PlaySound(),   "再生する音声ファイル (*.wav,*.mp3)");
        add(events, new WebRequestEvent.GetRequest(),     "GETするURL", "タイムアウト時間(ミリ秒)", "ヘッダー(JSON形式)");
        add(events, new WebRequestEvent.PostRequest(),    "POSTするURL", "タイムアウト時間(ミリ秒)", "ヘッダー(JSON形式)", "送信するコンテンツ");
        add(events, new RequestEvent(),                   "エンドポイント子名", "エンドポイント親名");

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
            resetTree();

        }
        return tree;
    }
    public static void resetTree() {
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

    public static void setEventDict(LinkedEvent linkedEvent) {
        if (linkedEvent.getEvent() instanceof RequestEvent event) {
            Map<String, Map<String, LinkedEvent>> inputs = tree.inputs();
            for (Map<String, LinkedEvent> value : inputs.values()) {
                for (Map.Entry<String, LinkedEvent> stringLinkedEventEntry : value.entrySet()) {
                    if (stringLinkedEventEntry.getValue() == linkedEvent) {
                        value.remove(stringLinkedEventEntry.getKey());
                    }
                }
            }
            if (inputs.containsKey(event.category)){
                inputs.get(event.category).put(event.field, linkedEvent);
            } else{
                HashMap<String, LinkedEvent> map = new HashMap<>();
                map.put(event.field, linkedEvent);
                inputs.put(event.category, map);
            }
        }
    }
}
