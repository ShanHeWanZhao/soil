package com.github.soil.algorithm.leetcode.middle;

import java.util.*;

/**
 * <a href="https://leetcode.cn/problems/insert-delete-getrandom-o1">
 *     380. O(1) 时间插入、删除和获取随机元素</a>

 *
 * 思路：
 * 1. 使用组合数据结构：ArrayList + HashMap
 *    - ArrayList 存储实际元素，支持O(1)随机访问
 *    - HashMap 存储元素值到其在ArrayList中索引的映射，支持O(1)查找
 * 2. 插入操作：直接添加到列表末尾，并记录索引映射
 * 3. 删除操作：将要删除的元素与末尾元素交换，然后删除末尾，保持列表紧凑
 * 4. 随机访问：利用Random类生成随机索引访问ArrayList
 *
 * 时间复杂度：
 * - insert(): O(1)
 * - remove(): O(1)
 * - getRandom(): O(1)
 *
 * 空间复杂度：O(n)，用于存储n个元素
 */
public class RandomizedSet {

    private Random random;
    private List<Integer> list;
    private Map<Integer, Integer> map;

    public RandomizedSet() {
        this.random = new Random();
        this.list = new ArrayList<>();
        this.map = new HashMap<>();
    }

    public boolean insert(int val) {
        if (map.containsKey(val)) {
            return false;
        }
        list.add(val);
        map.put(val, list.size() - 1);
        return true;
    }

    /**
     * 重点在这 ，数组删除数据，就把最后一个数据提到要被删除的位置，再删除最后一个（这样改动最小）。
     * 保持数组的紧凑，同时还要更新被移动的数据在map中的索引
     */
    public boolean remove(int val) {
        if (!map.containsKey(val)){
            return false;
        }
        Integer index = map.remove(val);
        // 更新被移动数据的索引
        map.put(list.get(list.size() - 1), index);
        // 移动数据
        list.set(index, list.get(list.size() - 1));
        list.remove(list.size() - 1);
        // 删除目标值
        map.remove(val);
        return true;
    }

    public int getRandom() {
        int index = random.nextInt(list.size());
        return list.get(index);
    }
}
