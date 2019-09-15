package com.wlarein.ad.index.adunit;


import com.wlarein.ad.index.adPlan.AdPlanObject;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * 索引对象
 * 包含AdUnit的基本信息
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AdUnitObject {

    private Long unitId;
    private Integer unitStatus;
    private Integer positionType;
    private Long planId;

    private AdPlanObject adPlanObject;

    void update(AdUnitObject newObject){
        if(null != newObject.getUnitId()){
            this.unitId = newObject.getUnitId();
        }
        if(null != newObject.getUnitStatus()){
            this.unitStatus = newObject.getUnitStatus();
        }
        if(null != newObject.getPositionType()){
            this.positionType = newObject.getPositionType();
        }
        if(null != newObject.getPlanId()){
            this.planId = newObject.getPlanId();
        }
        if(null != newObject.getAdPlanObject()){
            this.adPlanObject = newObject.getAdPlanObject();
        }
    }

    // 判断是否为各种定义的类型
    private static boolean isKaiPing(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.KAIPING) > 0;
    }
    private static boolean isTiePian(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN) > 0;
    }
    private static boolean isTiePianMiddle(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_MIDDLE) > 0;
    }
    private static boolean isTiePianPause(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_PAUSE) > 0;
    }
    private static boolean isTiePianPost(int positionType){
        return (positionType & AdUnitConstants.POSITION_TYPE.TIEPIAN_POST) > 0;
    }

    // 一个总的方法
    public static boolean isAdSlotTypeOK(int adSlotType, int positionType){
        switch (adSlotType){
            case AdUnitConstants.POSITION_TYPE.KAIPING:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_MIDDLE:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_PAUSE:
                return isKaiPing(positionType);
            case AdUnitConstants.POSITION_TYPE.TIEPIAN_POST:
                return isKaiPing(positionType);
            default:
                return false;
        }
    }
}
