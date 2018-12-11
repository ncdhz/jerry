package com.github.ncdhz.jerry.util;

import java.util.Random;

public class RandomUtil {

    private static Random random = new Random();

    /**
     *随机生成num位随机数
     */
    public static String getRandomNum(int num){
        String re = "";
        for (int i = 0; i < num; i++) {
            re += random.nextInt(10);
        }
        return re;
    }
    /**
     * 获取当前时间搓
     */
    public static String getCurrentTimeMillis(){
        return System.currentTimeMillis()+"";
    }

}
