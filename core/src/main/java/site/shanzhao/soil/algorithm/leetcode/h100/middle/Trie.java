package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 * <a href="https://leetcode.cn/problems/implement-trie-prefix-tree/?envType=study-plan-v2&envId=top-100-liked">
 *     实现Trie（前缀树）</a>
 *
 * 整体思路：
 * Trie是一种树形数据结构，用于高效地存储和检索字符串数据集中的键。
 * 这种数据结构的特点是：
 * 1. 根节点不包含字符，除根节点外的每个节点都包含一个字符
 * 2. 从根节点到某一节点的路径上的字符连接起来，为该节点对应的字符串
 * 3. 每个节点的所有子节点包含的字符都不相同
 *
 * 本实现具有以下功能：
 * - insert(word)：将单词插入到Trie中
 * - search(word)：检查Trie中是否存在完整的单词
 * - startsWith(prefix)：检查Trie中是否存在以给定前缀开头的单词
 *
 * 每个TrieNode节点包含：
 * - 一个布尔值word，表示到此节点为止是否构成一个完整单词
 * - 一个TrieNode数组children，表示该节点的所有可能子节点（这里假设只包含小写字母a-z）
 *
 * 时间复杂度：
 * - 所有操作的时间复杂度均为O(m)，其中m是单词/前缀的长度
 * 空间复杂度：
 * - O(n*26)，其中n是所有插入单词的字符总数，每个节点最多有26个子节点（小写字母）
 */
public class Trie {

    /**
     * Trie树的节点类
     * 每个节点包含一个布尔标记（表示是否为单词结尾）和子节点数组
     */
    private static class TrieNode {
        // 标记当前节点是否是一个单词的结尾
        public boolean word;
        // 子节点数组，每个索引对应一个小写字母（0-'a', 1-'b', ..., 25-'z'）
        public TrieNode[] children;

        // 构造函数，初始化子节点数组（26个小写字母）
        public TrieNode() {
            this.children = new TrieNode[26];
        }
    }

    // Trie树的根节点
    private TrieNode root;

    /**
     * 初始化Trie数据结构
     * 创建一个空的根节点
     */
    public Trie() {
        this.root = new TrieNode();
    }

    /**
     * 将单词插入Trie树
     * @param word 要插入的单词
     */
    public void insert(String word) {
        // 从根节点开始
        TrieNode cur = root;
        // 遍历单词的每个字符
        for (int i = 0; i < word.length(); i++) {
            // 计算当前字符在子节点数组中的索引
            int index = word.charAt(i) - 'a';
            // 如果当前字符的节点不存在，创建一个新节点
            if (cur.children[index] == null) {
                cur.children[index] = new TrieNode();
            }
            // 移动到下一个节点
            cur = cur.children[index];
        }
        // 标记单词的结束位置
        cur.word = true;
    }

    /**
     * 在Trie树中搜索完整单词
     * @param word 要搜索的单词
     * @return 如果单词存在于Trie树中返回true，否则返回false
     */
    public boolean search(String word) {
        // 从根节点开始
        TrieNode cur = root;
        // 遍历单词的每个字符
        for (int i = 0; i < word.length(); i++) {
            // 计算当前字符在子节点数组中的索引
            int index = word.charAt(i) - 'a';
            // 如果当前字符的节点不存在，单词不在Trie中
            if (cur.children[index] == null) {
                return false;
            }
            // 移动到下一个节点
            cur = cur.children[index];
        }
        // 检查最后一个节点是否标记为单词结尾
        return cur.word;
    }

    /**
     * 检查Trie树中是否存在以给定前缀开头的单词
     * @param prefix 要检查的前缀
     * @return 如果存在以该前缀开头的单词返回true，否则返回false
     */
    public boolean startsWith(String prefix) {
        // 从根节点开始
        TrieNode cur = root;
        // 遍历前缀的每个字符
        for (int i = 0; i < prefix.length(); i++) {
            // 计算当前字符在子节点数组中的索引
            int index = prefix.charAt(i) - 'a';
            // 如果当前字符的节点不存在，不存在该前缀
            if (cur.children[index] == null) {
                return false;
            }
            // 移动到下一个节点
            cur = cur.children[index];
        }
        // 如果能遍历完所有前缀字符，说明前缀存在
        return true;
    }
}
