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

import com.okayugroup.IotHome.event.Event;
import com.okayugroup.IotHome.event.LinkedEvent;
import com.okayugroup.IotHome.event.UserEventsObject;
import com.okayugroup.IotHome.event.input.RequestEvent;

import javax.swing.*;
import java.awt.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseMotionAdapter;
import java.awt.geom.GeneralPath;
import java.util.List;

public class EventsPane extends JPanel {
    public static final Color INPUT = new Color(134, 209, 246);
    public static final Color OUTPUT = new Color(255, 10, 10);
    public static final Color TEMPORARY = new Color(215, 215, 215);
    public static final Color OPERATOR = new Color(233, 239, 71);
    public static final Color GRAY1 = new Color(171, 171, 171);
    private static final Color GRAY2 = new Color(120, 120, 120);
    private static final Font FONT = MainView.Font;
    private double startX = 0, startY = 0;
    private double translateX = 0, translateY = 0;
    private LinkedEvent selectedNode;
    private int selectedDirection;
    private boolean draggable;
    private UserEventsObject userEventsObject;

    public EventsPane() {
        super();
        addMouseListener(new MouseAdapter() {
            @Override
            public void mousePressed(MouseEvent e) {
                super.mousePressed(e);
                startX = e.getX();
                startY = e.getY();
                selectedDirection = getSelectedNode(e);
                draggable = selectedDirection == -1;
                if (selectedNode != null) {
                    userEventsObject.events().remove(selectedNode);
                    userEventsObject.events().add(0, selectedNode);
                    repaint();
                }
            }
            @Override
            public void mouseReleased(MouseEvent e) {
                super.mouseReleased(e);
                selectedDirection = getSelectedNode(e);
                extracted(e);
            }
        });
        addMouseMotionListener(new MouseMotionAdapter() {
            @Override
            public void mouseDragged(MouseEvent e) {
                super.mouseDragged(e);
                if (draggable) {
                    setCursor(Cursor.getPredefinedCursor(Cursor.MOVE_CURSOR));
                    translateX += e.getX() - startX;
                    translateY += e.getY() - startY;
                    repaint();
                } else {
                    if (selectedDirection == 7 || selectedDirection == 0 || selectedDirection == 1) {
                        double p = selectedNode.getHeight();
                        double height = Math.max(20, selectedNode.getHeight() - (e.getY() - startY));
                        selectedNode.setHeight(height);
                        selectedNode.setY(selectedNode.getY() + (p - height));
                    }
                    if (1 <= selectedDirection && selectedDirection <= 3) {
                        selectedNode.setWidth(Math.max(40, selectedNode.getWidth() + (e.getX() - startX)));
                    }
                    if (3 <= selectedDirection && selectedDirection <= 5) {
                        selectedNode.setHeight(Math.max(20, selectedNode.getHeight() + (e.getY() - startY)));
                    }
                    if (5 <= selectedDirection && selectedDirection <= 7) {
                        double p = selectedNode.getWidth();
                        double width = Math.max(40, selectedNode.getWidth() - (e.getX() - startX));
                        selectedNode.setWidth(width);
                        selectedNode.setX(selectedNode.getX() + (p - width));

                    }
                    if (0 <= selectedDirection && selectedDirection <= 7) {
                        repaint();
                    }
                    if (selectedDirection == 12) {
                        selectedNode.setX(selectedNode.getX() + (e.getX() - startX));
                        selectedNode.setY(selectedNode.getY() + (e.getY() - startY));
                        repaint();
                    }
                }
                startX = e.getX();
                startY = e.getY();
            }
            @Override
            public void mouseMoved(MouseEvent e) {
                super.mouseMoved(e);
                extracted(e);
                if (100 <= selectedDirection) repaint();
            }
        });
    }

