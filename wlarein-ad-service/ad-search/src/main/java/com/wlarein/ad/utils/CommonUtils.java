package com.wlarein.ad.utils;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang.time.DateUtils;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.function.Supplier;

@Slf4j
public class CommonUtils {
    // 传进去一个map，若map的key不存在的情况下，就用factory返回一个新的对象
    public static <K, V> V getorCreate(K key, Map<K, V> map, Supplier<V> factory){
        // computeIfAbsent方法有两个参数，第一个参数是想要获取的map的key，获取不到的时候才会执行第二个参数指向的表达式
        return map.computeIfAbsent(key, k -> factory.get());
    }

    // 将字符串连接的方法
    public static String stringConcat(String... args){
        StringBuilder  result = new StringBuilder();
        for(String arg: args){
            result.append(arg);
            result.append("-");
        }
        result.deleteCharAt(result.length() - 1);
        return result.toString();
    }

    // Tue Jan 01 08:00:00 CST 2019
    // 该方法实现对日期的解析，将mysql中取出的String类型的日期解析为java的Date标准类型
    public static Date parseStringDate(String dateString){

        try{
            DateFormat dateFormat = new SimpleDateFormat(
                    // 星期几 月份 天 小时：分钟：秒 日期格式 年份
                    "EEE MMM dd HH:mm:ss zzz yyyy",
                    Locale.US
            );
            return DateUtils.addHours(dateFormat.parse(dateString), -8);

        }catch (ParseException ex){
            log.error("parseStringDate error: {}", dateString);
            return null;
        }
    }


}
