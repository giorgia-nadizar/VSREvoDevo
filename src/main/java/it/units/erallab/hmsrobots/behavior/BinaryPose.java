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

package it.units.erallab.hmsrobots.behavior;

import it.units.erallab.hmsrobots.util.Grid;

import java.util.List;

/**
 * @author "Eric Medvet" on 2021/12/03 for 2dhmsr
 */
public class BinaryPose {
  private final List<Grid.Key> contractedVoxels;

  public BinaryPose(List<Grid.Key> contractedVoxels) {
    this.contractedVoxels = contractedVoxels;
  }

  public List<Grid.Key> getContractedVoxels() {
    return contractedVoxels;
  }
}
