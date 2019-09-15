package com.wlarein.ad.mysql.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor

/**
 * 解析binlog文件（template.json的数据库）
 */
public class Template {

    private String database;
    private List<JsonTable> tableList;

}
