package io.github.gonalez.znpcs.skin;

import com.google.auto.value.AutoValue;
import com.google.gson.annotations.SerializedName;
import java.util.UUID;
import javax.annotation.Nullable;
import javax.annotation.concurrent.Immutable;

/** Request to generate skin from URL using Mine Skin API. */
@AutoValue
@Immutable
public abstract class MineSkinGenerateRequest {

  MineSkinGenerateRequest() {}

  public enum SkinVariant {
    @SerializedName("classic")
    CLASSIC,
    @SerializedName("slim")
    SLIM,
    @SerializedName("unknown")
    UNKNOWN
  }

  public abstract SkinVariant skinVariant();

  public abstract String name();

  public enum SkinVisibility {
    @SerializedName("public")
    PUBLIC,
    @SerializedName("unlisted")
    UNLISTED,
    @SerializedName("private")
    PRIVATE
  }

  public abstract SkinVisibility visibility();

  public abstract @Nullable UUID cape();

  // URL to a PNG image. Also supports data URLs (base64-encoded images).
  public abstract String url();

  public static Builder newBuilder() {
    return new AutoValue_MineSkinGenerateRequest.Builder()
      .setSkinVariant(SkinVariant.CLASSIC)
      .setName("My Example Skin")
      .setVisibility(SkinVisibility.PUBLIC)
      .setCape(null);
  }

  /** Builder for {@link MineSkinGenerateRequest}. */
  @AutoValue.Builder
  public abstract static class Builder {

    Builder() {}

    public abstract Builder setSkinVariant(SkinVariant skinVariant);

    public abstract Builder setName(String name);

    public abstract Builder setVisibility(SkinVisibility visibility);

    public abstract Builder setCape(@Nullable UUID cape);

    public abstract Builder setUrl(String url);

    public abstract MineSkinGenerateRequest build();
  }
}
