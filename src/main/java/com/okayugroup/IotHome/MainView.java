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
        DefaultMutableTreeNode root = new DefaultMutableTreeNode("webroot");
        root.add(new DefaultMutableTreeNode("settings"));
        for (var row: EventController.getTree().entrySet()) {
            DefaultMutableTreeNode dir = new DefaultMutableTreeNode(row.getKey());
            for (var col: row.getValue().entrySet()) {
                dir.add(new DefaultMutableTreeNode(col.getKey()));
            }
            root.add(dir);
        }
        webTree = new JTree(root);
        webTree.addTreeSelectionListener(this::onTreeSelected);

        eventsPane = new JPanel(new BorderLayout());
    }

    private void onTreeSelected(TreeSelectionEvent e){
        TreePath treePath = e.getPath();
        Object[] path = treePath.getPath();
        eventName.setEditable(true);
        eventName.setText(path[path.length - 1].toString());
        if (treePath.getPathCount() < 3) {
            list1.setListData(new Vector<>());
            if (path.length == 2 && path[1].toString().equals("settings")) {
                eventType.setText("Webページ");
                eventName.setText("設定画面");
                eventDescp.setText("このページを無効化して保護するには [グローバル>HTMLの設定ページを有効化] のチェックボックスを外してください。");
                eventName.setEditable(false);
            } else {
                eventType.setText("ディレクトリ");
                eventDescp.setText("表示するものがありません");
            }
            return;
        }
        List<Event> events = EventController.getEvents(path[1].toString(), path[2].toString());
        list1.setListData(Objects.requireNonNull(events).toArray(Event[]::new));
    }
}
