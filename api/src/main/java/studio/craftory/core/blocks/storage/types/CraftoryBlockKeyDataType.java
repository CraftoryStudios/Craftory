package studio.craftory.core.blocks.storage.types;

import java.nio.charset.StandardCharsets;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.containers.keys.CraftoryBlockKey;

public class CraftoryBlockKeyDataType implements PersistentDataType<byte[], CraftoryBlockKey> {

  @Override
  public Class<byte[]> getPrimitiveType() {
    return byte[].class;
  }

  @Override
  public Class<CraftoryBlockKey> getComplexType() {
    return CraftoryBlockKey.class;
  }

  @Override
  public byte[] toPrimitive(CraftoryBlockKey complex, PersistentDataAdapterContext context) {
    return complex.toString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public CraftoryBlockKey fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
    return Craftory.blockRegistry().getBlockKey(new String(primitive)).get();
  }
}
