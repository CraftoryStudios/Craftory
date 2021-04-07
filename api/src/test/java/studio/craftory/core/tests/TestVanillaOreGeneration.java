package studio.craftory.core.tests;

import java.util.HashSet;
import java.util.Optional;
import org.bukkit.Chunk;
import org.bukkit.Location;
import org.bukkit.Material;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.invocation.InvocationOnMock;
import org.mockito.junit.MockitoJUnitRunner;
import org.mockito.stubbing.Answer;
import studio.craftory.core.api.CustomBlockAPI;
import studio.craftory.core.blocks.templates.BaseCustomBlock;
import studio.craftory.core.data.IntRange;
import studio.craftory.core.terrian.retro.RetroGeneration;
import studio.craftory.core.terrian.retro.population.ore.VanillaOre;

@RunWith(MockitoJUnitRunner.class)
public class TestVanillaOreGeneration {

  @Mock
  CustomBlockAPI customBlockAPI;
  @Mock
  Chunk chunk;
  @InjectMocks
  RetroGeneration retroGeneration = new RetroGeneration();

  HashSet<Location> locations1 = new HashSet<>();


  @Before
  public void init() {
    //Setup Ore
    HashSet<Material> replaceable = new HashSet<>();
    replaceable.add(Material.STONE);
    VanillaOre ore = new VanillaOre(new BaseCustomBlock, replaceable, new IntRange(20,33), new IntRange(5, 60),
        new IntRange(3,10));
    retroGeneration.registerOre(ore);

    //Setup Chunk
    int x = 3;
    int z = 11;


    //Setup Return
    Mockito.when(customBlockAPI.placeCustomBlock(Mockito.any(), Mockito.any())).thenAnswer(new Answer<Object>() {
      @Override
      public Optional<Object> answer(InvocationOnMock invocation) {
        locations1.add(invocation.getArgument(0, org.bukkit.Location.class));
        return Optional.empty();
      }
    });
  }

  @Test
  public void testSameGenerationSpots() {

  }
}
