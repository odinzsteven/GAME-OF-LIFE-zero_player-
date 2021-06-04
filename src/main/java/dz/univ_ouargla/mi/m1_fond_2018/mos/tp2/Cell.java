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

import java.awt.Color;
import java.awt.Graphics2D;
import java.util.NoSuchElementException;
import java.util.Objects;

import org.jetbrains.annotations.NotNull;

public class Cell {

    private static final int EMPTY = 0;
    public static final State EMPTY_STATE = new State(EMPTY, "Vide", new Color(155, 100, 50));

    private static final int BURNED = 1;
    public static final State BURNED_STATE = new State(BURNED, "Brulée", new Color(50, 50, 50));

    private static final int FIRE = 3;
    public static final State FIRE_STATE = new State(FIRE, "Feu", new Color(255, 40, 20));

    private static final int BURNED_SLIGHTLY = 4;
    public static final State BURNED_SLIGHTLY_STATE = new State(BURNED_SLIGHTLY, "Brulée Froide", new Color(100, 100, 100));

    private static final int TREE = 8;
    public static final State TREE_STATE = new State(TREE, "Arbre", new Color(45, 150, 25));

    private static final State[] STATES = {
            EMPTY_STATE,
            BURNED_STATE,
            null,
            FIRE_STATE,
            BURNED_SLIGHTLY_STATE,
            null, null, null,
            TREE_STATE};

    private static final int CURRENT_STATE_MASK = 0b1111;
    private static final int NEXT_STATE_MASK = 0b1111_0000;
    private static final int FLAMMABILITY_MASK = 0b11;

    private State currentState = null;
    private State nextState = null;
    private int flammability = 0;

    public void draw(final Graphics2D g, final int x, final int y, final int cellSize) {
        g.setColor(currentState.color());
        if (currentState == STATES[TREE] || currentState == STATES[FIRE])
            g.fill3DRect(x, y, cellSize, cellSize, true);
        else
            g.fillRect(x, y, cellSize, cellSize);
    }

    public byte exportState() {
        return (byte) (currentState.ordinal() | nextState.ordinal() << 4);
    }

    public void importState(final byte data) {
        final int encodedState = Byte.toUnsignedInt(data);
        currentState = decodeState(encodedState, CURRENT_STATE_MASK, 0);
        nextState = decodeState(encodedState, NEXT_STATE_MASK, 4);
        flammability = 0;
    }

    private State decodeState(final int encodedState, final int mask, final int position) {
        return STATES[(encodedState & mask) >> position];
    }

    @NotNull
    public State getState(final int ordinal) {
        final State state = STATES[ordinal];
        if (state == null)
            throw new NoSuchElementException("for: " + ordinal);
        return state;
    }

    public void evolve() {
        switch (currentState.ordinal) {
            case EMPTY:
                nextState = STATES[EMPTY];
                break;
            case BURNED:
                nextState = STATES[BURNED_SLIGHTLY];
                break;
            case BURNED_SLIGHTLY:
                nextState = STATES[BURNED_SLIGHTLY];
                break;
            case FIRE:
                nextState = STATES[BURNED];
                break;
            case TREE:
                nextState = STATES[TREE];
                break;
            default:
                assert false;
        }
    }

    public void consider(final byte neighbor) {
        flammability += neighbor & FLAMMABILITY_MASK;
    }

    public boolean stillEvolving() {
        if (currentState != TREE_STATE)
            return false;

        final boolean onFire = flammability >= FIRE;
        if (onFire)
            nextState = STATES[FIRE];

        return !onFire;
    }

    public boolean onClick(final State cursorType) {
        final boolean changeState = currentState != cursorType;
        if (changeState)
            currentState = cursorType;

        return changeState;
    }

    public void reset() {
        currentState = null;
        nextState = null;
        flammability = 0;
    }

    public static final class State {

        private final int ordinal;
        private final String name;
        private final Color color;

        private State(final int ordinal, final String name, final Color color) {
            this.ordinal = ordinal;
            this.name = Objects.requireNonNull(name);
            this.color = Objects.requireNonNull(color);
        }

        public int ordinal() {
            return ordinal;
        }

        @NotNull
        public Color color() {
            return color;
        }

        @Override
        @NotNull
        public String toString() {
            return name;
        }
    }
}
