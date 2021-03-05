package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import studio.craftory.core.annotations.Tickable;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.components.energy.EnergyOutput;
import studio.craftory.core.components.energy.EnergyOutput.EnergyOutputData;
import studio.craftory.core.components.energy.EnergyStorage.EnergyStorageData;
import studio.craftory.core.data.CraftoryDirection;


public class CopperOre extends ComplexCustomBlock {

  public CopperOre(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }
}
