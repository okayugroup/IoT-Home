package com.okayugroup.IotHome;

import com.okayugroup.IotHome.event.Event;
import com.okayugroup.IotHome.event.EventController;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.Vector;

public class MainView {
    private JPanel formPanel;
    private JTextArea logArea;
    private JTabbedPane settingsPane;
    private JCheckBox useHtmlSettings;
    private JTree webTree;
    private JList<Event> list1;
    private JPanel eventsPane;
    private JTextArea eventDescp;
    private JTextField eventName;
    private JLabel eventType;
    private JComboBox comboBox1;
    private JEditorPane editorPane1;
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public static void main(String[] args) {
        Font font = loadFont(Objects.requireNonNull(MainView.class.getResource("/NotoSansJP-Medium.ttf")),11);
        setUIFont(new FontUIResource(font));

        JFrame frame = new JFrame("UserForm");
        MainView view = new MainView();

        new LogController(view);
        LogController.LOG.addLog("アプリが起動しました。");
        frame.setContentPane(view.formPanel);
        frame.setSize(600,400);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setVisible(true);
    }

    public void setLog(LogController.LogLevel level, String message) {
        var levelText = new StringBuilder(level.name());
        levelText.setLength(7);
        String builder = LocalDateTime.now().format(DATE_FORMAT) +
                " [" +
                levelText.toString().replace('\u0000', ' ') +
                "]: " +
                message +
                '\n';
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
    public static Font loadFont(URL path, float size) {
        try {
            Font font = Font.createFont(Font.TRUETYPE_FONT, new File(path.toURI()));
            return font.deriveFont(size);
        } catch (FontFormatException | IOException | URISyntaxException e) {
            throw new RuntimeException(e);
        }
    }


    private void createUIComponents() {
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("<ルート>");
        DefaultMutableTreeNode apiRoot = new DefaultMutableTreeNode("api");
        for (var row: EventController.getTree().entrySet()) {
            DefaultMutableTreeNode dir = new DefaultMutableTreeNode(row.getKey());
            for (var col: row.getValue().entrySet()) {
                dir.add(new DefaultMutableTreeNode(col.getKey()));
            }
            apiRoot.add(dir);
        }
        DefaultMutableTreeNode api = new DefaultMutableTreeNode("private");
        api.add(new DefaultMutableTreeNode("GET"));
        api.add(new DefaultMutableTreeNode("POST"));
        root.add(api);
        root.add(new DefaultMutableTreeNode("settings"));
        root.add(apiRoot);


        webTree = new JTree(root);
        webTree.addTreeSelectionListener(this::onTreeSelected);

        eventsPane = new JPanel(new BorderLayout());
    }

    private void onTreeSelected(TreeSelectionEvent e){
        TreePath treePath = e.getPath();
        Object[] path = treePath.getPath();
        if (path.length == 1){
            eventType.setText("ディレクトリ");
            eventName.setText("ルート");
            eventDescp.setText("REST APIのルートディレクトリです");
            eventName.setEditable(false); //親の名前が変更されることを防ぎます
        } else if (path[1].toString().equals("api")) {
            eventName.setEditable(true);
            eventName.setText(path[path.length - 1].toString());
            if (path.length == 2) {
                eventDescp.setText("イベントAPIのルートです。\n名前を編集することが出来ますが、編集した場合は再起動が必要になります。");
                return;
            }
            if (path.length < 4) {
                list1.setListData(new Vector<>());
                {
                    eventType.setText("ディレクトリ");
                    eventDescp.setText("表示するものがありません");
                }
                return;
            }
            eventType.setText("イベント");
            List<Event> events = EventController.getEvents(path[2].toString(), path[3].toString());
            list1.setListData(Objects.requireNonNull(events).toArray(Event[]::new));

        } else if (path.length == 2 && path[1].toString().equals("settings")) {
            eventType.setText("Webページ");
            eventName.setText("設定画面");
            eventDescp.setText("このページを無効化して保護するには [グローバル>HTMLの設定ページを有効化] のチェックボックスを外してください。");
            eventName.setEditable(false);
        } else if (path[1].toString().equals("private")) {
            eventName.setEditable(false);
            eventName.setText(path[path.length - 1].toString());
            String name = "内部で使用するAPI\n";

            if (path.length == 3) {
                eventType.setText("種別");
                switch (path[2].toString()) {
                    case "GET":
                        name += "設定のjsonファイルを取得します";
                        break;
                    case "POST":
                        name += "設定のjsonファイルを更新します";
                        break;
                    default:

                }
            } else eventType.setText("エンドポイント");

            eventDescp.setText(name + "\n注意：内部トークンを使用しているため通常の方法ではアクセスできません。");
        }

    }
}
