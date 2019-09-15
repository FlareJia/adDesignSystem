package com.wlarein.ad.mysql.dto;

import com.wlarein.ad.mysql.constant.OpType;
import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Supplier;

/**
 * 对模版文件的java对象进行解析
 */
@Data
public class ParseTemplate {

    private String database;

    private Map<String, TableTemplate> tableTemplateMap = new HashMap<>();

    public static ParseTemplate parse(Template _template){

        ParseTemplate template = new ParseTemplate();
        template.setDatabase(_template.getDatabase());

        for (JsonTable table : _template.getTableList()){
            String name = table.getTableName();
            Integer level = table.getLevel();

            // 每一个表都是一个tableTemplate
            TableTemplate tableTemplate = new TableTemplate();
            tableTemplate.setTableName(name);
            tableTemplate.setLevel(level.toString());
            // 将tableTemplate放到template中
            template.tableTemplateMap.put(name, tableTemplate);

            // 遍历操作类型对应的列
            Map<OpType, List<String>> opTypeFieldSetMap = tableTemplate.getOpTypeFieldSetMap();

            for(JsonTable.Column column : table.getInsert()){
                getAndCreateIfNedd(OpType.ADD, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }

            for(JsonTable.Column column : table.getUpdate()){
                getAndCreateIfNedd(OpType.UPDATE, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }
            for(JsonTable.Column column : table.getDelete()){
                getAndCreateIfNedd(OpType.DELETE, opTypeFieldSetMap, ArrayList::new).add(column.getColumn());
            }
        }
        return template;
    }

    private static <T, R> R getAndCreateIfNedd(T key, Map<T, R> map, Supplier<R> factory){
        return map.computeIfAbsent(key, k -> factory.get());
    }
}
