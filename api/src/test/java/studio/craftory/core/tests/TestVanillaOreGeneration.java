package studio.craftory.core.tests;

import be.seeseemelk.mockbukkit.WorldMock;
import be.seeseemelk.mockbukkit.block.BlockMock;
import java.util.HashSet;
import java.util.Optional;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Answers;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.data.IntRange;
import studio.craftory.core.terrian.retro.RetroGeneration;
import studio.craftory.core.terrian.retro.population.ore.VanillaOre;

@ExtendWith(MockitoExtension.class)
class TestVanillaOreGeneration {

  @Mock
  CustomBlockAPI customBlockAPI;
  @Mock(answer = Answers.RETURNS_DEEP_STUBS)
  Chunk chunk;
  @InjectMocks
  RetroGeneration retroGeneration = new RetroGeneration();


  HashSet<Location> locations1 = new HashSet<>();
  WorldMock worldMock = new WorldMock();


  @BeforeEach
  public void init() {
    //Setup Ore
    HashSet<Material> replaceable = new HashSet<>();
    replaceable.add(Material.AIR);
    VanillaOre ore = new VanillaOre(TestCustomBlock.class, replaceable, new IntRange(20,33), new IntRange(5, 60),
        new IntRange(3,10));
    retroGeneration.registerOre(ore);

    //Setup Chunk
    Mockito.when(chunk.getX()).thenReturn(3);
    Mockito.when(chunk.getZ()).thenReturn(11);
    Mockito.when(chunk.getWorld().getSeed()).thenReturn(4462149151511762283l);
    Mockito.when(chunk.getBlock(Mockito.anyInt(), Mockito.anyInt(), Mockito.anyInt())).thenAnswer(invocation -> {
      int x = invocation.getArgument(0, Integer.class);
      int y = invocation.getArgument(1, Integer.class);
      int z = invocation.getArgument(1, Integer.class);

      return new BlockMock(new Location(worldMock, x, y, z));
    });

    //Setup Return
    Mockito.when(customBlockAPI.placeCustomBlock(Mockito.any(), Mockito.any())).thenAnswer(invocation -> {
      locations1.add(invocation.getArgument(0, Location.class));
      return Optional.empty();
    });
  }

  @Test
  @DisplayName("Vanilla Ore Generation - Test multiple runs on same seed produce same result")
  void testSameGenerationSpots() {
    Assertions.assertTrue(locations1.isEmpty());
    retroGeneration.populateOre(chunk);
    Assertions.assertFalse(locations1.isEmpty());

    HashSet<Location> locations2 = new HashSet<>();
    Assertions.assertTrue(locations2.isEmpty());

    locations2.addAll(locations1);
    locations1.clear();
    Assertions.assertTrue(locations1.isEmpty());
    Assertions.assertFalse(locations2.isEmpty());

    retroGeneration.populateOre(chunk);

    Assertions.assertEquals(locations1, locations2);
  }
}
