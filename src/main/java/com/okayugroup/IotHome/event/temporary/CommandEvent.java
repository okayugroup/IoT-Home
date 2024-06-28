package com.okayugroup.IotHome.event.temporary;

import com.okayugroup.IotHome.LogController;
import com.okayugroup.IotHome.event.EventResult;
import com.okayugroup.IotHome.event.TemporaryEvent;
import org.jetbrains.annotations.Nullable;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

public abstract class CommandEvent extends TemporaryEvent<List<String>> {
    protected CommandEvent(String name, String... args) {
        super("コマンド実行イベント", name, args);
    }

    @Nullable
    protected String command;

    @Override
    public CommandEvent setArgs(@Nullable String... args) {
        command = args != null && args.length > 0 ? args[0] : null;
        return this;
    }

    @Override
    public String[] getArgs() {
        return new String[]{command};
    }

    EventResult<List<String>> execute(@Nullable EventResult<?> previousResult, int typeIndex)  {
        if (command == null) return new EventResult<>(null, List.of());
        try {

            String command = previousResult == null || previousResult.result() instanceof String[] ? this.command : this.command.formatted(previousResult.result());
            Process process = Runtime.getRuntime().exec(switch (typeIndex) {
                case 1 -> "powershell.exe /c " + command;
                case 2 -> "cmd.exe /c " + command;
                default -> command;
            });
            process.waitFor();
            Charset charset = typeIndex == 0 ? StandardCharsets.UTF_8 : Charset.forName("MS932");
            BufferedReader stream = new BufferedReader(new InputStreamReader(process.getInputStream(), charset));
            String line;
            List<String> result = new ArrayList<>();
            while ((line = stream.readLine()) != null) {
                result.add(line);
            }
            return new EventResult<>(null, result);
        } catch (IOException | InterruptedException e) {
            LogController.LOGGER.log("Exception occurred while executing command " + command + "\n" + e.getMessage());
            return new EventResult<>(e, List.of());
        }
    }

    @Override
    public abstract CommandEvent getCopy();

    public static class ConsoleCommand extends CommandEvent {
        public ConsoleCommand(String... args) {
            super("コンソール コマンド", args);
        }

        @Override
        public ConsoleCommand getCopy() {
            return new ConsoleCommand(getArgs());
        }

        @Override
        public EventResult<List<String>> execute(@Nullable EventResult<?> previousResult) {
            return execute(previousResult, 0);
        }
    }
    public static class PowershellCommand extends CommandEvent {

        public PowershellCommand(String... args) {
            super("Windows PowerShell コマンド", args);
        }

        @Override
        public PowershellCommand getCopy() {
            return new PowershellCommand(getArgs());
        }

        @Override
        public EventResult<List<String>> execute(@Nullable EventResult<?> previousResult) {
            return execute(previousResult, 1);
        }
    }
    public static class CommandPromptCommand extends CommandEvent {
       public CommandPromptCommand(String... args) {
           super("Windows コマンドプロンプト コマンド", args);
       }

       @Override
        public CommandPromptCommand getCopy() {
           return new CommandPromptCommand(getArgs());
       }

        @Override
        public EventResult<List<String>> execute(@Nullable EventResult<?> previousResult) {
           return execute(previousResult, 2);
        }
    }
}
