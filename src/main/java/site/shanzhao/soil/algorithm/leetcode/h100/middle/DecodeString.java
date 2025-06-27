package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayDeque;
import java.util.Deque;

/**
 * <a href="https://leetcode.cn/problems/decode-string/?envType=study-plan-v2&envId=top-100-liked">
 *     394. 字符串解码</a>
 *
 * 整体思路：
 * 1. 使用两个栈：
 *    - `countStack`：用于存储当前需要重复的次数。
 *    - `strStack`：用于存储当前已经解码的字符串。
 * 2. 遍历字符串中的每一个字符：
 *    - 如果遇到数字，则计算当前重复的次数（可能有多位数字）。
 *    - 如果遇到 `[`，则将当前的重复次数和已解码的字符串分别压入栈中，并重置计数器和字符串。
 *    - 如果遇到 `]`，则从栈中弹出重复次数和之前的字符串，将当前字符串重复指定次数后拼接到之前的字符串中。
 *    - 如果遇到字母，则直接拼接到当前字符串中。
 * 3. 最终返回解码后的字符串。
 *
 * 时间复杂度：O(n)，其中 n 是解码后字符串的长度。每个字符只会被处理一次。
 * 空间复杂度：O(n)，在最坏情况下，栈的深度可能达到字符串的长度。
 */
public class DecodeString {
    public String decodeString(String s) {
        // 用于存储重复次数的栈
        Deque<Integer> countStack = new ArrayDeque<>();
        // 用于存储已解码字符串的栈
        Deque<StringBuilder> strStack = new ArrayDeque<>();
        // 当前重复次数
        int count = 0;
        // 当前解码的字符串
        StringBuilder str = new StringBuilder();

        // 遍历字符串中的每一个字符
        for (int i = 0; i < s.length(); i++) {
            char iChar = s.charAt(i);

            // 如果遇到数字，计算当前重复次数
            if (Character.isDigit(iChar)) {
                count = count * 10 + (iChar - '0');
            }
            // 如果遇到 `[`，将当前重复次数和已解码字符串压入栈中，并重置计数器和字符串
            else if (iChar == '[') {
                countStack.push(count);
                strStack.push(str);
                count = 0;
                str = new StringBuilder();
            }
            // 如果遇到 `]`，从栈中弹出重复次数和之前的字符串，将当前字符串重复指定次数后拼接到之前的字符串中
            else if (iChar == ']') {
                StringBuilder sb = strStack.pop();
                int currentCount = countStack.pop();
                for (int j = 0; j < currentCount; j++) {
                    sb.append(str);
                }
                str = sb;
            }
            // 如果遇到字母，直接拼接到当前字符串中
            else {
                str.append(iChar);
            }
        }

        // 返回解码后的字符串
        return str.toString();
    }
}
