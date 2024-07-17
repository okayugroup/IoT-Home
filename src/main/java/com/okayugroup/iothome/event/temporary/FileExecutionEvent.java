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

package com.okayugroup.iothome.event.temporary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.okayugroup.iothome.LogController;
import com.okayugroup.iothome.event.EventResult;
import com.okayugroup.iothome.event.TemporaryEvent;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class FileExecutionEvent extends TemporaryEvent<List<String>> {
    protected FileExecutionEvent(String name, String... args) {
        super("ファイル実行", name, args);
    }
    protected String filePath;
    protected String directory = null;
    @Override
    public abstract FileExecutionEvent getCopy();
    @Override
    public void setArgs(String... args) {
        filePath = args.length > 0 ? args[0] : "";
        directory = args.length > 1 ? args[1] : null;
    }

    @Override
    public @NotNull String @NotNull [] getArgs() {
        return directory == null ? new String[]{filePath} : new String[]{filePath, directory};
    }
    @Override
    public String getReturns() {
        return "ファイル実行結果\nリスト[文字列]";
    }
    public static class ExecuteFile extends FileExecutionEvent {
        public ExecuteFile(String... args) {
            super("ファイルを実行", args);
        }

        @Override
        public ExecuteFile getCopy() {
            return new ExecuteFile(getArgs());
        }

        @Override
        public EventResult<List<String>> execute(@Nullable EventResult<?> previousResult) {
            try {
                // ProcessBuilderを使用してコマンドを設定
                ProcessBuilder builder = new ProcessBuilder(filePath);
                if (directory != null && !directory.isEmpty()) builder.directory(new java.io.File(directory)); // 実行するディレクトリを設定（オプション）

                // プロセスを開始
                Process process = builder.start();
                // プロセスの出力を取得する
                BufferedReader reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
                List<String> stringList = new ArrayList<>();
                String line;
                while ((line = reader.readLine()) != null) {
                    stringList.add(line);
                }

                return new EventResult<>(null, stringList);
            } catch (Exception e) {
                LogController.LOGGER.log(LogController.LogLevel.ERROR, "ファイル実行中の例外 " + filePath + "\n" + e.getMessage());
                return new EventResult<>(e, List.of());
            }


        }
    }
    public static class PlaySound extends FileExecutionEvent {
        public PlaySound(String... args) {
            super("音声を再生", args);
        }

        @Override
        public EventResult<List<String>> execute(@Nullable EventResult<?> previousResult) {
            try {
                // 入力ストリームを作成
                InputStream is = new FileInputStream(filePath);

                // プレーヤーを作成して再生開始
                Player player = new Player(is);
                player.play();
                return new EventResult<>(null, List.of());
            } catch (IOException | JavaLayerException e) {
                LogController.LOGGER.log(LogController.LogLevel.ERROR, "音声ファイル再生中の例外 " + filePath + "\n" + e.getMessage());
                return new EventResult<>(e, List.of());
            }
        }
        @Override
        public String getReturns() {
            return "ファイル実行結果\nリスト[文字列](空)";
        }
        @Override
        public FileExecutionEvent getCopy() {
            return new PlaySound(getArgs());
        }
    }
}
