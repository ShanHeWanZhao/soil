package com.github.soil.util;


import java.util.HashMap;
import java.util.Map;

/**
 * @author tanruidong
 * @date 2020-05-28 22:07
 */
public class HashMapTest {
    public static void main(String[] args) {
        Map<String, Object> map = new HashMap<>(4);
        for (int i = 50; i <= 80; i++){
            map.put(""+i, 12);
        }

        map.put("11", 12);
        map.put("13", 12);
        map.put("14", 12);
        map.put("15", 12);
        map.put("16", 12);
        map.put("17", 12);
        map.put("18", 12);
        map.put("19", 12);
        map.put("20", 12);
        map.put("21", 12);
        map.put("22", 12);
        map.put("23", 12);
        map.put("23", 15);
        map.put("24", 12);
        map.put("25", 12);
        map.put("26", 12);
        map.put("27", 12);
        map.put("28", 12);
        map.put("29", 12);
        System.out.println(map);
    }

}
