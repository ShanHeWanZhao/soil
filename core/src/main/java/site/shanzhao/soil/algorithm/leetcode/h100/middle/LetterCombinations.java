package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.List;

/**
 * <a href="https://leetcode.cn/problems/letter-combinations-of-a-phone-number/?envType=study-plan-v2&envId=top-100-liked">
 *     17. 电话号码的字母组合</a>
 *
 * 本题使用回溯算法解决，关键点如下：
 * 1. 建立数字到字母的映射关系（如按键2对应"abc"）
 * 2. 对输入的每个数字，尝试其对应的每个字母
 * 3. 使用递归深度优先搜索所有可能的组合
 * 4. 当组合长度等于输入数字长度时，将当前组合加入结果
 *
 * 算法特点：
 * - 使用字符数组path记录当前组合，优化性能
 * - 层次化递归，每层对应输入的一个数字
 * - 在每一层，尝试当前数字对应的所有可能字母
 *
 * 时间复杂度：O(4^n)，最坏情况下每个数字对应4个字母（如7和9）
 * 空间复杂度：O(n)，递归深度和结果存储的空间
 */
public class LetterCombinations {
    public List<String> letterCombinations(String digits) {
        List<String> result = new ArrayList<>();
        if (digits == null || digits.isEmpty()){
            return result;
        }
        // 创建固定长度的字符数组存储当前组合
        char[] path = new char[digits.length()];

        // 建立数字到字母的映射，索引对应电话按键数字
        char[][] numChars = new char[][] {
                {},                        // 0
                {},                        // 1
                { 'a', 'b', 'c' },         // 2
                { 'd', 'e', 'f' },         // 3
                { 'g', 'h', 'i' },         // 4
                { 'j', 'k', 'l' },         // 5
                { 'm', 'n', 'o' },         // 6
                { 'p', 'q', 'r', 's'},     // 7
                { 't', 'u', 'v' },         // 8
                { 'w', 'x', 'y', 'z'}      // 9
        };
        backtrace(digits, 0, numChars, path, result);
        return result;
    }

    private void backtrace(String digits, int index, char[][] numChars,char[] path, List<String> result){
        // 终止条件：已处理完所有数字
        if (index == digits.length()){
            result.add(new String(path));
            return;
        }

        // 获取当前数字对应的所有可能字母
        char[] numC = numChars[Integer.parseInt(digits.charAt(index) + "")];

        // 尝试当前数字的每个可能字母
        for (char c : numC) {
            // 在当前位置放入字母
            path[index] = c;
            // 递归处理下一个数字
            backtrace(digits, index + 1, numChars, path, result);
            // 注意：这里不需要显式回溯，因为下次循环会覆盖当前位置
        }
    }
}
