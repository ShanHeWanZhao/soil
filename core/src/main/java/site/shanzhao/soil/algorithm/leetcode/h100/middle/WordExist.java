package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/word-search/description/?envType=study-plan-v2&envId=top-100-liked">
 *     79. 单词搜索</a>
 *
 *
 * 思路：
 * 1. 遍历整个二维网格，对每个单元格作为起点，尝试匹配单词的第一个字符。
 * 2. 使用DFS从当前单元格开始，向上下左右四个方向递归搜索，尝试匹配单词的下一个字符。
 * 3. 在搜索过程中，如果当前单元格的字符与单词的当前字符不匹配，或者当前单元格已经访问过，则回溯。
 * 4. 如果成功匹配到单词的最后一个字符，则返回true。
 * 5. 如果遍历完所有可能的路径仍未找到匹配的单词，则返回false。
 *
 * 时间复杂度：O(M * N * 4^L)，其中M和N分别是网格的行数和列数，L是单词的长度。
 *   - 最坏情况下，每个单元格都需要进行DFS搜索，且每次DFS最多有4个方向可以选择。
 *   - 因此，时间复杂度为O(M * N * 4^L)。
 *
 * 空间复杂度：O(L)，其中L是单词的长度。
 *   - 空间复杂度主要取决于递归调用栈的深度，最坏情况下递归深度为L。
 */
public class WordExist {

    private boolean exist; // 用于标记是否找到匹配的单词

    /**
     * 主方法，用于在二维网格中搜索是否存在给定的单词。
     *
     * @param board 二维字符网格
     * @param word 要搜索的单词
     * @return 如果找到单词则返回true，否则返回false
     */
    public boolean exist(char[][] board, String word) {
        int[][] directions = new int[][] { { -1, 0 }, { 1, 0 }, { 0, -1 }, { 0, 1 } }; // 定义四个方向的移动
        for (int i = 0; i < board.length; i++) {
            for (int j = 0; j < board[i].length; j++) {
                if (exist) {
                    break; // 如果已经找到匹配的单词，则提前退出
                }
                backtrack(board, word, 0, i, j, directions); // 从当前单元格开始回溯搜索
            }
        }
        return exist;
    }

    /**
     * 回溯方法，用于递归搜索匹配的单词。
     *
     * @param board 二维字符网格
     * @param word 要搜索的单词
     * @param index 当前匹配的字符索引
     * @param row 当前单元格的行索引
     * @param col 当前单元格的列索引
     * @param directions 四个方向的移动
     */
    private void backtrack(char[][] board, String word, int index, int row, int col, int[][] directions) {
        if (exist || index == word.length()) {
            exist = true; // 如果已经找到匹配的单词或者匹配到最后一个字符，则标记为true
            return;
        }
        if (!isValid(board, row, col) || board[row][col] != word.charAt(index)) {
            return; // 如果当前单元格无效或者字符不匹配，则回溯
        }
        board[row][col] = '1'; // 标记当前单元格为已访问
        for (int[] direction : directions) {
            int nextRow = direction[0] + row; // 计算下一个单元格的行索引
            int nextCol = direction[1] + col; // 计算下一个单元格的列索引
            backtrack(board, word, index + 1, nextRow, nextCol, directions); // 递归搜索下一个字符
        }
        board[row][col] = word.charAt(index); // 恢复当前单元格的原始字符，以便其他路径可以访问
    }

    /**
     * 判断当前单元格是否在网格范围内。
     *
     * @param board 二维字符网格
     * @param row 当前单元格的行索引
     * @param col 当前单元格的列索引
     * @return 如果单元格在网格范围内则返回true，否则返回false
     */
    private boolean isValid(char[][] board, int row, int col) {
        return 0 <= row && row < board.length && 0 <= col && col < board[0].length;
    }
}