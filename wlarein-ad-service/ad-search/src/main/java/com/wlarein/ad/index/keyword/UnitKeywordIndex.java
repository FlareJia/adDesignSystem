package com.wlarein.ad.index.keyword;

import com.wlarein.ad.index.IndexAware;
import com.wlarein.ad.utils.CommonUtils;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang.StringUtils;
import org.springframework.stereotype.Component;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentSkipListSet;
/**
 * 索引实现，实现IndexAware
 */
@Slf4j
@Component
public class UnitKeywordIndex implements IndexAware<String, Set<Long>> {
    // keyword到Unit的索引，倒排索引
    private static Map<String, Set<Long>> keywordUnitMap;
    // unit到keyword的索引，正向索引
    private static Map<Long, Set<String>> unitKeywordMap;

    static {
        keywordUnitMap = new ConcurrentHashMap<>();
        unitKeywordMap = new ConcurrentHashMap<>();
    }

    @Override
    public Set<Long> get(String key) {
        if(StringUtils.isEmpty(key)){
            return Collections.emptySet();
        }

        Set<Long> result = keywordUnitMap.get(key);
        if(result==null){
            return Collections.emptySet();
        }
        return result;
    }

    @Override
    public void add(String key, Set<Long> value) {
        log.info("UnitKeywordIndex, before add: {}", unitKeywordMap);
        // 同样，更新的时候需要线程安全
        // 更新倒排索引
        Set<Long> unitIdSet = CommonUtils.getorCreate(key, keywordUnitMap, ConcurrentSkipListSet::new);
        unitIdSet.addAll(value);

        // 更新正向索引
        for(Long unitId : value){
            Set<String> keywordSet = CommonUtils.getorCreate(unitId, unitKeywordMap, ConcurrentSkipListSet::new);
            keywordSet.add(key);
        }

        log.info("UnitKeywordIndex, after add: {}", unitKeywordMap);
    }

    @Override
    public void update(String key, Set<Long> value) {
        log.error("keyword index can not support update");
    }

    @Override
    public void delete(String key, Set<Long> value) {
        log.info("UnitKeyWordIndex, before delete: {}", unitKeywordMap);
        Set<Long> unitIds = CommonUtils.getorCreate(key, keywordUnitMap, ConcurrentSkipListSet::new);
        unitIds.removeAll(value);
        for(Long unitId: value){
            Set<String> keywordSet = CommonUtils.getorCreate(unitId, unitKeywordMap, ConcurrentSkipListSet::new);
            keywordSet.remove(key);
        }
        log.info("UnitKeyWordIndex, after delete: {}", unitKeywordMap);
    }
    // 匹配方法，推广单元和关键词的匹配
    public boolean match(Long unitId, List<String> keywords){
        if(unitKeywordMap.containsKey(unitId) && CollectionUtils.isNotEmpty(unitKeywordMap.get(unitId))){
            Set<String> unitKeywords = unitKeywordMap.get(unitId);
            // 当keywords 是 unitKeywords 的 子集合时返回true
            return CollectionUtils.isSubCollection(keywords, unitKeywords);
        }
        return false;
    }
}
