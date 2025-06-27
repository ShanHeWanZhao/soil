package site.shanzhao.soil.algorithm.leetcode.middle;

/**
 * 最长回文子串
 * @author tanruidong
 * @date 2022/05/03 11:22
 */
public class LongestPalindromicSubstring {

    public static void main(String[] args) {
        System.out.println(bruteForce("sas"));
        System.out.println(bruteForce("dadadadada"));
        System.out.println(bruteForce("babad"));
    }

    /**
     * 暴力解法，但会超出解题的时间限制
     */
    public static String bruteForce(String s){
        if (s.isEmpty()) return s;
        int length = s.length();
        int maxLength = 0;
        String result = null;
        for (int i = 0; i < length; i++){
            for (int j = i; j < length; j++){
                String substring = s.substring(i, j + 1);
                if (isPalindromic(substring)){
                    maxLength = Math.max(maxLength, j + 1 - i);
                    if (j + 1 - i == maxLength){
                        result = substring;
                    }
                }
            }
        }
        return result;
    }

    public static boolean isPalindromic(String s){
        int length = s.length();
        for (int i = 0; i < length / 2;i++ ){
            if (s.charAt(i) != s.charAt(length - 1 - i)){
                return false;
            }
        }
        return true;
    }
}
