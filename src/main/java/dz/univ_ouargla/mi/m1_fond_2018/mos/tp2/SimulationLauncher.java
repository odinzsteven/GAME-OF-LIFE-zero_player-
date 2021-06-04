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

import javax.swing.SwingUtilities;

import dz.univ_ouargla.mi.m1_fond_2018.mos.common.DashboardFrame;

public final class SimulationLauncher {

    public static void main(final String... arguments) {
        final var forest = new Forest(new ForestKeeper());
        final var dashboard = new Dashboard(forest);
        final var simulation = new DashboardFrame("Forest Simulator", dashboard, forest);
        SwingUtilities.invokeLater(simulation::launch);
        
    }
}
