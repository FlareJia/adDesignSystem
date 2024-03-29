package com.wlarein.ad.handler;


import com.alibaba.fastjson.JSON;
import com.wlarein.ad.dump.table.*;
import com.wlarein.ad.index.DataTable;
import com.wlarein.ad.index.IndexAware;
import com.wlarein.ad.index.adPlan.AdPlanIndex;
import com.wlarein.ad.index.adPlan.AdPlanObject;
import com.wlarein.ad.index.adunit.AdUnitIndex;
import com.wlarein.ad.index.adunit.AdUnitObject;
import com.wlarein.ad.index.creative.CreativeIndex;
import com.wlarein.ad.index.creative.CreativeObject;
import com.wlarein.ad.index.creativeUnit.CreativeUnitIndex;
import com.wlarein.ad.index.creativeUnit.CreativeUnitObject;
import com.wlarein.ad.index.district.UnitDistrictIndex;
import com.wlarein.ad.index.interest.UnitItIndex;
import com.wlarein.ad.index.keyword.UnitKeywordIndex;
import com.wlarein.ad.mysql.constant.OpType;
import com.wlarein.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;

/**
 * 1.索引之间存在着层级的划分，也就是依赖关系的划分
 * 2.加载全量索引其实是增量索引 "添加" 的一种特殊实现
 */
@Slf4j
public class AdLevelDataHandler {

    // 第一层级为用户
    // 第二层级不与其他索引之间存在着关联关系
    // 因为从文件中得到的为json数据，故参数1为AdplanTable
    public static void handleLevel2(AdPlanTable planTable, OpType type){
        AdPlanObject planObject = new AdPlanObject(
                planTable.getId(),
                planTable.getUserId(),
                planTable.getPlanStatus(),
                planTable.getStartDate(),
                planTable.getEndDate()
        );
        handleBinlogEvent(
                // 获取对应IndexAware，（of方法 索引服务的缓存？？？）
                DataTable.of(AdPlanIndex.class),
                planObject.getPlanId(),
                planObject,
                type
        );
    }

    public static void handleLevel2(AdCreativeTable creativeTable, OpType type){
        CreativeObject creativeObject = new CreativeObject(
                creativeTable.getAdId(),
                creativeTable.getName(),
                creativeTable.getType(),
                creativeTable.getMaterialType(),
                creativeTable.getHeight(),
                creativeTable.getWidth(),
                creativeTable.getAuditStatus(),
                creativeTable.getAdUrl()
        );
        handleBinlogEvent(
                DataTable.of(CreativeIndex.class),
                creativeObject.getAdId(),
                creativeObject,
                type
        );
    }

    public static void handleLevel3(AdUnitTable unitTable, OpType type){
        // 先获取AdPlanObject
        AdPlanObject adPlanObject = DataTable.of(AdPlanIndex.class).get(unitTable.getPlanId());

        if(null == adPlanObject){
            log.error("handleLevel3 found AdPlanObject error: {}", unitTable.getPlanId());
            return;
        }

        AdUnitObject unitObject = new AdUnitObject(
                unitTable.getUnitId(),
                unitTable.getUnitStatus(),
                unitTable.getPositionType(),
                unitTable.getPlanId(),
                adPlanObject
        );
        handleBinlogEvent(
                DataTable.of(AdUnitIndex.class),
                unitTable.getUnitId(),
                unitObject,
                type
        );

    }

    public static void handleLevel3(AdCreativeUnitTable creativeUnitTable, OpType type){

        if(type == OpType.UPDATE){
            log.error("CreativeunitIndex not support update");
            return;
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(creativeUnitTable.getUnitId());
        CreativeObject creativeObject = DataTable.of(
                CreativeIndex.class
        ).get(creativeUnitTable.getAdId());

        if(null == unitObject || null == creativeObject){
            log.error("AdCreativeUnitTable index error: {}", JSON.toJSONString(creativeUnitTable));
            return;
        }

        CreativeUnitObject creativeUnitObject = new CreativeUnitObject(
                creativeUnitTable.getAdId(),
                creativeUnitTable.getUnitId()
        );
        handleBinlogEvent(
                DataTable.of(CreativeUnitIndex.class),
                CommonUtils.stringConcat(creativeUnitObject.getAdId().toString(), creativeUnitObject.getUnitId().toString()),
                creativeUnitObject,
                type
        );
    }

    public static void handleLevel4(AdUnitDistrictTable unitDistrictTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("DistrictIndex not support update");
            return;
        }
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitDistrictTable.getUnitId());
        if(unitObject==null){
            log.error("AdUnitDistrictTable index error: {}", unitDistrictTable.getUnitId());
            return;
        }

        String key = CommonUtils.stringConcat(
                unitDistrictTable.getProvince(),
                unitDistrictTable.getCity()
        );
        Set<Long> value = new HashSet<>(
                Collections.singleton(unitDistrictTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitDistrictIndex.class),
                key,
                value,
                type
        );
    }

    public static void handleLevel4(AdUnitItTable unitItTable, OpType type){
        if(type == OpType.UPDATE){
            log.error("it index can not support update");
        }

        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(unitItTable.getUnitId());
        if(unitObject == null) {
            log.error("AdUnitItTable index error: {}", unitItTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(unitItTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitItIndex.class),
                unitItTable.getItTag(),
                value,
                type
        );
    }

    public static void handleLevel4(AdUnitKeywordTable keywordTable, OpType type){
        if(type==OpType.UPDATE){
            log.error("keyword index can not support update");
        }
        AdUnitObject unitObject = DataTable.of(
                AdUnitIndex.class
        ).get(keywordTable.getUnitId());
        if(unitObject == null){
            log.error("AdUnitKeywordTable index error: {}", keywordTable.getUnitId());
            return;
        }

        Set<Long> value = new HashSet<>(
                Collections.singleton(keywordTable.getUnitId())
        );
        handleBinlogEvent(
                DataTable.of(UnitKeywordIndex.class),
                keywordTable.getKeyword(),
                value,
                type
        );
    }

    // 索引对象需要一个键和一个值，故返回类型为K，V
    // 该方法用来处理增量索引的总方法
    private static <K, V> void handleBinlogEvent(IndexAware<K, V> index, K key, V value, OpType type){
        switch (type){
            case ADD:
                index.add(key, value);
                break;
            case UPDATE:
                index.update(key, value);
                break;
            case DELETE:
                index.delete(key, value);
                break;
            default:
                break;
        }
    }
}
