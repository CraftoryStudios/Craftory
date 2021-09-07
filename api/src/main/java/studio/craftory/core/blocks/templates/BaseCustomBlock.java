package studio.craftory.core.blocks.templates;

import lombok.Getter;
import lombok.NonNull;
import lombok.Setter;
import org.bukkit.Location;
import org.bukkit.event.player.PlayerInteractEvent;
import studio.craftory.core.containers.CraftoryDirection;

public abstract class BaseCustomBlock {

  @Getter
  private final Location location;

  @Getter
  @Setter
  private CraftoryDirection facingDirection;


  protected BaseCustomBlock(@NonNull Location location, @NonNull CraftoryDirection facingDirection) {
    this.location = location;
    this.facingDirection = facingDirection;
  }


  public void renderCustomBlock() {

  }

  public void onPlayerClick(PlayerInteractEvent playerInteractEvent) {

  }

}
