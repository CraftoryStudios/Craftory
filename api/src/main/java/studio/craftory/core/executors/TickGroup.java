package studio.craftory.core.executors;

import java.util.HashSet;
import java.util.Queue;
import java.util.Set;
import java.util.concurrent.ConcurrentLinkedQueue;
import lombok.Getter;
import studio.craftory.core.executors.interfaces.Tickable;

public class TickGroup {
  int tick;

  @Getter
  private Set<Tickable> tickables;

  public TickGroup(int tick) {
    this.tickables = new HashSet<>();
    this.tick = tick;
  }
}
