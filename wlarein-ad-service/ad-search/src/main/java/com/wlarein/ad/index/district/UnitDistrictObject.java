package com.wlarein.ad.index.district;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 索引对象
 * 包含UnitDistrict的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitDistrictObject {
    private Long unitId;
    private String province;
    private String city;

    // <String, Set<Long>>
    // province-city 将省和市构造成一个对象
}
