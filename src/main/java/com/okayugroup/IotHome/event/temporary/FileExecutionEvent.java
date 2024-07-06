/*
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.okayugroup.IotHome.event.temporary;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

import com.okayugroup.IotHome.LogController;
import com.okayugroup.IotHome.event.EventResult;
import com.okayugroup.IotHome.event.TemporaryEvent;
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
    public FileExecutionEvent setArgs(String... args) {
        filePath = args.length > 0 ? args[0] : null;
        directory = args.length > 1 ? args[1] : null;
        return this;
    }

    @Override
    public @NotNull String @NotNull [] getArgs() {
        return directory == null ? new String[]{filePath} : new String[]{filePath, directory};
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
        public FileExecutionEvent getCopy() {
            return new PlaySound(getArgs());
        }
    }
}
