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

package com.okayugroup.IotHome;

import javax.swing.*;
import java.awt.*;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(() -> IoTHomeApplication.main(args));
        t.start();
        if (!Arrays.asList(args).contains("--nogui")) {
            try {
                SwingUtilities.invokeLater(() -> MainView.main(args));
            } catch (HeadlessException e) {
                System.err.println("""
                        ハードウェアがGUIに対応していないようです。
                        このアプリはGUIが無くても動きますが、設定の方法が難しくなります。
                        
                        このエラーが予期したものではない場合はGitHubで報告してください。
                        GUIのない環境で実行している場合は、アプリ実行時に生成されたevents.jsonを編集することで設定ができます。
                        """);
            }
        }
    }
}
