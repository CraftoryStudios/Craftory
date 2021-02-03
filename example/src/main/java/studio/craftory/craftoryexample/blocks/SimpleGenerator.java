package studio.craftory.craftoryexample.blocks;

import lombok.NonNull;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import studio.craftory.core.annotations.CustomBlock;
import studio.craftory.core.annotations.RenderData;
import studio.craftory.core.annotations.SyncTickable;
import studio.craftory.core.blocks.renders.Renderers;
import studio.craftory.core.blocks.templates.ComplexCustomBlock;
import studio.craftory.core.components.energy.EnergyOutput;
import studio.craftory.core.components.energy.EnergyOutput.EnergyOutputData;
import studio.craftory.core.components.energy.EnergyStorage;
import studio.craftory.core.components.energy.EnergyStorage.EnergyStorageData;
import studio.craftory.core.data.CraftoryDirection;

@CustomBlock(renders = {Renderers.BLOCK_STATE_RENDER, Renderers.ENTITY_SPAWNER_RENDER, Renderers.HEAD_RENDER})
@RenderData(
    northFacingModel = "assets/blocks/northexample", southFacingModel = "assets/blocks/southexample",
    eastFacingModel = "assets/blocks/eastexample", westFacingModel = "assets/blocks/westexample",
    upFacingModel = "assets/blocks/upexample", downFacingModel = "assets/blocks/downexample",
    headModel = "assets/blocks/headexample"
)
@EnergyOutputData(maxExtract = 100)
@EnergyStorageData(capacity = 100000)
public class SimpleGenerator extends ComplexCustomBlock implements EnergyOutput {

  public SimpleGenerator(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    super(location, facingDirection);
  }

  @SyncTickable(ticks = 5)
  public void generateEnergy() {
    increaseStoredEnergy(100);
  }

  @Override
  public void onPlayerClick(PlayerInteractEvent playerInteractEvent) {
    playerInteractEvent.getPlayer().sendMessage("Generate Energy: " + getEnergyStored());
    playerInteractEvent.getPlayer().sendMessage("Space Available: " + getFreeSpace());
  }
}
