package com.jhhc.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Random;

/**
 * @author xiaojiang
 * @date 2021/1/11 10:11
 *  随机造错误场景
 */
public class RandomFault {

    private static List<Integer> bases = new ArrayList<Integer>(100);

    static {
        for (int i = 0; i <= 99; i++) {
            bases.add(i); // [1-100]
        }
    }

    /**
     * 获取是否返回错误场景
     * @param rate 错误概率（0-100）
     * @return
     */
    public static boolean getFault(int rate) {
        if (rate > 100 || rate < 0) {
            return false;
        }
        List<Integer> faultList = bases.subList(0, rate);
        Random random = new Random();
        int t = random.nextInt(99);
        return faultList.contains(t);
    }

    public static void main(String[] args) {
        int t = 1;
        for (int i = 0; i < 100; i++) {
            if (getFault(100))
                System.out.println(t++);
        }

    }
}
