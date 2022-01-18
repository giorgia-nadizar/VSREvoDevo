/*
 * Copyright (C) 2021 Giorgia Nadizar <giorgia.nadizar@gmail.com> (as Giorgia Nadizar)
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

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonProperty;
import it.units.erallab.hmsrobots.core.objects.ControllableVoxel;
import it.units.erallab.hmsrobots.util.Grid;

public class StepController<V extends ControllableVoxel> extends AbstractController<V> {

  @JsonProperty
  private final AbstractController<V> innerController;
  @JsonProperty
  private final double stepT;

  double lastT = Double.NEGATIVE_INFINITY;
  Grid<Double> lastControlSignals = null;

  @JsonCreator
  public StepController(
      @JsonProperty("innerController") AbstractController<V> innerController,
      @JsonProperty("stepT") double stepT) {
    this.innerController = innerController;
    this.stepT = stepT;
  }

  @Override
  public Grid<Double> computeControlSignals(double t, Grid<? extends V> voxels) {
    Grid<Double> controlSignals = innerController.computeControlSignals(t, voxels);
    if (t - lastT >= stepT || lastControlSignals == null) {
      lastControlSignals = Grid.create(controlSignals, v -> v);
      lastT = t;
    }
    return lastControlSignals;
  }

  @Override
  public void reset() {
    innerController.reset();
    lastT = Double.NEGATIVE_INFINITY;
  }

}
