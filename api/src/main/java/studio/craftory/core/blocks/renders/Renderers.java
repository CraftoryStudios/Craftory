package studio.craftory.core.blocks.renders;

public enum Renderers {
  BLOCK_STATE_RENDER("b"),
  TRANSPARENT_BLOCK_STATE_RENDER("t"),
  ENTITY_SPAWNER_RENDER("e"),
  HEAD_RENDER("h");

  public final String value;

  Renderers(String value) { this.value = value;}
}
