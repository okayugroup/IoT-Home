package com.okayugroup.IotHome;

import com.okayugroup.IotHome.event.*;
import com.okayugroup.IotHome.event.Event;
import jakarta.annotation.Nullable;

import javax.swing.*;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;

public class MainView {
    public static final Event[] EVENTS = {CommandEvent.ConsoleCommand(), FileExecutionEvent.ExecuteFile()};
    private JPanel formPanel;
    private JTextArea logArea;
    private JTabbedPane settingsPane;
    private JCheckBox useHtmlSettings;
    private JTree webTree;
    private JList<Event> eventList;
    private JPanel eventsPane;
    private JTextArea eventDescription;
    private JTextField eventName;
    private JLabel eventType;
    private JComboBox<Event> availableEvents;
    private JEditorPane args;
    private JButton addEvent;
    private JButton removeEvent;
    private JComboBox<EventTemplate> availableEventTypes;
    public static DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    private List<Event> selectedEvents = null;
    public static void main(String[] args) {
        Font font = loadFont();
        setUIFont(new FontUIResource(font.deriveFont(10f)));

        JFrame frame = new JFrame("UserForm");
        MainView view = new MainView();
        view.initComponents();
        new LogController(view);
        LogController.LOGGER.log("アプリが起動しました。");
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
    private static Font loadFont() {
        try {
            // フォントファイルをストリームとして読み込む
            InputStream inputStream = MainView.class.getResourceAsStream("/NotoSansJP-Medium.ttf");
            if (inputStream == null) {
                throw new IOException("Font file not found: " + "/NotoSansJP-Medium.ttf");
            }

            // フォントを作成
            return Font.createFont(Font.TRUETYPE_FONT, inputStream);
        } catch (FontFormatException | IOException e) {
            LogController.LOGGER.log("フォントが読み込めませんでした。" + e.getMessage());
            // デフォルトのフォントを返すか、例外を処理する
            return new JLabel().getFont(); // デフォルトのフォントを返す
        }
    }
    @Nullable
    private Event createEvent() {
        Event e = (Event)availableEvents.getSelectedItem();
        if (e == null) return null;
        return e.getCopy(availableEventTypes.getSelectedIndex()).setArgs(args.getText().split("\n"));
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
    private void initComponents() {
        // 選択可能なイベントを初期化
        DefaultComboBoxModel<Event> events = new DefaultComboBoxModel<>(EVENTS);
        availableEvents.setModel(events);
        availableEvents.setRenderer(new ItemRenderer());
        availableEvents.addItemListener(e -> availableEventTypes.setModel(new DefaultComboBoxModel<>(((Event)e.getItem()).getTemplates())));
        availableEventTypes.setModel(new DefaultComboBoxModel<>(EVENTS[0].getTemplates()));

        addEvent.addActionListener(e -> {
            if (selectedEvents != null) {
                selectedEvents.add(createEvent());
                eventList.setListData(Objects.requireNonNull(selectedEvents).toArray(Event[]::new));
            }
        });
    }
    private void onTreeSelected(TreeSelectionEvent e){
        System.out.println(availableEvents.getModel());
        TreePath treePath = e.getPath();
        Object[] path = treePath.getPath();
        eventsPane.setVisible(false);
        if (path.length == 1){
            eventType.setText("ディレクトリ");
            eventName.setText("ルート");
            eventDescription.setText("すべてのAPIのルートディレクトリです");
            eventName.setEditable(false); //親の名前が変更されることを防ぎます
        } else if (path[1].toString().equals("api")) { // root
            eventName.setEditable(true);
            eventName.setText(path[path.length - 1].toString());
            if (path.length == 2) {
                eventDescription.setText("イベントAPIのルートです。\n名前を編集することが出来ますが、編集した場合は再起動が必要になります。");
                return;
            }
            if (path.length < 4) {
                {
                    eventType.setText("ディレクトリ");
                    eventDescription.setText("表示するものがありません");
                }
                return;
            }

            eventType.setText("イベント");
            eventDescription.setText("イベントのグループです。編集、順序の変更、追加、削除が可能です。");
            selectedEvents = EventController.getEvents(path[2].toString(), path[3].toString());
            eventList.setListData(Objects.requireNonNull(selectedEvents).toArray(Event[]::new));
            eventsPane.setVisible(true);
        } else if (path.length == 2 && path[1].toString().equals("settings")) {
            eventType.setText("Webページ");
            eventName.setText("設定画面");
            eventDescription.setText("このページを無効化して保護するには [グローバル>HTMLの設定ページを有効化] のチェックボックスを外してください。");
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

            eventDescription.setText(name + "\n注意：内部トークンを使用しているため通常の方法ではアクセスできません。");
        }

    }
}
