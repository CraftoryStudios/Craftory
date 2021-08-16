package studio.craftory.core.blocks.storage;

import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.containers.CraftoryDirection;

public class CraftoryDirectionDataType implements PersistentDataType<Byte, CraftoryDirection> {

  @Override
  public Class<Byte> getPrimitiveType() {
    return Byte.class;
  }

  @Override
  public Class<CraftoryDirection> getComplexType() {
    return CraftoryDirection.class;
  }

  @Override
  public Byte toPrimitive(CraftoryDirection complex, PersistentDataAdapterContext context) {
    return complex.label;
  }

  @Override
  public CraftoryDirection fromPrimitive(Byte primitive, PersistentDataAdapterContext context) {
    return CraftoryDirection.valueOfLabel(primitive);
  }
}
