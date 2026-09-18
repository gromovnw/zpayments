package it.gromov.zpayments.update.dto;

import com.google.gson.annotations.SerializedName;
import lombok.Getter;

@Getter
public final class GithubAssetDto {

    private String name;

    @SerializedName("browser_download_url")
    private String browserDownloadUrl;
}
