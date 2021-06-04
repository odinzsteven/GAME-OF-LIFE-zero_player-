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

import java.awt.BasicStroke;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Toolkit;
import java.awt.event.MouseEvent;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

import dz.univ_ouargla.mi.m1_fond_2018.mos.common.AbstractCellsManager;
import dz.univ_ouargla.mi.m1_fond_2018.mos.common.IntProperty;

public final class ForestKeeper extends AbstractCellsManager {

    public static final int MIN_LENGTH = 10;
    public static final int MAX_LENGTH = 46_340;

    private static final Color BORDER_COLOR = new Color(175, 117, 55);
    private static final int CELLS_IN_ONE_DATA_CHUNK;

    static {
        final var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        final var halfScreenResolution = screenSize.width * screenSize.height / 2;
        final var defaultCellSize = IntProperty.CELL_SIZE.getDefault() + IntProperty.BORDER_WIDTH.getDefault();
        CELLS_IN_ONE_DATA_CHUNK = halfScreenResolution / defaultCellSize;
    }

    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    private final List<byte[]> data = new LinkedList<>();
    private final Cell cell = new Cell();
    private final Point[] neighbors = {
            new Point(-1, -1), new Point(0, -1), new Point(1, -1),
            new Point(-1, 0), new Point(1, 0),
            new Point(-1, 1), new Point(0, 1), new Point(1, 1),
    };

    private volatile int columnsCount;
    private volatile int rowsCount;

    private volatile Dimension size = new Dimension();
    private volatile int cellSize;
    private volatile int borderWidth;

