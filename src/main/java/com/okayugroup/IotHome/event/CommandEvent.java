package com.okayugroup.IotHome.event;

import com.okayugroup.IotHome.LogController;
import jakarta.annotation.Nullable;
import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

public class CommandEvent extends Event {
    private CommandEvent(int type, String... args) {
        super("コマンド実行イベント", type, args);
    }
    @Contract(" -> new")
    public static @NotNull CommandEvent ConsoleCommand() {
        return new CommandEvent(0);
    }
    public static CommandEvent PowershellCommand() {
        return new CommandEvent(1);
    }
    public static CommandEvent CmdPromptCommand() {
        return new CommandEvent(2);
    }
    @Override
    protected EventTemplate[] initializeEvents() {
        return new EventTemplate[]{
                new EventTemplate("コンソール コマンド", 1, "実行するコマンド"),
                new EventTemplate("Windows Powershell コマンド", 1, "実行するコマンド"),
                new EventTemplate("Windows コマンド", 1, "実行するコマンド"),
        };
    }

    @Override
    public CommandEvent getCopy(int typeIndex) {
        return new CommandEvent(typeIndex);
    }

    @Nullable
    protected String command;

    @Override
    public CommandEvent setArgs(@Nullable String... args) {
        command = args != null && args.length > 0 ? args[0] : null;
        return this;
    }

    @Override
    public String getArgs() {
        return command;
    }

    @Override
    public EventResult execute(EventResult previousResult)  {
        if (command == null) return EventResult.ERROR;
        try {
            Process process = Runtime.getRuntime().exec(switch (typeIndex) {
                case 1 -> "powershell.exe /c " + command;
                case 2 -> "cmd.exe /c " + command;
                default -> command;
            });
            process.waitFor();
            BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream()));
            String line;
            List<String> result = new ArrayList<>();
            while ((line = stream.readLine()) != null) {
                result.add(line);
            }
            return new EventResult(getType(), process.exitValue(), result);
        } catch (IOException | InterruptedException e) {
            LogController.LOGGER.log("Exception occurred while executing command " + command + "\n" + e.getMessage());
            return EventResult.ERROR;
        }

    }
}
