package studio.craftory.core.terrian.retro.population.ore;

import java.util.Random;
import java.util.Set;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.bukkit.Chunk;
import org.bukkit.Material;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.IntRange;
import studio.craftory.core.data.Vector3;

@Getter
@RequiredArgsConstructor
public abstract class Ore {

  protected final Class<? extends BaseCustomBlock> material;
  protected final Set<Material> replaceable;
  protected final IntRange amount;
  protected final IntRange height;
  protected CustomBlockAPI customBlockAPI;

  public void injectAPI(CustomBlockAPI customBlockAPI) {
    this.customBlockAPI = customBlockAPI;
  }


  public abstract void generate(Vector3 origin, Chunk chunk, Random random);
}
