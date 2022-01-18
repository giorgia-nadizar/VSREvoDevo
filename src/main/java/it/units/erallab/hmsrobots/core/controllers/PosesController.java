/*
 * Copyright (c) "Eric Medvet" 2021.
 *
 * This program is free software: you can redistribute it and/or modify it
 * under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful, but
 * WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.
 * See the GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package it.units.erallab.hmsrobots.core.controllers;

import it.units.erallab.hmsrobots.behavior.BinaryPose;
import it.units.erallab.hmsrobots.core.objects.SensingVoxel;
import it.units.erallab.hmsrobots.util.Grid;

import java.util.List;

/**
 * @author "Eric Medvet" on 2021/12/03 for 2dhmsr
 */
public class PosesController extends AbstractController<SensingVoxel> {

  private final double stepT;
  private final List<BinaryPose> poses;

  public PosesController(double stepT, List<BinaryPose> poses) {
    this.stepT = stepT;
    this.poses = poses;
  }

  @Override
  public Grid<Double> computeControlSignals(double t, Grid<? extends SensingVoxel> voxels) {
    int poseIndex = (int) Math.round(t / stepT) % poses.size();
    Grid<Double> values = Grid.create(voxels, v -> -1d);
    for (Grid.Key key : poses.get(poseIndex).getContractedVoxels()) {
      if (key.getX() >= 0 && key.getX() < values.getW() && key.getY() >= 0 && key.getY() < values.getH()) {
        values.set(key.getX(), key.getY(), 1d);
      }
    }
    return values;
  }

  @Override
  public void reset() {

  }
}
