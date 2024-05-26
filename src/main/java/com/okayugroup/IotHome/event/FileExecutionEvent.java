package com.okayugroup.IotHome.event;

import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import com.okayugroup.IotHome.LogController;
import javazoom.jl.decoder.JavaLayerException;
import javazoom.jl.player.Player;
import org.jetbrains.annotations.Nullable;

public class FileExecutionEvent extends Event {
    protected FileExecutionEvent(int type, String... args) {
        super("ファイル実行イベント", type, args);
    }
    public static FileExecutionEvent ExecuteFile() {
        return new FileExecutionEvent(0);
    }
    public static FileExecutionEvent PlaySound() {
        return new FileExecutionEvent(1);
    }
    private static final EventTemplate[] templates = new EventTemplate[]{
            new EventTemplate("ファイルを実行", 1, "ファイルパス", "実行するディレクトリ(オプション)"),
            new EventTemplate("音声を再生", 1, "ファイルパス")
    };
    private String filePath;
    private String directory = null;
    @Override
    protected EventTemplate[] initializeEvents() {
        return templates;
    }

    @Override
    public FileExecutionEvent getCopy(int typeIndex) {
        return new FileExecutionEvent(typeIndex);
    }

    @Override
    public FileExecutionEvent setArgs(String... args) {
        filePath = args.length > 0 ? args[0] : null;
        directory = args.length > 1 ? args[1] : null;
        return this;
    }

    @Override
    public String[] getArgs() {
        return directory == null ? new String[]{filePath} : new String[]{filePath, directory};
    }

    @Override
    public EventResult execute(@Nullable EventResult previousResult) {
        if (filePath == null) return new EventResult(getType(), -1, List.of());
        String filePath = previousResult == null ? this.filePath : this.filePath.formatted(previousResult.result().toArray());
        switch (typeIndex) {
            case 0:
            default:
                try {
                    // ProcessBuilderを使用してコマンドを設定
                    ProcessBuilder builder = new ProcessBuilder(filePath);
                    if (directory != null) builder.directory(new java.io.File(directory)); // 実行するディレクトリを設定（オプション）

                    // プロセスを開始
                    Process process = builder.start();

                    // プロセスの終了を待機
                    int exitCode = process.waitFor();
                    return new EventResult(getType(), exitCode, List.of());
                } catch (Exception e) {
                    LogController.LOGGER.log(LogController.LogLevel.ERROR, "ファイル実行中の例外 " + filePath + "\n" + e.getMessage());
                    return EventResult.ERROR;
                }
            case 1:
                try {
                    // 入力ストリームを作成
                    InputStream is = new FileInputStream(filePath);

                    // プレーヤーを作成して再生開始
                    Player player = new Player(is);
                    player.play();
                    return new EventResult(getType(), 0, List.of());
                } catch (IOException | JavaLayerException e) {
                    LogController.LOGGER.log(LogController.LogLevel.ERROR, "音声ファイル再生中の例外 " + filePath + "\n" + e.getMessage());
                    return EventResult.ERROR;
                }
        }

    }
}
