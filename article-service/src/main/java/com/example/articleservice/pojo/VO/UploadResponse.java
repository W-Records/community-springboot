package com.example.articleservice.pojo.VO;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class UploadResponse {

    private int errno; // 注意：值是数字，不能是字符串

    @JsonInclude(JsonInclude.Include.NON_EMPTY)
    private DataContainer data;

    private String message;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class DataContainer {

        private String url; // 图片 src ，必须
        private String alt; // 图片描述文字，非必须
        private String href; // 图片的链接，非必须
    }

    /**
     * Factory method to create an instance representing a successful upload.
     *
     * @param url     图片src
     * @param alt     图片描述文字（可选）
     * @param href    图片链接（可选）
     * @return        成功的UploadResponse对象
     */
    public static UploadResponse success(String url, String alt, String href) {
        return UploadResponse.builder()
                .errno(0)
                .data(DataContainer.builder()
                        .url(url)
                        .alt(alt)
                        .href(href)
                        .build())
                .build();
    }

    /**
     * Factory method to create an instance representing a failed upload.
     *
     * @param message 失败信息
     * @return        失败的UploadResponse对象
     */
    public static UploadResponse failure(String message) {
        return UploadResponse.builder()
                .errno(1)
                .message(message)
                .build();
    }
}
