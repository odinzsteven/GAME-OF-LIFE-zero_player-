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

import java.awt.Dimension;
import java.awt.Graphics2D;

public abstract class AbstractCellsManager {

    public abstract void evolve();

    public abstract void pack(final Dimension size, final int cellSize, final int borderWidth);

    public abstract void paint(final Graphics2D g, final Dimension size, final int cellSize, final int borderWidth);
}
