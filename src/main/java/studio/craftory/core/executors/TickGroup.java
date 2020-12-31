package studio.craftory.core.executors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import studio.craftory.core.executors.interfaces.Tickable;

public class TickGroup {
  int tick;
  public Queue<Tickable> tickables;

  public TickGroup(int tick) {
    this.tickables = new ConcurrentLinkedQueue<>();
    this.tick = tick;
  }
}
