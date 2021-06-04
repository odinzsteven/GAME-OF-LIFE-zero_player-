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

package dz.univ_ouargla.mi.m1_fond_2018.mos.common;

import java.awt.FlowLayout;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.util.HashMap;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JSeparator;
import javax.swing.JSpinner;
import javax.swing.JToggleButton;
import javax.swing.SpinnerNumberModel;
import javax.swing.SwingConstants;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class CellGridDashboard extends JPanel {

    private static final long serialVersionUID = 6508454969145894827L;
    private HashMap<IntProperty, Integer> oldIntValues = new HashMap<>();

    public CellGridDashboard(final BasicCellGrid cellGrid) {
        super(new FlowLayout(FlowLayout.CENTER));
        setup(cellGrid);
    }

    private void setup(final BasicCellGrid cellGrid) {
        add(newToggleButton(BooleanProperty.STARTED));

        add(new JSeparator(SwingConstants.VERTICAL));
        add(new JLabel(IntProperty.FRAME_DELAY.getLabel()));
        add(newIntSpinner(IntProperty.FRAME_DELAY));

        add(new JSeparator(SwingConstants.VERTICAL));
        add(new JLabel(IntProperty.CELL_SIZE.getLabel()));
        add(newIntSpinner(IntProperty.CELL_SIZE));

        add(new JSeparator(SwingConstants.VERTICAL));
        add(new JLabel(IntProperty.BORDER_WIDTH.getLabel()));
        add(newIntSpinner(IntProperty.BORDER_WIDTH));

        addPropertyChangeListener(BooleanProperty.STARTED.fullName(), cellGrid::editStartedState);
        addPropertyChangeListener(IntProperty.FRAME_DELAY.fullName(), cellGrid::editFrameDelay);
        addPropertyChangeListener(IntProperty.CELL_SIZE.fullName(), cellGrid::editCellSize);
        addPropertyChangeListener(IntProperty.BORDER_WIDTH.fullName(), cellGrid::editBorderWidth);
    }

    @Contract(value = "_ -> new", pure = true)
    private JToggleButton newToggleButton(@NotNull final BooleanProperty property) {
        final var button = new JToggleButton(property.getLabel());
        button.setSelected(property.getDefault());
        button.addActionListener(event -> {
            final boolean newState = button.isSelected();
            firePropertyChange(BooleanProperty.STARTED.fullName(), !newState, newState);
        });
        return button;
    }

    @Contract(value = "!null -> new", pure = true)
    private JSpinner newIntSpinner(@NotNull final IntProperty intProperty) {
        final SpinnerNumberModel model = new SpinnerNumberModel(
                intProperty.getDefault(),
                intProperty.getMin(),
                intProperty.getMax(),
                intProperty.getStep());

        final var spinner = new JSpinner(model);

        spinner.addChangeListener(event -> {
            final Integer newValue = model.getNumber().intValue();
            firePropertyChange(intProperty.fullName(), oldIntValues.get(intProperty), newValue);
            oldIntValues.put(intProperty, newValue);
        });

        spinner.addFocusListener(new FocusAdapter() {
            @Override
            public void focusLost(final FocusEvent e) {
                final Integer oldValue = oldIntValues.get(intProperty);
                model.setValue(oldValue != null ? oldValue : intProperty.getDefault());
            }
        });
        return spinner;
    }
}
