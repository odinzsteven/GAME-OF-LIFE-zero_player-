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

import java.awt.event.MouseEvent;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import javax.swing.event.MouseInputAdapter;

import dz.univ_ouargla.mi.m1_fond_2018.mos.common.BasicCellGrid;
import org.jetbrains.annotations.NotNull;

public final class Forest extends BasicCellGrid {

    private static final long serialVersionUID = 5197086095816885130L;

    private final ForestKeeper forestKeeper;

    private volatile Cell.State cursorType = StateProperty.CURSOR_TYPE.getDefault();

    public Forest(final ForestKeeper forestKeeper) {
        super(forestKeeper);
        this.forestKeeper = forestKeeper;
        addListeners();
    }

    private void addListeners() {
        final MouseListener mouseListener = new MouseListener(this);
        addMouseListener(mouseListener);
        addMouseMotionListener(mouseListener);
    }

    public void editCursorType(final PropertyChangeEvent event) {
        final Object newCursorType = event.getNewValue();
        if (newCursorType instanceof Cell.State)
            setCursorType((Cell.State) newCursorType);
    }

    public Cell.State getCursorType() {
        return cursorType;
    }

    public void setCursorType(final Cell.State newCursorType) {
        settersLock.lock();
        try {
            final Cell.State oldCursorType = cursorType;
            if (oldCursorType != newCursorType) {
                cursorType = newCursorType;
                firePropertyChange(StateProperty.CURSOR_TYPE.fullName(), oldCursorType, newCursorType);
            }
        } finally {
            settersLock.unlock();
        }
    }

    private void handleClick(final MouseEvent event) {
        final boolean paintNeeded = forestKeeper.handleClick(event, cursorType);
        if (paintNeeded)
            repaint();
    }

    private static final class MouseListener extends MouseInputAdapter {

        private final Forest cellGrid;

        public MouseListener(@NotNull final Forest cellGrid) {
            this.cellGrid = Objects.requireNonNull(cellGrid);
        }

        @Override
        public void mouseClicked(final MouseEvent event) {
            cellGrid.handleClick(event);
        }

        @Override
        public void mouseDragged(final MouseEvent event) {
            cellGrid.handleClick(event);
        }

    }
}
