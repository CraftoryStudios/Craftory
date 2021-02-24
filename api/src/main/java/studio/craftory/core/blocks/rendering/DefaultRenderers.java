package studio.craftory.core.blocks.rendering;

public enum DefaultRenderers {
  BLOCK_STATE_RENDER("b"),
  TRANSPARENT_BLOCK_STATE_RENDER("t"),
  ENTITY_SPAWNER_RENDER("e"),
  HEAD_RENDER("h");

  public final String value;

  DefaultRenderers(String value) { this.value = value;}
}
