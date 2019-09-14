package com.wlarein.ad.index.creativeUnit;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 索引对象
 * 包含CreativeUnit的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class CreativeUnitObject {
    private Long adId;
    private Long unitId;

    // key：adId-unitId 结合的key，可以唯一确定creative

}
