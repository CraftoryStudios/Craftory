package studio.craftory.core.blocks.storage.types;

import java.nio.charset.StandardCharsets;
import org.bukkit.Material;
import org.bukkit.inventory.ItemStack;
import org.bukkit.persistence.PersistentDataAdapterContext;
import org.bukkit.persistence.PersistentDataType;
import studio.craftory.core.Craftory;
import studio.craftory.core.blocks.components.fluid.CraftoryFluid;
import studio.craftory.core.containers.CraftoryDirection;

public class FluidDataType implements PersistentDataType<byte[], CraftoryFluid> {

  @Override
  public Class<byte[]> getPrimitiveType() {
    return byte[].class;
  }

  @Override
  public Class<CraftoryFluid> getComplexType() {
    return CraftoryFluid.class;
  }

  @Override
  public byte[] toPrimitive(CraftoryFluid complex, PersistentDataAdapterContext context) {
    return complex.toString().getBytes(StandardCharsets.UTF_8);
  }

  @Override
  public CraftoryFluid fromPrimitive(byte[] primitive, PersistentDataAdapterContext context) {
    String combined = new String(primitive);
    String[] segments = combined.split("|");
    // TODO Improve when fluid redone
    return new CraftoryFluid(Craftory.getInstance().getServer().getPluginManager().getPlugin(segments[0]), segments[1], segments[2],
        new ItemStack(Material.valueOf(segments[3])), Boolean.valueOf(segments[4]));
  }
}
