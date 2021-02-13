package studio.craftory.core.tests;

import be.seeseemelk.mockbukkit.MockBukkit;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import studio.craftory.core.Craftory;

public class TestPluginClass {

  private static Craftory plugin;

  @BeforeAll
  public static void load() {
    MockBukkit.mock();
    plugin = MockBukkit.load(Craftory.class);
  }

  @AfterAll
  public static void unload() {
    MockBukkit.unmock();
  }

  @Test
  @DisplayName("Test plugin getters return a value")
  void testGetters() {
    Assertions.assertNotNull(Craftory.getInstance());
  }
}
