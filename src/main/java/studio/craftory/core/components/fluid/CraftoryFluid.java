package studio.craftory.core.components.fluid;

import java.util.Optional;
import lombok.NonNull;
import lombok.Value;
import org.bukkit.inventory.ItemStack;
import org.bukkit.plugin.Plugin;
import studio.craftory.core.data.CraftoryKey;

@Value
public class CraftoryFluid {

  CraftoryKey id;
  String displayName;
  ItemStack representingItem;
  boolean gaseous;

  @NonNull
  public CraftoryFluid(Plugin plugin, String internalName, String displayName, ItemStack representingItem,
      boolean gaseous) {
    this.id = new CraftoryKey(plugin, internalName);
    this.displayName = displayName;
    this.representingItem = representingItem;
    this.gaseous = gaseous;
  }

  public Optional<Plugin> getRegisteringPlugin() {
   return this.id.getPlugin();
  }

  public String getInternalName() {
    return this.id.getName();
  }

  @Override
  public boolean equals(Object object) {
    if (!(object instanceof CraftoryFluid))
      return false;
    CraftoryFluid fluid = (CraftoryFluid) object;
    return getId().equals(fluid.getId());
  }

  @Override
  public int hashCode() {
    return id.toString().hashCode();
  }

}
