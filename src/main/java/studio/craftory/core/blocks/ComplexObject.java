package studio.craftory.core.blocks;

import java.util.ArrayList;
import java.util.concurrent.ThreadLocalRandom;
import lombok.Getter;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.executors.interfaces.Tickable;

public class ComplexObject implements Tickable {

  @Getter
  int testVar = 0;
  @Getter
  ArrayList<Integer> integers = new ArrayList<>();

  @SyncTickable(ticks = 1)
  public void doAction() {
    testVar++;
  }

  @SyncTickable(ticks = 2)
  public void doActionTwo() {
    testVar = (int) Math.round(Math.log(Math.acos(Math.sqrt(testVar) / 4 * 50)));
  }

  @SyncTickable(ticks = 4)
  public void doActionThree() {
    integers.add(ThreadLocalRandom.current().nextInt());
  }

  @SyncTickable(ticks = 8)
  public void doActionFour() {
    for (int i = 0; i < integers.size() - 1; i++) {
      for (int j = 1; j < integers.size() - 1; j++) {
        if (integers.get(j - 1) > integers.get(j)) {
          int temp = integers.get(j - 1);
          integers.set(j - 1, integers.get(j));
          integers.set(j, temp);
        }
      }
    }
  }
}
