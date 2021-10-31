package studio.craftory.core.terrian.retro.population.ore;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Random;
import java.util.Set;
import org.bukkit.Chunk;
import org.bukkit.Material;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.CustomBlock;
import studio.craftory.core.containers.IntRange;
import studio.craftory.core.containers.Vector3;

public abstract class Ore {

  protected final Class<? extends CustomBlock> material;
  protected final Set<Material> replaceable;
  protected final IntRange amount;
  protected final IntRange height;
  protected CustomBlockAPI customBlockAPI;

  protected Ore(Class<? extends CustomBlock> material,  IntRange amount,
      IntRange height, Material... replaceable) {
    this.material = material;
    this.replaceable = new HashSet<>(Arrays.asList(replaceable));
    this.amount = amount;
    this.height = height;
  }

  public Ore(Class<? extends CustomBlock> material, Set<Material> replaceable, IntRange amount, IntRange height) {
    this.material = material;
    this.replaceable = replaceable;
    this.amount = amount;
    this.height = height;
  }

  public void injectAPI(CustomBlockAPI customBlockAPI) {
    this.customBlockAPI = customBlockAPI;
  }

  public abstract void generate(Vector3 origin, Chunk chunk, Random random);

  public Class<? extends CustomBlock> getMaterial() {return this.material;}

  public Set<Material> getReplaceable() {return this.replaceable;}

  public IntRange getAmount() {return this.amount;}

  public IntRange getHeight() {return this.height;}

  public CustomBlockAPI getCustomBlockAPI() {return this.customBlockAPI;}
}
