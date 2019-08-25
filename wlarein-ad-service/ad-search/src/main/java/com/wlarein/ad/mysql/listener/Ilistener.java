package com.wlarein.ad.mysql.listener;

import com.wlarein.ad.mysql.dto.BinlogRowData;

public interface Ilistener {

    void register();

    void onEvent(BinlogRowData eventData);
}
