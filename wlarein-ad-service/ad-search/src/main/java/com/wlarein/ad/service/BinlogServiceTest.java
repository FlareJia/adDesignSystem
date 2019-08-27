package com.wlarein.ad.service;

import com.github.shyiko.mysql.binlog.BinaryLogClient;
import com.github.shyiko.mysql.binlog.event.DeleteRowsEventData;
import com.github.shyiko.mysql.binlog.event.EventData;
import com.github.shyiko.mysql.binlog.event.UpdateRowsEventData;
import com.github.shyiko.mysql.binlog.event.WriteRowsEventData;

public class BinlogServiceTest {
    /**
     * Write-----------
     * WriteRowsEventData{tableId=79, includedColumns={0, 1, 2, 3, 4, 5, 6, 7}, rows=[
     *     [10, 10, plan, 1, Tue Jan 01 08:00:00 CST 2019, Tue Jan 01 08:00:00 CST 2019, Tue Jan 01 08:00:00 CST 2019, Tue Jan 01 08:00:00 CST 2019]
     * ]}
     */
    //  Tue Jan 01 08:00:00 CST 2019

    public static void main(String[] args) throws Exception{
        // 构造BinaryLogClient，填充MySQL的地址信息
        BinaryLogClient client = new BinaryLogClient(
                "127.0.0.1",
                3306,
                "root",
                "root123456"
        );
        // 下面两句为设定读取Binlog的文件和位置
        // client.setBinlogFilename();
        // client.setBinlogPosition();

        // 给BinaryLogClient注册监听器，实现对Binlog的监听和解析
        // event就是监听到的Binlog变化信息
        client.registerEventListener(event -> {
            // 获取event中的data部分
            EventData data = event.getData();

            if(data instanceof UpdateRowsEventData){
                System.out.println("Update----------");
                System.out.println(data.toString());
            }
            else if (data instanceof WriteRowsEventData){
                System.out.println("Write-----------");
                System.out.println(data.toString());
            }
            else if (data instanceof DeleteRowsEventData){
                System.out.println("Delete-----------");
                System.out.println(data.toString());
            }
        });

        // 将自己伪装成Slave，连接到MySQL Master，开始监听
        client.connect();
    }
}
