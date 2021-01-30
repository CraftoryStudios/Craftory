package studio.craftory.core.executors;

import java.util.HashSet;
import java.util.Set;
import lombok.Getter;
import studio.craftory.core.blocks.templates.BaseCustomBlock;

public class TickGroup {
  int tick;

  @Getter
  private Set<BaseCustomBlock> tickables;

  public TickGroup(int tick) {
    this.tickables = new HashSet<>();
    this.tick = tick;
  }
}
