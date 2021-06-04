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

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.beans.PropertyChangeEvent;
import java.util.Objects;
import java.util.concurrent.locks.Lock;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.JPanel;
import javax.swing.Timer;

import org.jetbrains.annotations.Contract;
import org.jetbrains.annotations.NotNull;

public class BasicCellGrid extends JPanel {

    private static final long serialVersionUID = 3151857207506041641L;

    protected final Lock settersLock = new ReentrantLock();
    protected final Timer animator;

    private final AbstractCellsManager cells;
    private volatile boolean started = BooleanProperty.STARTED.getDefault();
    private volatile int cellSize = IntProperty.CELL_SIZE.getDefault();
    private volatile int borderWidth = IntProperty.BORDER_WIDTH.getDefault();

    public BasicCellGrid(final @NotNull AbstractCellsManager cells) {
        this.cells = Objects.requireNonNull(cells);
        this.animator = new Timer(IntProperty.FRAME_DELAY.getDefault(), event -> {
            cells.evolve();
            repaint();
        });
        addListeners();
    }

    private void addListeners() {
        addComponentListener(new SizeListener(this));
    }

    public void editStartedState(final PropertyChangeEvent event) {
        final Object newState = event.getNewValue();
        if (Boolean.TRUE.equals(newState))
            setStarted(true);
        else if (Boolean.FALSE.equals(newState))
            setStarted(false);
    }

    public void editFrameDelay(final PropertyChangeEvent event) {
        final Object newDelay = event.getNewValue();
        if (newDelay instanceof Number)
            setFrameDelay(((Number) newDelay).intValue());
    }

    public void editCellSize(final PropertyChangeEvent event) {
        final Object newCellSize = event.getNewValue();
        if (newCellSize instanceof Number)
            setCellSize(((Number) newCellSize).intValue());
    }

    public void editBorderWidth(final PropertyChangeEvent event) {
        final Object newBorderWidth = event.getNewValue();
        if (newBorderWidth instanceof Number)
            setBorderWidth(((Number) newBorderWidth).intValue());
    }

    @Contract(pure = true)
    public final boolean isStarted() {
        return started;
    }

    public final void setStarted(final boolean newStartedState) {
        settersLock.lock();
        try {
            final var oldStartedState = started;
            if (oldStartedState != newStartedState) {
                started = newStartedState;
                if (started)
                    animator.start();
                else
                    animator.stop();
                firePropertyChange(BooleanProperty.STARTED.fullName(), oldStartedState, newStartedState);
            }
        } finally {
            settersLock.unlock();
        }
    }

    @Contract(pure = true)
    public final int getFrameDelay() {
        return animator.getDelay();
    }

    public final void setFrameDelay(final int newFrameDelay) {
        settersLock.lock();
        try {
            final var oldFrameDelay = animator.getDelay();
            if (oldFrameDelay != newFrameDelay) {
                animator.setDelay(newFrameDelay);
                firePropertyChange(IntProperty.FRAME_DELAY.fullName(), oldFrameDelay, newFrameDelay);
            }
        } finally {
            settersLock.unlock();
        }
    }

    @Contract(pure = true)
    public final int getCellSize() {
        return cellSize;
    }

    public final void setCellSize(final int newCellSize) {
        settersLock.lock();
        try {
            final var oldCellSize = cellSize;
            if (oldCellSize != newCellSize) {
                cellSize = newCellSize;
                packCells();
                repaint();
                firePropertyChange(IntProperty.CELL_SIZE.fullName(), oldCellSize, newCellSize);
            }
        } finally {
            settersLock.unlock();
        }
    }

    @Contract(pure = true)
    public final int getBorderWidth() {
        return borderWidth;
    }

    public final void setBorderWidth(final int newBorderWidth) {
        settersLock.lock();
        try {
            final var oldBorderWidth = borderWidth;
            if (oldBorderWidth != newBorderWidth) {
                borderWidth = newBorderWidth;
                packCells();
                repaint();
                firePropertyChange(IntProperty.BORDER_WIDTH.fullName(), oldBorderWidth, newBorderWidth);
            }
        } finally {
            settersLock.unlock();
        }
    }

    private void packCells() {
        cells.pack(getSize(), cellSize, borderWidth);
    }

    @Override
    protected void validateTree() {
        super.validateTree();
        packCells();
        if (BooleanProperty.STARTED.getDefault())
            animator.start();
    }

    @Override
    public final void paint(final Graphics g) {
        super.paint(g);
        cells.paint((Graphics2D) g, getSize(), cellSize, borderWidth);
    }

    private static final class SizeListener extends ComponentAdapter {

        private final BasicCellGrid cellGrid;

        private SizeListener(@NotNull final BasicCellGrid cellGrid) {
            this.cellGrid = Objects.requireNonNull(cellGrid);
        }

        @Override
        public void componentResized(final ComponentEvent e) {
            cellGrid.packCells();
        }
    }
}
