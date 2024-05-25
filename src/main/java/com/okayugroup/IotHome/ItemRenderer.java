package com.okayugroup.IotHome;
import com.okayugroup.IotHome.event.Event;

import javax.swing.*;
import java.awt.*;

public class ItemRenderer extends JLabel implements ListCellRenderer<Event> {

    public ItemRenderer() {
        setOpaque(true);
    }

    @Override
    public Component getListCellRendererComponent(JList<? extends Event> list, Event value, int index,
                                                  boolean isSelected, boolean cellHasFocus) {
        if (isSelected) {
            setBackground(list.getSelectionBackground());
            setForeground(list.getSelectionForeground());
        } else {
            setBackground(list.getBackground());
            setForeground(list.getForeground());
        }

        // 表示したい情報を設定
        setText(value == null ? "[無選択]" : value.name);
        return this;
    }
}
