package studio.craftory.core.terrian.retro.population.ore;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;
import org.bukkit.Chunk;
import org.bukkit.Material;
import org.bukkit.Tag;
import org.bukkit.block.Block;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.IntRange;
import studio.craftory.core.data.Vector3;

public class VanillaOre extends Ore{
  private final IntRange oreSizeRange;

  public VanillaOre(Class<? extends BaseCustomBlock> material,  IntRange amount,
      IntRange height, IntRange oreSizeRange, Material... replaceable) {
    super(material,amount, height, replaceable);
    this.oreSizeRange = oreSizeRange;
  }

  @Override
  public void generate(Vector3 location, Chunk chunk, Random random) {
    double size = oreSizeRange.getRandomInRange(random);

    double f = random.nextFloat() * Math.PI;
    float g = (float)size / 8.0F;

    double fS = Math.sin(f) * (double)g;
    double fC = Math.cos(f) * (double)g;

    double d1 = location.getX() + 8 + fS;
    double d2 = location.getX() + 8 - fS;
    double d3 = location.getZ() + 8 + fC;
    double d4 = location.getZ() + 8 - fC;

    double d5 = location.getY() + random.nextInt(3) - 2D;
    double d6 = location.getY() + random.nextInt(3) - 2D;

    for(int i = 0; i < size; i++) {
      double iFactor = i / size;

      double d10 = random.nextDouble() * size / 16.0D;
      double d11 = (Math.sin(Math.PI * iFactor) + 1.0) * d10 + 1.0;

      int xMin = (int)(Math.floor(d1 + (d2 - d1) * iFactor - d11 / 2.0D));
      int yMin = (int)(Math.floor(d5 + (d6 - d5) * iFactor - d11 / 2.0D));
      int zMin = (int)(Math.floor(d3 + (d4 - d3) * iFactor - d11 / 2.0D));

      int xMax = (int)(Math.floor(d1 + (d2 - d1) * iFactor + d11 / 2.0D));
      int yMax = (int)(Math.floor(d5 + (d6 - d5) * iFactor + d11 / 2.0D));
      int zMax = (int)(Math.floor(d3 + (d4 - d3) * iFactor + d11 / 2.0D));

      for(int x = xMin; x <= xMax; x++) {
        double d13 = (x + 0.5D - (d1 + (d2 - d1) * iFactor)) / (d11 / 2.0D);

        if(d13 * d13 < 1.0D) {
          for(int y = yMin; y <= yMax; y++) {
            double d14 = (y + 0.5D - (d5 + (d6 - d5) * iFactor)) / (d11 / 2.0D);
            if(d13 * d13 + d14 * d14 < 1.0D) {
              for(int z = zMin; z <= zMax; z++) {
                double d15 = (z + 0.5D - (d3 + (d4 - d3) * iFactor)) / (d11 / 2.0D);
                if(x > 15 || z > 15 || y > 255 || x < 0 || z < 0 || y < 0) continue;
                Block block = chunk.getBlock(x, y, z);
                if((d13 * d13 + d14 * d14 + d15 * d15 < 1.0D) && this.replaceable.contains(block.getType())) {
                  customBlockAPI.placeCustomBlock(block.getLocation(), material);
                }
              }
            }
          }
        }
      }
    }
  }
}
