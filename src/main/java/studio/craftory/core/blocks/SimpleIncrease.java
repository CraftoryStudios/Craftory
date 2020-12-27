package studio.craftory.core.blocks;

import lombok.Getter;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;

public class SimpleIncrease implements Tickable {

  @Getter
  int testVar = 0;

  @SyncTickable(ticks = 1)
  public void doAction() {

    testVar++;
  }
}
