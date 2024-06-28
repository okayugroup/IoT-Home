package com.okayugroup.IotHome;

import com.okayugroup.IotHome.event.temporary.CommandEvent;
import com.okayugroup.IotHome.event.Event;
import com.okayugroup.IotHome.event.temporary.FileExecutionEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;

public class EventsPane extends JPanel {
    public static final Color INPUT = new Color(134, 209, 246);
    public static final Color OUTPUT = new Color(255, 10, 10);
    public static final Color TEMPORARY = new Color(85, 196, 89);
    public static final Color OPERATOR = new Color(233, 239, 71);
    public static final Color GRAY1 = new Color(200, 200, 200);
    private static final Color GRAY2 = new Color(120, 120, 120);
    private static final Font FONT = MainView.Font;
    private double startX = 0, startY = 0;
    private double translateX = 0, translateY = 0;

    public EventsPane() {
        super();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                startX = e.getX();
                startY = e.getY();
            }

        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                translateX += e.getX() - startX;
                translateY += e.getY() - startY;
                startX = e.getX();
                startY = e.getY();
                repaint();
            }
        });
    }
    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        // グラフィックスオブジェクトをGraphics2Dにキャスト
        Graphics2D g2d = (Graphics2D) g;
        g2d.clearRect(0, 0, getWidth(), getHeight());
        // 背景を塗りつぶす
        g2d.setColor(Color.BLACK);
        g2d.fillRect(0, 0, getWidth(), getHeight());


        // アンチエイリアスを有効にする
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(FONT.deriveFont(9f));

        // 入力ボタン
        g2d.setColor(INPUT);
        g2d.fillRect(0, 0, getWidth() / 8, 20);
        g2d.setColor(Color.BLACK);

        g2d.drawString("入力",  7, 15);

        // 出力ボタン
        g2d.setColor(OUTPUT);
        g2d.fillRect(getWidth() / 8, 0, getWidth() / 8, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawString("出力",  getWidth() / 8 + 7, 15);

        g2d.translate(translateX, translateY);
        // ウィンドウを描画
        drawWindow(g2d, 0, 80, 200,  120, new CommandEvent.PowershellCommand());
        drawWindow(g2d, 10, 40, 280,  140, new FileExecutionEvent.ExecuteFile());

    }

    private static void drawWindow(Graphics2D g2d, double x, double y, double width, double height, Event<?> event) {
        GeneralPath path = new GeneralPath();
        // パスを定義する
        path.moveTo(x, y + 20);    // 開始点
        path.lineTo(x + width, y + 20);
        path.lineTo(x + width, y + height - 10);
        path.quadTo(x + width, y + height, x + width - 10, y + height); // 二次ベジェ曲線
        path.lineTo(x + 10, y + height);
        path.quadTo(x, y + height, x, y + height - 10);
        path.closePath();       // パスを閉じる
        g2d.setColor(Color.WHITE);
        g2d.fill(path);

        path.reset();
        path.moveTo(x + width, y + 20);
        path.lineTo(x + width, y + 10);
        path.quadTo(x + width, y, x + width - 10, y);
        path.lineTo(x + width * .5, y);
        path.lineTo(x + width * .5, y + 20);

        g2d.setColor(GRAY2);
        g2d.fill(path);

        drawText(g2d, (float) (x + width * .5 + 10), (float) y + 15, width * .5 - 10, event.getChildName());

        path.reset();
        path.moveTo(x + width * .5, y + 20);
        path.lineTo(x + width * .5 + 10, y + 10);
        path.lineTo(x + width * .5, y);
        path.lineTo(x + width * .15, y);
        path.lineTo(x + width * .15, y + 20);

        g2d.setColor(GRAY1);
        g2d.fill(path);

        drawText(g2d, (float) (x + width * .15 + 10), (float) y + 15, width * .45 - 10, event.getParentName());

        path.reset();
        path.moveTo(x + width * .15, y + 20);
        path.lineTo(x + width * .15 + 10, y + 10);
        path.lineTo(x + width * .15, y);
        path.lineTo(x + 10, y);
        path.quadTo(x, y, x, y + 10);
        path.lineTo(x, y + 20);



        g2d.setColor(switch(event.getTypeId()) {
            case INPUT -> INPUT;
            case OUTPUT -> OUTPUT;
            case TEMPORARY -> TEMPORARY;
            case OPERATOR -> OPERATOR;
            default -> Color.WHITE;
        });
        g2d.fill(path);

        drawText(g2d, (float) x + 5, (float) y + 15, width * .2 - 10, switch(event.getTypeId()) {
            case INPUT -> "入力";
            case OUTPUT -> "出力";
            case TEMPORARY -> "中間";
            case OPERATOR -> "演算";
            default -> "不明";
        });
    }

    private static void drawText(Graphics2D g2d, float x, float y, double width, String text) {
        g2d.setColor(Color.BLACK);
        g2d.drawString(getMaximumText(g2d.getFontMetrics(), text, width), x, y);
    }

    public static String getMaximumText(FontMetrics font, String text, double width) {
        double bounds = font.stringWidth(text);
        double bTextWidth = font.stringWidth("...");
        if (bounds <= width) {
            return text;
        }
        for (int i = text.length() - 2; i >= 0; i--) {
            String result = text.substring(0, i);
            if (font.stringWidth(result) + bTextWidth <= width) {
                return result + "...";
            }
        }
        return "...";
    }
}