    public void evolve() {
        lock.writeLock().lock();
        try {
            for (int column = 0; column < columnsCount; column++)
                for (int row = 0; row < rowsCount; row++) {
                    final int index = indexOf(column, row);
                    cell.importState(getDataAt(index));
                    cell.evolve();

                    int i = 0;
                    while (cell.stillEvolving() && i < neighbors.length) {
                        final Point neighbor = neighbors[i++];
                        cell.consider(getCellData(column + neighbor.x, row + neighbor.y));
                    }

                    setDataAt(index, cell.exportState());
                }

            for (int column = 0; column < columnsCount; column++)
                for (int row = 0; row < rowsCount; row++) {
                    final int index = indexOf(column, row);
                    final byte data = getDataAt(index);
                    final int newData = Byte.toUnsignedInt(data) >> 4;
                    setDataAt(index, (byte) newData);
                }

            cell.reset();
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void pack(final Dimension size, final int cellSize, final int borderWidth) {
        lock.writeLock().lock();
        try {
            final int newColumnsCount = countCellsIn(size.width, cellSize, borderWidth);
            final int newRowsCount = countCellsIn(size.height, cellSize, borderWidth);

            ensureCellsAvailable(newColumnsCount, newRowsCount);

            this.columnsCount = newColumnsCount;
            this.rowsCount = newRowsCount;
        } finally {
            lock.writeLock().unlock();
        }
    }

    private int countCellsIn(final int length, final int cellSize, final int borderWidth) {
        final var pureLength = length - borderWidth;
        return Math.max(pureLength, 0) / (borderWidth + cellSize);
    }

    private void ensureCellsAvailable(final int columnsCount, final int rowsCount) {
        final var longestDimension = Math.max(columnsCount, rowsCount);
        ensureCellsAtLeast(longestDimension * longestDimension);
    }

    private void ensureCellsAtLeast(final int minimumCells) {
        final List<byte[]> data = this.data;
        while (data.size() * CELLS_IN_ONE_DATA_CHUNK < minimumCells)
            data.add(new byte[CELLS_IN_ONE_DATA_CHUNK]);
    }

    public void paint(final Graphics2D g, final Dimension size, final int cellSize, final int borderWidth) {
        lock.readLock().lock();
        try {
            this.size = size;
            this.cellSize = cellSize;
            this.borderWidth = borderWidth;

            drawBackground(g);
            drawGrid(g);
        } finally {
            lock.readLock().unlock();
        }
    }

    private void drawBackground(final Graphics g) {
        g.setColor(Color.WHITE);
        g.fillRect(0, 0, size.width - 1, size.height - 1);
    }

    private void drawGrid(final Graphics2D g) {
        final int cellBox = cellSize + borderWidth;

        final var widthMargin = calcWidthMargin(size, cellBox);
        final var heightMargin = calcHeightMargin(size, cellBox);

        drawBorders(g, borderWidth, cellBox, widthMargin, heightMargin);

        final var halfBorder = borderWidth - borderWidth / 2;
        drawCells(g, cellBox, widthMargin + halfBorder, heightMargin + halfBorder);
    }

    private int calcHeightMargin(final Dimension size, final int cellBox) {
        final int fullMargin = size.height - 1 - cellBox * rowsCount;
        return fullMargin / 2;
    }

    private int calcWidthMargin(final Dimension size, final int cellBox) {
        final int fullMargin = size.width - 1 - cellBox * columnsCount;
        return fullMargin / 2;
    }

    private void drawCells(final Graphics2D g, final int cellBoxSize, final int columnsOffset, final int rowsOffset) {
        for (int column = 0; column < columnsCount; column++)
            for (int row = 0; row < rowsCount; row++) {
                final int x = column * cellBoxSize + columnsOffset;
                final int y = row * cellBoxSize + rowsOffset;
                cell.importState(getCellData(column, row));
                cell.draw(g, x, y, cellSize);
            }

        cell.reset();
    }

    private int indexOf(final int column, final int row) {
        if (column < row)
            return row * row + row * 2 - column;
        else if (column > row)
            return column * column + row;
        else
            return column * column + column;
    }

    private byte getCellData(final int column, final int row) {
        if (column < 0 || row < 0 || column >= columnsCount || row >= rowsCount)
            return 0;
        else
            return getDataAt(indexOf(column, row));
    }

    private byte getDataAt(final int index) {
        final byte[] cellsData = data.get(index / CELLS_IN_ONE_DATA_CHUNK);
        return cellsData[index % CELLS_IN_ONE_DATA_CHUNK];
    }

    private void setCellData(final int column, final int row, final byte data) {
        if (column < 0 || row < 0 || column >= columnsCount || row >= rowsCount)
            throw new IndexOutOfBoundsException("for column: " + column + " and row: " + row);
        else
            setDataAt(indexOf(column, row), data);
    }

    private void setDataAt(final int index, final byte cellData) {
        final byte[] cellsData = data.get(index / CELLS_IN_ONE_DATA_CHUNK);
        cellsData[index % CELLS_IN_ONE_DATA_CHUNK] = cellData;
    }

    private void drawBorders(final Graphics2D g, final int borderWidth, final int cellBoxSize,
                             final int columnsOffset, final int rowsOffset) {

        g.setColor(BORDER_COLOR);
        g.setStroke(new BasicStroke(borderWidth));

        final var columnsLimit = cellBoxSize * columnsCount + columnsOffset;
        final var rowsLimit = cellBoxSize * rowsCount + rowsOffset;

        drawColumns(g, cellBoxSize, columnsOffset, rowsOffset, rowsLimit, columnsCount);
        drawRows(g, cellBoxSize, columnsOffset, rowsOffset, columnsLimit, rowsCount);
    }

    private void drawColumns(final Graphics2D g, final int cellBoxSize,
                             final int columnsOffset, final int rowsOffset,
                             final int rowsLimit, final int columnsCount) {
        g.drawLine(columnsOffset, rowsOffset, columnsOffset, rowsLimit);

        for (var i = 1; i <= columnsCount; i++) {
            final var columnOffset = cellBoxSize * i + columnsOffset;
            g.drawLine(columnOffset, rowsOffset, columnOffset, rowsLimit);
        }
    }

    private void drawRows(final Graphics2D g, final int cellBoxSize,
                          final int columnsOffset, final int rowsOffset,
                          final int columnsLimit, final int rowsCount) {
        g.drawLine(columnsOffset, rowsOffset, columnsLimit, rowsOffset);

        for (int i = 1; i <= rowsCount; i++) {
            final var rowOffset = cellBoxSize * i + rowsOffset;
            g.drawLine(columnsOffset, rowOffset, columnsLimit, rowOffset);
        }
    }

    public boolean handleClick(final MouseEvent event, final Cell.State cursorType) {
        lock.writeLock().lock();
        try {
            final int inputX = event.getX();
            final int inputY = event.getY();

            if (inputX >= 0 && inputY >= 0 && inputX < size.width && inputY < size.height)
                return handleClickAt(inputX, inputY, cursorType);
            else
                return false;

        } finally {
            lock.writeLock().unlock();
        }
    }

    private boolean handleClickAt(final int inputX, final int inputY, final Cell.State cursorType) {
        final int cellBox = cellSize + borderWidth;

        final var widthMargin = calcWidthMargin(size, cellBox);
        final var heightMargin = calcHeightMargin(size, cellBox);

        final int x = inputX - widthMargin - borderWidth + borderWidth / 2;
        final int y = inputY - heightMargin - borderWidth + borderWidth / 2;

        if (x >= 0 && y >= 0 && x % cellBox < cellSize && y % cellBox < cellSize) {
            final int column = x / cellBox;
            final int row = y / cellBox;
            return handleClickOn(column, row, cursorType);
        } else
            return false;
    }

    private boolean handleClickOn(final int column, final int row, final Cell.State cursorType) {
        if (column < columnsCount && row < rowsCount) {
            cell.importState(getCellData(column, row));

            final boolean stateChanged = cell.onClick(cursorType);

            if (stateChanged)
                setCellData(column, row, cell.exportState());

            return stateChanged;
        } else
            return false;
    }

    public Dimension getDimensions() {
        lock.readLock().lock();
        try {
            return new Dimension(columnsCount, rowsCount);
        } finally {
            lock.readLock().unlock();
        }
    }

    public void copyFrom(final ForestKeeper forestKeeper) {
        lock.writeLock().lock();
        try {
            copy(forestKeeper);
        } finally {
            lock.writeLock().unlock();
        }
    }

    private void copy(final ForestKeeper forestKeeper) {
        forestKeeper.lock.readLock().lock();
        try {
            this.columnsCount = forestKeeper.columnsCount;
            this.rowsCount = forestKeeper.rowsCount;

            final List<byte[]> dataSrc = forestKeeper.data;
            ensureCellsAtLeast(dataSrc.size() * CELLS_IN_ONE_DATA_CHUNK);
            for (int i = 0; i < dataSrc.size(); i++) {
                final byte[] src = dataSrc.get(i);
                System.arraycopy(src, 0, this.data.get(i), 0, src.length);
            }
        } finally {
            forestKeeper.lock.readLock().unlock();
        }
    }
}
