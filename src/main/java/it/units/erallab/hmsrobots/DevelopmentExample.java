/*
 * Copyright (C) 2021 Eric Medvet <eric.medvet@gmail.com> (as Eric Medvet <eric.medvet@gmail.com>)
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
package it.units.erallab.hmsrobots;

import it.units.erallab.hmsrobots.core.controllers.TimeFunctions;
import it.units.erallab.hmsrobots.core.objects.Robot;
import it.units.erallab.hmsrobots.core.objects.SensingVoxel;
import it.units.erallab.hmsrobots.tasks.devolocomotion.DistanceBasedDevoLocomotion;
import it.units.erallab.hmsrobots.tasks.devolocomotion.TimeBasedDevoLocomotion;
import it.units.erallab.hmsrobots.tasks.locomotion.Locomotion;
import it.units.erallab.hmsrobots.util.Grid;
import it.units.erallab.hmsrobots.util.RobotUtils;
import it.units.erallab.hmsrobots.viewers.GridOnlineViewer;
import org.dyn4j.dynamics.Settings;

import java.util.function.UnaryOperator;

/**
 * @author Eric Medvet <eric.medvet@gmail.com>
 */
public class DevelopmentExample {

  public static void main(String[] args) {
    timeBasedDevelopment();
  }

  private static void timeBasedDevelopment() {
    UnaryOperator<Robot<?>> devoFunction = devoComb();
    TimeBasedDevoLocomotion devoLocomotion = TimeBasedDevoLocomotion.uniformlyDistributedTimeBasedDevoLocomotion(10, 40d, Locomotion.createTerrain("downhill-20"), new Settings());
    GridOnlineViewer.run(devoLocomotion, devoFunction);
  }

  private static void distanceBasedDevelopment() {
    UnaryOperator<Robot<?>> devoFunction = devoComb();
    DistanceBasedDevoLocomotion devoLocomotion = new DistanceBasedDevoLocomotion(20, 20, 60, Locomotion.createTerrain("downhill-20"), new Settings());
    GridOnlineViewer.run(devoLocomotion, devoFunction);
  }

  private static UnaryOperator<Robot<?>> devoComb() {
    int initialLength = 3;
    double f = 1d;
    return r -> {
      int l = (r == null) ? initialLength : (r.getVoxels().getW() + 1);
      Grid<? extends SensingVoxel> body = RobotUtils
          .buildSensorizingFunction("uniform-ax+t+r-0.01")
          .apply(Grid.create(l, 2, (x, y) -> y > 0 || (x % 2 == 0)));
      return new Robot<>(
          new TimeFunctions(Grid.create(
              body.getW(),
              body.getH(),
              (x, y) -> (Double t) -> Math.sin(-2 * Math.PI * f * t + Math.PI * ((double) x / (double) body.getW()))
          )),
          body
      );
    };
  }


}
