package studio.craftory.core.data;

import java.util.HashMap;
import java.util.Map;

public enum CraftoryDirection {
  NORTH((byte)0),
  EAST((byte)1),
  WEST((byte)2),
  SOUTH((byte)3),
  UP((byte)4),
  DOWN((byte)5);

  public final byte label;
  private static final Map<Byte, CraftoryDirection> BY_LABEL = new HashMap<>();

  static {
    for (CraftoryDirection e: values()) {
      BY_LABEL.put(e.label, e);
    }
  }

  private CraftoryDirection(byte label) {
    this.label = label;
  }

  public static CraftoryDirection valueOfLabel(byte label) {
    return BY_LABEL.get(label);
  }

  @Override
  public String toString() {
    return this.label+"";
  }
}
