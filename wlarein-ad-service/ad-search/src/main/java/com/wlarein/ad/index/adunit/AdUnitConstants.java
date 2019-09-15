package com.wlarein.ad.index.adunit;

public class AdUnitConstants {

    public static class POSITION_TYPE{
        // 二进制编排，可以用位或运算，加快运算速度
        public static final int KAIPING = 1;
        public static final int TIEPIAN = 2;
        public static final int TIEPIAN_MIDDLE = 4;
        public static final int TIEPIAN_PAUSE = 8;
        public static final int TIEPIAN_POST = 16;
    }
}
