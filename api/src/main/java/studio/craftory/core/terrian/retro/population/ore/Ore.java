package studio.craftory.core.terrian.retro.population.ore;

import java.util.HashSet;
import java.util.Random;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.bukkit.Chunk;
import org.bukkit.Material;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.IntRange;
import studio.craftory.core.data.Vector3;

@Getter
@AllArgsConstructor
public abstract class Ore {

  protected final Class<? extends BaseCustomBlock> material;
  protected final HashSet<Material> replaceable;
  protected final IntRange amount;
  protected final IntRange height;


  public abstract void generate(Vector3 origin, Chunk chunk, Random random);
}
