package it.gromov.zpayments.update.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

import java.util.Collections;
import java.util.List;

@Getter
public final class GithubReleaseDto {

    @SerializedName("tag_name")
    private String tagName;

    private List<GithubAssetDto> assets;

    public List<GithubAssetDto> getAssets() {
        return assets == null ? Collections.emptyList() : assets;
    }
}