    private void extracted(MouseEvent e) {
        setCursor(switch (selectedDirection = getSelectedNode(e)) {
            case 0 -> Cursor.getPredefinedCursor(Cursor.N_RESIZE_CURSOR);
            case 2 -> Cursor.getPredefinedCursor(Cursor.E_RESIZE_CURSOR);
            case 4 -> Cursor.getPredefinedCursor(Cursor.S_RESIZE_CURSOR);
            case 6 -> Cursor.getPredefinedCursor(Cursor.W_RESIZE_CURSOR);
            case 1 -> Cursor.getPredefinedCursor(Cursor.NE_RESIZE_CURSOR);
            case 3 -> Cursor.getPredefinedCursor(Cursor.SE_RESIZE_CURSOR);
            case 5 -> Cursor.getPredefinedCursor(Cursor.SW_RESIZE_CURSOR);
            case 7 -> Cursor.getPredefinedCursor(Cursor.NW_RESIZE_CURSOR);
            default -> Cursor.getDefaultCursor();
        });
        if (100 <= selectedDirection) {
            setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        }
    }

    public int getSelectedNode(MouseEvent e) {
        if (e.getY() < 24) {
            if (e.getX() < getWidth() / 6) {
                selectedNode = new LinkedEvent(new RequestEvent(), -translateX, -translateY, 200, 100);
                return 12;
            }
            selectedNode = null;
            return 16;
        }

        for (LinkedEvent event : userEventsObject.events()) {
            double x = e.getX() - event.getX() - translateX;
            double y = e.getY() - event.getY() - translateY;
            double height = event.getHeight();
            double width = event.getWidth();
            for (int i = 0; i < event.getMaxConnections(); i++) {
                double v = (20 + (height - 20) / (event.getMaxConnections() + 1) * (i + 1)) - y;
                if (Math.sqrt((width - x) * (width - x) + v * v) < 4) {
                    selectedNode = event;
                    return 100 + i;
                }
            }

            if (0 <= y && y <= height) {
                if (-5 <= x && x <= 0) {
                    selectedNode = event;
                    return 6;
                }
                if (width < x && x < width + 5) {
                    selectedNode = event;
                    return 2;
                }
            }
            if (0 <= x && x <= width) {
                if (-5 <= y && y <= 0) {
                    selectedNode = event;
                    return 0;
                }
                if (height < y && y < height + 5) {
                    selectedNode = event;
                    return 4;
                }
            }

            if (-5 <= y && y <= 0 && width < x && x < width + 5) {
                selectedNode = event;
                return 1;
            }
            if (height < y && y < height + 5 && width < x && x < width + 5) {
                selectedNode = event;
                return 3;
            }
            if (height < y && y < height + 5 && -5 <= x && x <= 0) {
                selectedNode = event;
                return 5;
            }
            if (-5 <= y && y <= 0 && -5 <= x && x <= 0) {
                selectedNode = event;
                return 7;
            }
            if (0 <= x && x <= width) {
                if (0 <= y && y <= 20) {
                    selectedNode = event;
                    return 12;
                }
            }

        }
        selectedNode = null;
        return -1;
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
        g2d.translate(translateX, translateY);
        List<LinkedEvent> events = userEventsObject.events();
        for (int i = events.size() - 1; i >= 0; i--) {
            LinkedEvent event = events.get(i);
            // ウィンドウを描画
            drawWindow(g2d, event);
        }
        g2d.translate(-translateX, -translateY);

        // アンチエイリアスを有効にする
        g2d.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
        g2d.setFont(FONT.deriveFont(9f));

        g2d.setColor(Color.WHITE);
        g2d.fillRect(0, 0, getWidth(), 24);
        // 入力ボタン
        g2d.setColor(INPUT);
        g2d.fillRect(2, 2, getWidth() / 6 - 2, 20);
        g2d.setColor(Color.BLACK);

        g2d.drawString("入力",  6, 15);

        // 出力ボタン
        g2d.setColor(OUTPUT);
        g2d.fillRect(getWidth() / 6 + 2, 2, getWidth() / 6 - 2, 20);
        g2d.setColor(Color.BLACK);
        g2d.drawString("出力",  getWidth() / 6 + 6, 15);

        if (100 <= selectedDirection) {
            g2d.setColor(INPUT);
            g2d.drawString(selectedNode.getEvent().getReturns(), (float) (selectedNode.getX() + translateX + selectedNode.getWidth() + 4), (float) (selectedNode.getY() + translateY + 18 + (selectedNode.getHeight() - 20) / (selectedNode.getMaxConnections() + 1) * (selectedDirection - 100 + 1)));
        }

    }

