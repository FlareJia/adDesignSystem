package com.wlarein.ad.sender;

import com.wlarein.ad.mysql.dto.MySqlRowData;

public interface ISender {
    void sender(MySqlRowData rowData);
}
