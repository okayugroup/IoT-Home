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

package com.okayugroup.IotHome;

import javax.swing.*;
import javax.swing.plaf.FontUIResource;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class MainView {
    private JPanel formPanel;
    private JTextArea logArea;
    private JCheckBox useHtmlSettings;
    public static Font Font = loadFont();
    private JPanel eventsPane;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    public static void main(String[] args) {

        setUIFont(new FontUIResource(Font.deriveFont(10f)));

        JFrame frame = new JFrame("IoT-Home");
        MainView view = new MainView();
        view.initComponents();
        new LogController(view);
        LogController.LOGGER.log("アプリが起動しました。");
        frame.setContentPane(view.formPanel);
        frame.setSize(1200,800);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setLog(LogController.LogLevel level, String message) {
        String builder = LocalDateTime.now().format(DATE_FORMAT) +
                " [" +
                level.name() +
                "]: " +
                message +
                "\n";
        logArea.append(builder);
    }
    // UIManagerを使用してアプリケーション全体のフォントを設定するメソッド
    public static void setUIFont(javax.swing.plaf.FontUIResource f) {
        java.util.Enumeration<Object> keys = UIManager.getDefaults().keys();
        while (keys.hasMoreElements()) {
            Object key = keys.nextElement();
            Object value = UIManager.get(key);
            if (value instanceof javax.swing.plaf.FontUIResource)
                UIManager.put(key, f);
        }
    }
    private static Font loadFont() {
        try {
            // フォントファイルをストリームとして読み込む
            InputStream inputStream = MainView.class.getResourceAsStream("/NotoSansJP-Medium.ttf");
            if (inputStream == null) {
                throw new IOException("Font file not found: " + "/NotoSansJP-Medium.ttf");
            }

            // フォントを作成
            return java.awt.Font.createFont(java.awt.Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e) {
            LogController.LOGGER.log("フォントが読み込めませんでした。" + e.getMessage());
            // デフォルトのフォントを返すか、例外を処理する
            return new JLabel().getFont(); // デフォルトのフォントを返す
        }
    }

    private void createUIComponents() {
        eventsPane = new EventsPane();

    }
    private void initComponents() {
        useHtmlSettings.addActionListener(e->LogController.LOGGER.log("この機能はまだ実装されていません"));
    }
}
