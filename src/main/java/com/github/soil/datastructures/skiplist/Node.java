package com.github.soil.datastructures.skiplist;

import lombok.Data;

/**
 * @author tanruidong
 * @since 2024/03/26 15:48
 */
@Data
public class Node {
    public static final int MAX_LEVEL = 16;

    private int data = -1;
    private Node[] forwards = new Node[MAX_LEVEL];
    private int maxLevel = 0;
}
