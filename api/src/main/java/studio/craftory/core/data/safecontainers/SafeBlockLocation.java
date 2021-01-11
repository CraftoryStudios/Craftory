package studio.craftory.core.data.safecontainers;

import com.google.gson.annotations.SerializedName;
import java.util.Objects;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NonNull;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.World;
import org.bukkit.block.Block;

@Data
@AllArgsConstructor
public class SafeBlockLocation {

  @NonNull
  @SerializedName(value = "world")
  private SafeWorld safeWorld;
  @NonNull
  private int x;
  @NonNull
  private int y;
  @NonNull
  private int z;

  public SafeBlockLocation(@NonNull Location location) {
    this(new SafeWorld(Objects.requireNonNull(location.getWorld())), location.getBlockX(), location.getBlockY(), location.getBlockZ());
  }

  public Optional<World> getWorld() {
    return safeWorld.getWorld();
  }

  public Optional<Location> getLocation() {
    return getWorld().map(world -> new Location(world, x, y, z));
  }

  public Optional<Block> getBlock() {
    return getLocation().map(Location::getBlock);
  }

  public Optional<Chunk> getChunk() {
    return getLocation().map(Location::getChunk);
  }

  public Integer getChunkX() {
    return (int) Math.ceil(x / 16f);
  }

  public Integer getChunkZ() {
    return (int) Math.ceil(z / 16f);
  }
}
