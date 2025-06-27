package site.shanzhao.soil.algorithm.leetcode.h100.middle;

import site.shanzhao.soil.algorithm.leetcode.editor.cn.TreeNode;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <a href="http://erleetcode.cn/problems/binary-tree-right-side-view/description/?envType=study-plan-v2&envId=top-100-liked">
 *     二叉树的右视图</a>
 *
 *     层序遍历，根据当前层的节点数量来收集最后一个节点值即可
 */
public class RightSideView {
    public List<Integer> rightSideView(TreeNode root) {
        List<Integer> result = new ArrayList();
        if ( root == null){
            return result;
        }
        Queue<TreeNode> queue = new LinkedList();
        queue.add(root);
        while (!queue.isEmpty()){
            int curLevelSize = queue.size();
            for (int i = 0; i < curLevelSize; i++){
                TreeNode cur = queue.poll();
                if (i == curLevelSize - 1){
                    result.add(cur.val);
                }
                if (cur.left != null){
                    queue.offer(cur.left);
                }
                if (cur.right != null){
                    queue.offer(cur.right);
                }
            }
        }
        return result;
    }
}
