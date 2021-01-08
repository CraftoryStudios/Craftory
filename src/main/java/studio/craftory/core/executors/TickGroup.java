package studio.craftory.core.executors;

import java.util.Queue;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;
import studio.craftory.core.executors.interfaces.Tickable;

public class TickGroup {
  int tick;

  @Getter
  private Queue<Tickable> tickables;

  public TickGroup(int tick) {
    this.tickables = new ConcurrentLinkedQueue<>();
    this.tick = tick;
  }
}
