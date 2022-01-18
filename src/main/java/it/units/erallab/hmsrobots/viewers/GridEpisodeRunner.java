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
package it.units.erallab.hmsrobots.viewers;

import it.units.erallab.hmsrobots.tasks.Task;
import it.units.erallab.hmsrobots.util.Grid;
import org.apache.commons.lang3.tuple.Pair;

import java.io.Flushable;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.logging.Level;
import java.util.logging.LogManager;
import java.util.logging.Logger;

/**
 * @author Eric Medvet <eric.medvet@gmail.com>
 */
public class GridEpisodeRunner<S> implements Runnable {

  static {
    try {
      LogManager.getLogManager().readConfiguration(GridEpisodeRunner.class.getClassLoader().getResourceAsStream("logging.properties"));
    } catch (IOException ex) {
      //ignore
    } catch (SecurityException ex) {
      //ignore
    }
  }

  private final Grid<Pair<String, S>> namedSolutionGrid;
  private final Task<S, ?> episode;

  private final GridSnapshotListener gridSnapshotListener;
  private final ExecutorService executor;

  private static final Logger L = Logger.getLogger(GridEpisodeRunner.class.getName());

  public GridEpisodeRunner(Grid<Pair<String, S>> namedSolutionGrid, Task<S, ?> episode, GridSnapshotListener gridSnapshotListener, ExecutorService executor) {
    this.namedSolutionGrid = namedSolutionGrid;
    this.episode = episode;
    this.executor = executor;
    this.gridSnapshotListener = gridSnapshotListener;
  }

  @Override
  public void run() {
    //start episodes
    List<Future<?>> results = new ArrayList<>();
    namedSolutionGrid.stream()
        .filter(p -> p.getValue() != null && p.getValue().getRight() != null)
        .forEach(entry -> {
          results.add(executor.submit(() -> {
            L.fine(String.format("Starting %s in position (%d,%d)", episode.getClass().getSimpleName(), entry.getX(), entry.getY()));
            Object outcome = episode.apply(entry.getValue().getRight(), gridSnapshotListener.listener(entry.getX(), entry.getY()));
            L.fine(String.format("Ended %s in position (%d,%d) with outcome %s", episode.getClass().getSimpleName(), entry.getX(), entry.getY(), outcome));
          }));
        });
    //wait for results
    for (Future<?> result : results) {
      try {
        result.get();
      } catch (InterruptedException | ExecutionException ex) {
        L.log(Level.SEVERE, String.format("Cannot obtain one result due to %s", ex), ex);
      }
    }
    //flush and write
    if (gridSnapshotListener instanceof Flushable) {
      try {
        L.finer(String.format("Flushing with %s", gridSnapshotListener.getClass().getSimpleName()));
        ((Flushable) gridSnapshotListener).flush();
        L.finer("Flushed");
      } catch (IOException e) {
        L.log(Level.SEVERE, String.format("Cannot flush video due to %s", e), e);
      }
    }
  }

}
