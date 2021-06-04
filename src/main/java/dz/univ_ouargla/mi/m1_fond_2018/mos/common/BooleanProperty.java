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

public enum BooleanProperty {

    STARTED("Démare", false);

    private final String fullName = getClass().getCanonicalName() + '#' + name();

    private final String label;
    private final boolean defaultValue;

    BooleanProperty(final String label, final boolean defaultValue) {
        this.label = label;
        this.defaultValue = defaultValue;
    }

    @NotNull
    public String fullName() {
        return fullName;
    }

    public boolean getDefault() {
        return defaultValue;
    }

    public String getLabel() {
        return label;
    }
}
