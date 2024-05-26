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
import java.awt.*;

public class Main {
    public static void main(String[] args) {
        Thread t = new Thread(() -> IoTHomeApplication.main(args));
        t.start();
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
