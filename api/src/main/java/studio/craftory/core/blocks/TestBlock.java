package studio.craftory.core.blocks;

import lombok.Getter;
import studio.craftory.core.annotations.Persistent;
import studio.craftory.core.data.keys.CraftoryDataKey;
import studio.craftory.core.data.persitanceholders.DataHolder;
import studio.craftory.core.data.persitanceholders.PersistentDataHolder;
import studio.craftory.core.data.persitanceholders.VolatileDataHolder;
import studio.craftory.core.data.safecontainers.SafeBlockLocation;
import studio.craftory.core.data.safecontainers.SafeWorld;

public class TestBlock implements PersistentDataHolder, VolatileDataHolder {

  CraftoryDataKey dataKey = new CraftoryDataKey("CraftoryCore", "testKey", Integer.class);

  @Getter
  @Persistent(name = "location")
  private SafeBlockLocation safeBlockLocation;

  @Persistent()
  private DataHolder persistentData = new DataHolder();
  private DataHolder volatileData = new DataHolder();

  public TestBlock() {
    int i = 10;
    persistentData.set(dataKey, i);
    safeBlockLocation = new SafeBlockLocation(new SafeWorld("world"), 1, 1, 1);
  }

  @Override
  public DataHolder getPersistentData() {
    return persistentData;
  }

  @Override
  public DataHolder getVolatileData() {
    return volatileData;
  }
}
