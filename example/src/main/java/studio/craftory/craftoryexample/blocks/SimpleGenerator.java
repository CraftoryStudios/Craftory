package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import studio.craftory.core.annotations.CustomBlock;
import studio.craftory.core.annotations.DefaultRendersData;
import studio.craftory.core.annotations.Tickable;
import studio.craftory.core.blocks.renders.Renderers;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.components.energy.EnergyOutput;
import studio.craftory.core.components.energy.EnergyOutput.EnergyOutputData;
import studio.craftory.core.components.energy.EnergyStorage.EnergyStorageData;
import studio.craftory.core.data.CraftoryDirection;

@EnergyOutputData(maxExtract = 100)
@EnergyStorageData(capacity = 1000000)
public class SimpleGenerator extends ComplexCustomBlock implements EnergyOutput {

  public SimpleGenerator(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

  @Tickable(ticks = 2)
  public void generateEnergy() {
    increaseStoredEnergy(100);
  }

  @Override
  public void onPlayerClick(PlayerInteractEvent playerInteractEvent) {
    playerInteractEvent.getPlayer().sendMessage("Generate Energy: " + getEnergyStored());
    playerInteractEvent.getPlayer().sendMessage("Space Available: " + getFreeSpace());
  }
}
