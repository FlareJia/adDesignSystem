package com.wlarein.ad.index.interest;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * 索引对象
 * 包含UnitIt的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UnitItObject {
     private Long unitId;
     private String itTag;
}
