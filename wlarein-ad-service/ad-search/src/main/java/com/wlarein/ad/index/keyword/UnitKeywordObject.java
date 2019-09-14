package com.wlarein.ad.index.keyword;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 索引对象
 * 包含UnitKeyword的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitKeywordObject {
    private Long unitId;
    private String Keyword;
}
