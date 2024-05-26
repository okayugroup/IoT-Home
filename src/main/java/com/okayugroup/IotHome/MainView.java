package com.okayugroup.IotHome;

import com.okayugroup.IotHome.event.*;
import com.okayugroup.IotHome.event.Event;
import jakarta.annotation.Nullable;
import org.apache.catalina.mbeans.SparseUserDatabaseMBean;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import javax.swing.event.TreeSelectionEvent;
import javax.swing.plaf.FontUIResource;
import javax.swing.tree.*;
import java.awt.*;
import java.io.IOException;
import java.io.InputStream;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class MainView {
    public static final Event[] EVENTS = EventController.EVENT_DICT.values().toArray(new Event[0]);
    private JPanel formPanel;
    private JTextArea logArea;
    private JCheckBox useHtmlSettings;
    private JTree webTree;
    private JList<Event> eventList;
    private JPanel eventsPane;
    private JTextArea eventDescription;
    private JTextField eventName;
    private JLabel eventType;
    private JComboBox<Event> availableEvents;
    private JTextArea args;
    private JButton addEvent;
    private JButton removeEvent;
    private JComboBox<EventTemplate> availableEventTypes;
    private JButton testEvents;
    private JButton save;
    private JButton newDirectory;
    public static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private int selectedEventIndex = -1;
    private boolean eventHandleable = true;
    private String root;
    private String name;
    private List<Event> selectedEvents = null;
    private boolean modifiable = false;
    private boolean isEventRoot = false;
    private static final String webRootName = EventController.getTree().rootName();
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
        DefaultMutableTreeNode apiRoot = new DefaultMutableTreeNode(webRootName);
        for (var row: EventController.getTree().events().entrySet()) {
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
                updateEvents();
            }
        });
        removeEvent.addActionListener(e -> {
            if (selectedEvents != null) {
                selectedEvents.remove(eventList.getSelectedIndex());
                updateEvents();
            }
        });
        testEvents.addActionListener(e -> {
            if (selectedEvents != null) {
                Thread t = new Thread(() -> EventController.execute(selectedEvents));
                t.start();
            }
        });
        eventList.addListSelectionListener(e -> {
            if (eventList.getSelectedIndex() < 0) return;
            eventHandleable = false;
            selectedEventIndex = eventList.getSelectedIndex();
            var value = Arrays.stream(EVENTS).filter(i -> i.name.equals(selectedEvents.get(selectedEventIndex).name)).findAny();
            value.ifPresent(event -> availableEvents.setSelectedItem(event));
            availableEventTypes.setSelectedItem(selectedEvents.get(selectedEventIndex).getType());
            eventHandleable = true;
            args.setText(String.join("\n", selectedEvents.get(selectedEventIndex).getArgs()));
        });

        availableEventTypes.addItemListener(e -> {
            if (eventHandleable && selectedEventIndex >= 0) {
                selectedEvents.set(selectedEventIndex, selectedEvents.get(selectedEventIndex).getCopy(availableEventTypes.getSelectedIndex()));
                updateEvents();
            }
        });
        availableEvents.addItemListener(e -> {
            if (eventHandleable && selectedEventIndex >= 0 && availableEvents.getSelectedItem() != null) {
                selectedEvents.set(selectedEventIndex, ((Event)availableEvents.getSelectedItem()).getCopy(0));
                updateEvents();
            }
        });
        eventName.addActionListener(e -> {
            if (eventHandleable) {
                String text = eventName.getText();
                if (root == null) {
                    if (text.isEmpty() || text.isBlank() || text.equals("private") || text.equals("settings")) {
                        eventName.setForeground(Color.RED);
                        LogController.LOGGER.log(LogController.LogLevel.ERROR, "\"private\", \"settings\", 空白・スペースは使用できません。");
                    } else {
                        eventName.setForeground(Color.BLACK);
                        //webRootName = text; // NOTICE: 名前変更するならここも実装しましょう
                        DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) webTree.getLastSelectedPathComponent();
                        selectedNode.setUserObject(text);
                        ((DefaultTreeModel) webTree.getModel()).nodeChanged(selectedNode);

                    }
                } else if (text.isEmpty() || text.isBlank() || (name == null ? EventController.containsOnRoot(root) : EventController.containsEvent(root, text))) {
                    eventName.setForeground(Color.RED);
                    Toolkit.getDefaultToolkit().beep(); // 操作が無効の音を鳴らす
                    LogController.LOGGER.log(LogController.LogLevel.ERROR, "同じ名前、重複する名前、空白・スペースは使用できません。");
                } else {
                    eventName.setForeground(Color.BLACK);
                    DefaultMutableTreeNode selectedNode = (DefaultMutableTreeNode) webTree.getLastSelectedPathComponent();
                    selectedNode.setUserObject(text);
                    ((DefaultTreeModel) webTree.getModel()).nodeChanged(selectedNode);
                    if (name == null)
                        EventController.setEvent(root, text);
                    else
                        EventController.setEvent(root, name, text);
                }
            }
        });
        args.getDocument().addDocumentListener(new DocumentListener() {
            public void update() {
                if (eventHandleable && selectedEventIndex >= 0 && availableEvents.getSelectedItem() != null) {
                    selectedEvents.get(selectedEventIndex).setArgs(args.getText().split("\n"));
                }
            }
            @Override
            public void insertUpdate(DocumentEvent e) { update(); }
            @Override
            public void removeUpdate(DocumentEvent e) { update(); }
            @Override
            public void changedUpdate(DocumentEvent e) {}
        });
        save.addActionListener(e -> EventController.saveTree());
        newDirectory.addActionListener(e -> {
            if (modifiable) {
                String name;
                if (isEventRoot) {
                    name = "directory";
                    int i = 0;
                    do {
                        i++;
                    } while (EventController.containsOnRoot(name + i));
                    name += i;
                } else {
                    name = "event";
                    int i = 0;
                    do {
                        i++;
                    } while (EventController.containsEvent(root, name + i));
                    name += i;
                }
                DefaultMutableTreeNode root = ((DefaultMutableTreeNode) webTree.getLastSelectedPathComponent());
                DefaultMutableTreeNode newValue = new DefaultMutableTreeNode(name);
                root.add(newValue);
                ((DefaultTreeModel) webTree.getModel()).reload();
                if (isEventRoot) {
                    EventController.putNewOnRoot(name);
                } else {
                    EventController.putNewEvent(this.root, name);
                }
            }
        });
    }

    private void updateEvents() {
        eventList.setListData(Objects.requireNonNull(selectedEvents).toArray(Event[]::new));
        eventList.setSelectedIndex(selectedEventIndex);
    }

    private void onTreeSelected(TreeSelectionEvent e){
        modifiable = false;
        newDirectory.setEnabled(false);
        TreePath treePath = e.getPath();
        Object[] path = treePath.getPath();
        eventsPane.setVisible(false);
        if (path.length == 1){
            eventType.setText("ディレクトリ");
            eventName.setText("ルート");
            eventDescription.setText("すべてのAPIのルートディレクトリです");
            eventName.setEditable(false); //親の名前が変更されることを防ぎます
        } else if (path[1].toString().equals(webRootName)) { // root
            eventName.setEditable(false);  // TODO: 編集できるようにするべきである。
            eventName.setText(path[path.length - 1].toString());
            isEventRoot = path.length == 2;
            root = null;
            name = null;

            if (path.length == 2) {
                modifiable = true;
                newDirectory.setEnabled(true);
                eventType.setText("ディレクトリ");
                eventDescription.setText("""
                        イベントAPIのルートです。
                        名前は編集できません。


                        実は名前を編集することが出来るようにするつもりでした。
                        難しそうです...。
                        今後実装するかも
                        """);
                return;
            }
            if (path.length < 4) {
                modifiable = true;
                newDirectory.setEnabled(true);
                eventName.setEditable(true);
                eventType.setText("ディレクトリ");
                eventDescription.setText("表示するものがありません");
                root = path[2].toString();
                name = null;
                return;
            }

            eventType.setText("イベント");
            eventDescription.setText("イベントのグループです。編集、追加、削除が可能です。");
            selectedEvents = EventController.getEvents(root = path[2].toString(), name = path[3].toString());

            updateEvents();
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
