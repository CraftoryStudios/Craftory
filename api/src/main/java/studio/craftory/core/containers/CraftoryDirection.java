package studio.craftory.core.containers;

import java.util.HashMap;
import java.util.Map;

public enum CraftoryDirection {
  NORTH(0),
  EAST(1),
  WEST(2),
  SOUTH(3),
  UP(4),
  DOWN(5);

  private static final Map<Integer, CraftoryDirection> BY_LABEL = new HashMap<>();

  static {
    for (CraftoryDirection e : values()) {
      BY_LABEL.put(e.label, e);
    }
  }

  public final int label;

  CraftoryDirection(int label) {
    this.label = label;
  }

  public static CraftoryDirection valueOfLabel(int label) {
    return BY_LABEL.get(label);
  }

  @Override
  public String toString() {
    return this.label + "";
  }
}
