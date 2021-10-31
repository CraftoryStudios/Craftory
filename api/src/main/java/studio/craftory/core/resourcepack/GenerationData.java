package studio.craftory.core.resourcepack;

public class GenerationData {

  private int amountLeft;
  private String data;
  private String blockName;

  public GenerationData(int amountLeft, String data, String blockName) {
    this.amountLeft = amountLeft;
    this.data = data;
    this.blockName = blockName;
  }

  public int getAmountLeft() {return this.amountLeft;}

  public String getData() {return this.data;}

  public String getBlockName() {return this.blockName;}

  public GenerationData setAmountLeft(int amountLeft) {
    this.amountLeft = amountLeft;
    return this;
  }

  public GenerationData setData(String data) {
    this.data = data;
    return this;
  }

  public GenerationData setBlockName(String blockName) {
    this.blockName = blockName;
    return this;
  }
}
