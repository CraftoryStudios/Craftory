package studio.craftory.core.terrian.retro.population.ore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.containers.IntRange;
import studio.craftory.core.containers.Vector3;

@Getter
@RequiredArgsConstructor
public abstract class Ore {

  protected final Class<? extends BaseCustomBlock> material;
  protected final Set<Material> replaceable;
  protected final IntRange amount;
  protected final IntRange height;
  protected CustomBlockAPI customBlockAPI;

  protected Ore(Class<? extends BaseCustomBlock> material,  IntRange amount,
      IntRange height, Material... replaceable) {
    this.material = material;
    this.replaceable = new HashSet<>(Arrays.asList(replaceable));
    this.amount = amount;
    this.height = height;
  }

  public void injectAPI(CustomBlockAPI customBlockAPI) {
    this.customBlockAPI = customBlockAPI;
  }

  public abstract void generate(Vector3 origin, Chunk chunk, Random random);
}
