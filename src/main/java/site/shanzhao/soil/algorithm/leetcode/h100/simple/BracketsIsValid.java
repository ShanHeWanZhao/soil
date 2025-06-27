package site.shanzhao.soil.algorithm.leetcode.h100.simple;

import java.util.*;

/**
 * <a href="https://leetcode.cn/problems/valid-parentheses/?envType=study-plan-v2&envId=top-100-liked">
 *     20. 有效的括号</a>
 *     把括号想象成代码块的括号。进入代码块（左括号）则入栈，离开代码块（右括号）则出栈。

 * 整体思路：
 * 1. 使用一个哈希表来存储括号的匹配关系，键为左括号，值为对应的右括号。
 * 2. 使用一个栈来存储遇到的左括号。
 * 3. 遍历字符串中的每一个字符：
 *    - 如果是左括号，则将其压入栈中。
 *    - 如果是右括号，则检查栈是否为空，或者栈顶的左括号是否与当前右括号匹配。
 *      - 如果不匹配，则返回 false。
 *      - 如果匹配，则将栈顶的左括号弹出。
 * 4. 最后检查栈是否为空，如果为空则说明所有括号都匹配成功，返回 true；否则返回 false。
 *
 * 时间复杂度：O(n)，其中 n 是字符串的长度。每个字符只会被压入和弹出栈一次。
 * 空间复杂度：O(n)，在最坏情况下，栈的大小可能会达到字符串长度的一半（例如全是左括号的情况）。
 */
public class BracketsIsValid {
    public boolean bracketsIsValid(String s) {
        Map<Character, Character> map = new HashMap<>();
        map.put('{', '}');
        map.put('[', ']');
        map.put('(', ')');

        Deque<Character> stack = new ArrayDeque<>();
        for (int i = 0;i < s.length(); i++){
            char sChar = s.charAt(i);
            if (map.containsKey(sChar)){ // 左括号入栈
                stack.push(sChar);
            }else{ // 一定是右括号
                if (stack.isEmpty() || sChar != map.get(stack.pop())){ // 如果没有左括号了 或 匹配的不是对应的左括号，则返回false
                    return false;
                }
            }
        }
        return stack.isEmpty();
    }
}
