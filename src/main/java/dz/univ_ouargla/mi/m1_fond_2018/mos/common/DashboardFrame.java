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

import java.awt.BorderLayout;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.HeadlessException;
import java.awt.Toolkit;
import java.util.Objects;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.WindowConstants;

import org.jetbrains.annotations.NotNull;

public class DashboardFrame extends JFrame {

    private static final long serialVersionUID = -4353093736437769640L;
    private static final Dimension DEFAULT_WINDOW_SIZE;

    static {
        final var screenSize = Toolkit.getDefaultToolkit().getScreenSize();
        DEFAULT_WINDOW_SIZE = new Dimension(screenSize.width / 2, screenSize.height / 2);
    }

    public DashboardFrame(final @NotNull String title,
                          final @NotNull Component dashboard,
                          final @NotNull Component screen) throws HeadlessException {
        super(Objects.requireNonNull(title));
        setup(Objects.requireNonNull(dashboard), Objects.requireNonNull(screen));
    }

    private void setup(final @NotNull Component dashboard,
                       final @NotNull Component screen) {

        setDefaultCloseOperation(WindowConstants.EXIT_ON_CLOSE);

        final var content = new JPanel(new BorderLayout());
        content.add(dashboard, BorderLayout.NORTH);
        content.add(screen, BorderLayout.CENTER);

        setContentPane(content);
        setPreferredSize(DEFAULT_WINDOW_SIZE);
    }

    public void launch() {
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }
}
