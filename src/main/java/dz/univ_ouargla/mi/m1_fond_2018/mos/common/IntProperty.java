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

import org.jetbrains.annotations.NotNull;

public enum IntProperty {

    FRAME_DELAY("Durée (ms):", 500, 30, 60_000, 100),
    CELL_SIZE("Taille:", 10, 1, 50, 1),
    BORDER_WIDTH("Bordure:", 1, 0, 10, 1);

    private final String fullName = getClass().getCanonicalName() + '#' + name();

    private String label;
    private final int defaultValue;
    private final int minValue;
    private final int maxValue;
    private final int step;

    IntProperty(final String label, final int defaultValue, final int minValue, final int maxValue, final int step) {
        this.label = label;
        this.defaultValue = defaultValue;
        this.minValue = minValue;
        this.maxValue = maxValue;
        this.step = step;
    }

    @NotNull
    public String fullName() {
        return fullName;
    }

    public String getLabel() {
        return label;
    }

    public int getDefault() {
        return defaultValue;
    }

    public int getMin() {
        return minValue;
    }

    public int getMax() {
        return maxValue;
    }

    public int getStep() {
        return step;
    }
}
