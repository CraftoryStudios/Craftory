package studio.craftory.core.blocks;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;

public class SimpleObject implements Tickable {

  public SimpleObject(int i) {
    this.increase = i;
  }

  int increase;

  @Getter
  int testVar = 0;

  @SyncTickable(ticks = 1)
  public void doAction() {
    testVar = testVar + increase;
  }
}
