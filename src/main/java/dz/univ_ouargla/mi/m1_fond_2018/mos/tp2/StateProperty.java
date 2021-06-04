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

import org.jetbrains.annotations.NotNull;

public enum StateProperty {

    CURSOR_TYPE(Cell.TREE_STATE);

    private final String fullName = getClass().getCanonicalName() + '#' + name();

    private final Cell.State defaultState;

    StateProperty(final Cell.State defaultState) {
        this.defaultState = defaultState;
    }

    @NotNull
    public String fullName() {
        return fullName;
    }

    @NotNull
    public Cell.State getDefault() {
        return defaultState;
    }
}
