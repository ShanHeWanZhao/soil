package site.shanzhao.soil.algorithm.leetcode.h100.middle;

/**
 *
 * <a href="https://leetcode.cn/problems/container-with-most-water/?envType=study-plan-v2&envId=top-100-liked">
 *     盛最多水的容器
 *  </a> <br/>
 *  两个指针，一个头一个尾，此时容器底最大，接下来随着指针向内移动，会造成容器的底变小，在这种情况下想要让容器盛水变多，就只有在容器的高上下功夫。
 *  那我们该如何决策哪个指针移动呢？
 *  我们能够发现不管是左指针向右移动一位，还是右指针向左移动一位，容器的底都是一样的，都比原来减少了 1。
 *  这种情况下我们想要让指针移动后的容器面积增大，就要使移动后的容器的高尽量大，所以我们选择指针所指的高较小的那个指针进行移动，
 *  这样我们就保留了容器较高的那条边，放弃了较小的那条边，以获得有更高的边的机会。
 */
public class MaxArea {

    public int maxArea(int[] height) {
        if (height == null || height.length < 2){
            return 0;
        }
        int left = 0;
        int right = height.length - 1;
        int area = 0;
        while (left < right) {
            int length = right - left;
            int high = Math.min(height[left], height[right]);
            area = Math.max(high * length, area);
            if (height[right] < height[left]){
                right--;
            }else{
                left++;
            }
        }
        return area;
    }
}
