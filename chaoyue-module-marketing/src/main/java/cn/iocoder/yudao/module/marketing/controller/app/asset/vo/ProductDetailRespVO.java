package cn.iocoder.yudao.module.marketing.controller.app.asset.vo;

import lombok.Data;

import java.util.Map;

@Data
public class ProductDetailRespVO {
    private Long id;
    private String name;
    private String status;
    private Long currentVersionId;
    private Integer currentVersion;
    private String publicScope;
    private String facts;
    private Map<String, String> factDisplay;
}