    private static void drawWindow(Graphics2D g2d, LinkedEvent linkedEvent) {
        double x = linkedEvent.getX(), y = linkedEvent.getY(), width = linkedEvent.getWidth(), height = linkedEvent.getHeight();
        g2d.setFont(FONT.deriveFont(10f));
        Event<?> event = linkedEvent.getEvent();
        GeneralPath path = new GeneralPath();
        // 背景部を描画
        path.moveTo(x, y + 20);
        path.lineTo(x + width, y + 20);
        path.lineTo(x + width, y + height - 10);
        path.quadTo(x + width, y + height, x + width - 10, y + height);
        path.lineTo(x + 10, y + height);
        path.quadTo(x, y + height, x, y + height - 10);
        path.closePath();
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
        path.moveTo(x + width * .50, y + 20);
        path.lineTo(x + width * .50 + 10, y + 10);
        path.lineTo(x + width * .50, y);
        path.lineTo(x + width * .15, y);
        path.lineTo(x + width * .15, y + 20);

        g2d.setColor(GRAY1);
        g2d.fill(path);

        drawText(g2d, (float) (x + width * .15 + 10), (float) y + 15, width * .35 - 10, event.getParentName());

        path.reset();
        path.moveTo(x + width * .15, y + 20);
        path.lineTo(x + width * .15 + 10, y + 10);
        path.lineTo(x + width * .15, y);
        path.lineTo(x + 10, y);
        path.quadTo(x, y, x, y + 10);
        path.lineTo(x, y + 20);

        // 色をプリセットから指定
        g2d.setColor(switch(event.getTypeId()) {
            case INPUT -> INPUT;
            case OUTPUT -> OUTPUT;
            case TEMPORARY -> TEMPORARY;
            case OPERATOR -> OPERATOR;
        });
        g2d.fill(path);

        drawText(g2d, (float) x + 5, (float) y + 15, width * .2 - 10, switch(event.getTypeId()) {
            case INPUT -> "入力";
            case OUTPUT -> "出力";
            case TEMPORARY -> "中間";
            case OPERATOR -> "演算";
        });

        int maxConnections = linkedEvent.getMaxConnections();
        for (int i = 0; i < maxConnections; i++) {
            double v = y + 20 + (height - 20) / (maxConnections + 1) * (i + 1);
            g2d.setColor(Color.WHITE);
            g2d.fillOval((int) (x + width - 4), (int) v - 4, 8, 8);
            g2d.setColor(Color.CYAN);
            g2d.fillOval((int) (x + width - 2), (int) v - 2, 4, 4);
        }

        // 引数を表示
        g2d.setColor(Color.BLACK);
        String[] argDesc = event.getTemplate().getArgDescriptions();
        String[] args = event.getArgs();
        for (int i = 0; i < argDesc.length; i++) {
            if (45 + 30 * i > height) break;
            g2d.setFont(g2d.getFont().deriveFont(Font.BOLD, 10));
            drawText(g2d, (float) (x + 5), (float) (y + 35 + 30 * i), width - 10, argDesc[i]);
            g2d.setFont(g2d.getFont().deriveFont(Font.PLAIN, 8));
            drawText(g2d, (float) (x + 5), (float) (y + 46 + 30 * i), width - 10, i > args.length - 1 ? "" : args[i]);
        }


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
        for (int i = text.length() - 1; i >= 0; i--) {
            String result = text.substring(0, i);
            if (font.stringWidth(result) + bTextWidth <= width) {
                return result + "...";
            }
        }
        return "...";
    }

    public void setUserEventsObject(UserEventsObject userEventsObject) {
        this.userEventsObject = userEventsObject;
    }
}