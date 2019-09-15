package com.wlarein.ad.sender;

import com.wlarein.ad.mysql.dto.MySqlRowData;

/**
 * 投递数据接口
 */
public interface ISender {
    void sender(MySqlRowData rowData);
}
