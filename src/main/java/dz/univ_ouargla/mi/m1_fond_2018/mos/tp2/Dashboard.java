/*
 * Copyright (c) 2018 Youcef DEBBAH (youcef-debbah@hotmail.com)
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software
 * and associated documentation files (the Software) to deal in the Software without restriction
 * but under the following conditions:
 *
 * - This notice shall be included in all copies and portions of the Software.
 * - The software is provided "AS IS", WITHOUT WARRANTY OF ANY KIND (Implicit or Explicit).
 *
 */

package dz.univ_ouargla.mi.m1_fond_2018.mos.tp2;

import javax.swing.JComboBox;
import javax.swing.JSeparator;
import javax.swing.SwingConstants;

import dz.univ_ouargla.mi.m1_fond_2018.mos.common.CellGridDashboard;

public class Dashboard extends CellGridDashboard {

    private static final long serialVersionUID = 497951304869397529L;

    public Dashboard(final Forest forest) {
        super(forest);
        setup(forest);
    }

    private void setup(final Forest forest) {
        add(new JSeparator(SwingConstants.VERTICAL));
        add(newCursorTypePalette());
        addPropertyChangeListener(StateProperty.CURSOR_TYPE.fullName(), forest::editCursorType);
    }

    private JComboBox<Cell.State> newCursorTypePalette() {
        final JComboBox<Cell.State> comboBox = new JComboBox<>();
        comboBox.addItem(Cell.TREE_STATE);
        comboBox.addItem(Cell.FIRE_STATE);
        comboBox.addItem(Cell.EMPTY_STATE);
        comboBox.addItem(Cell.BURNED_STATE);
        comboBox.addItem(Cell.BURNED_SLIGHTLY_STATE);

        comboBox.setSelectedItem(StateProperty.CURSOR_TYPE.getDefault());
        comboBox.addActionListener(event ->
                firePropertyChange(StateProperty.CURSOR_TYPE.fullName(), null, comboBox.getSelectedItem()));

        return comboBox;
    }
}
