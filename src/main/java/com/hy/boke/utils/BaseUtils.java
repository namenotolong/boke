package com.hy.boke.utils;

import java.util.Random;

public class BaseUtils {
    public static String getRandomCode(int length){
        char[] chars = {'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i',
                'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z'};
        char[] nums = {'1', '2', '3', '4', '5', '6', '7', '8', '9', '0'};
        boolean flag = true;
        StringBuffer sb = new StringBuffer(length);
        Random random = new Random();
        for (int i = 0; i < length; i++) {
            if (flag){
                sb.append(chars[random.nextInt(chars.length)]);
                flag = false;
            } else {
                sb.append(nums[random.nextInt(nums.length)]);
                flag = true;
            }
        }
        return sb.toString();
    }
}
