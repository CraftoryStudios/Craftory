package studio.craftory.core.data;

import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public class RenderData {

  private Optional<String> northFacingModel;
  private Optional<String> southFacingModel;
  private Optional<String> eastFacingModel;
  private Optional<String> westFacingModel;
  private Optional<String> upFacingModel;
  private Optional<String> downFacingModel;
  private Optional<String> headModel;

  public static RenderDataBuilder builder() {
    return new RenderDataBuilder();
  }

  public static class RenderDataBuilder {
    private Optional<String> northFacingModel = Optional.empty();
    private Optional<String> southFacingModel = Optional.empty();
    private Optional<String> eastFacingModel = Optional.empty();
    private Optional<String> westFacingModel = Optional.empty();
    private Optional<String> upFacingModel = Optional.empty();
    private Optional<String> downFacingModel = Optional.empty();

    private Optional<String> headModel = Optional.empty();

    private RenderDataBuilder() {}

    public RenderDataBuilder northModel(String northFacingModel) {
      this.northFacingModel = Optional.ofNullable(northFacingModel);
      return this;
    }
    public RenderDataBuilder southModel(String southFacingModel) {
      this.southFacingModel = Optional.ofNullable(southFacingModel);
      return this;
    }
    public RenderDataBuilder eastModel(String eastFacingModel) {
      this.eastFacingModel = Optional.ofNullable(eastFacingModel);
      return this;
    }
    public RenderDataBuilder westModel(String westFacingModel) {
      this.westFacingModel = Optional.ofNullable(westFacingModel);
      return this;
    }
    public RenderDataBuilder upModel(String upFacingModel) {
      this.upFacingModel = Optional.ofNullable(upFacingModel);
      return this;
    }
    public RenderDataBuilder downModel(String northFacingModel) {
      this.northFacingModel = Optional.ofNullable(northFacingModel);
      return this;
    }

    public RenderDataBuilder headModel(String headModel) {
      this.headModel = Optional.ofNullable(headModel);
      return this;
    }

    public RenderData build() {
      return new RenderData(
          northFacingModel,
          southFacingModel,
          eastFacingModel,
          westFacingModel,
          upFacingModel,
          downFacingModel,
          headModel);
    }
  }
}
