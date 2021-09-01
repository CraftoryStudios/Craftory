package studio.craftory.core.containers;

import java.util.HashMap;
import java.util.Map;
import org.bukkit.block.BlockFace;

public enum CraftoryDirection {
  NORTH(0),
  EAST(1),
  WEST(2),
  SOUTH(3),
  UP(4),
  DOWN(5);

  public final int label;
  private static final Map<Integer, CraftoryDirection> BY_LABEL = new HashMap<>();

  static {
    for (CraftoryDirection e: values()) {
      BY_LABEL.put(e.label, e);
    }
  }

  private CraftoryDirection(int label) {
    this.label = label;
  }

  public static CraftoryDirection valueOfLabel(int label) {
    return BY_LABEL.get(label);
  }

  public static CraftoryDirection getCraftoryDirection(BlockFace blockFace) {
    switch (blockFace) {
      case UP:
        return CraftoryDirection.UP;
      case DOWN:
        return CraftoryDirection.DOWN;
      case EAST:
        return CraftoryDirection.EAST;
      case WEST:
        return CraftoryDirection.WEST;
      case SOUTH:
        return CraftoryDirection.SOUTH;
      default:
        return CraftoryDirection.NORTH;
    }
  }

  @Override
  public String toString() {
    return this.label+"";
  }
}
