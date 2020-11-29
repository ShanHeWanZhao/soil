package com.github.soil.basis.util;

import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.RandomUtils;

import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author tanruidong
 * @date 2020/08/18 16:47
 */
public class MapCapacity {
    public static void main(String[] args) throws Exception {
        test_put();
    }
    public static void test_put() throws Exception {
        String ls = System.getProperty("line.separator");
        int capacity = 4677;
        int size = tableSizeFor(capacity);
        int count = Double.valueOf(size * 0.75).intValue() - 1;
        HashMap<String, String> map = new HashMap<>(capacity);
        List<Integer> list = new LinkedList<>();
        for (int i = 0; i < count; i++) {
            String key = RandomStringUtils.randomAlphabetic(RandomUtils.nextInt(4, 12));
            map.put(key, "unimportant value");
            addIndex(key,  list, size);
        }
        long use = list.stream().distinct().count();
        list.sort(Integer::compareTo);
        String out = String.format(
                "1、map中桶的总数量：[%s]%s" +
                "2、预计存放元素的个数为：[%s]%s" +
                "3、map中实际存放的元素个数：[%s]%s" +
                "4、map中已使用桶的数量：[%s]%s" +
                "5、map中桶的使用率：[%s]%s" +
                "6、各元素在桶的索引位排序：%s",
                size, ls,
                count, ls,
                map.size(), ls,
                use, ls,
                (float) (use * 100.0 / size) + "%", ls,
                list);
        System.out.println(out);
        Map<Integer, Integer> collect = list.stream()
                .collect(Collectors.toMap(Function.identity(), integer -> 1, (integer, integer2) -> integer + 1));
        List<Integer> repeatCount = collect.values().stream().sorted(Comparator.comparingInt(Integer::intValue).reversed()).collect(Collectors.toList());
        System.out.println("各使用的桶节点长度排序："+repeatCount);
//        collect.forEach((i1, i2) -> System.out.println(String.format("桶的索引为：[%s], 该节点长度：[%s]", i1, i2)));
    }

    private static void addIndex(String key, List<Integer> list, int size) {
        int h;
        int hash = (key == null) ? 0 : (h = key.hashCode()) ^ (h >>> 16);
        int index = (size - 1) & hash;
        list.add(index);
    }
    private static int tableSizeFor(int cap) {
        int n = cap - 1;
        n |= n >>> 1;
        n |= n >>> 2;
        n |= n >>> 4;
        n |= n >>> 8;
        n |= n >>> 16;
        return (n < 0) ? 1 : (n >= 1 << 30) ? 1 << 30 : n + 1;
    }

}
