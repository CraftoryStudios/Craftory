package studio.craftory.core.executors;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import studio.craftory.core.blocks.CustomBlock;

public class TickGroup {

  final int tick;

  @Getter
  private final Set<CustomBlock> tickables;

  public TickGroup(int tick) {
    this.tickables = new HashSet<>();
    this.tick = tick;
  }
}
