package com.github.soil.algorithm.leetcode.h100.middle;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

/**
 * <a href="https://leetcode.cn/problems/course-schedule/?envType=study-plan-v2&envId=top-100-liked">
 *     课程表</a>
 *
 * 这个问题本质上是判断有向图中是否存在环。如果课程之间的依赖关系形成了环，
 * 那么就无法完成所有课程。解决方案使用拓扑排序（Kahn算法）：
 * 1. 构建课程依赖关系的有向图（邻接表）和每个课程的入度数组
 * 2. 将所有入度为0的课程（没有前置要求的课程）加入队列作为起点
 * 3. 逐步"学习"队列中的课程，同时减少其后续课程的入度
 * 4. 当一个课程的入度变为0时，表示其所有前置课程已完成，将其加入队列
 * 5. 如果最终学习的课程数等于总课程数，说明可以完成所有课程；否则存在环，无法完成
 *
 * 时间复杂度：O(V+E)，其中V是课程数量，E是依赖关系的数量
 * 空间复杂度：O(V+E)，用于存储图结构
 */
public class CourseCanFinish {
    public boolean canFinish(int numCourses, int[][] prerequisites) {
        // 如果没有任何前置课程要求，则可以完成所有课程
        if (prerequisites.length == 0) {
            return true;
        }

        // 创建邻接表，indexCanUnlockCourses[i]表示学完课程i后可以解锁的所有课程列表
        List<List<Integer>> indexCanUnlockCourses = new ArrayList<>();
        for (int i = 0; i < numCourses; i++) {
            indexCanUnlockCourses.add(new ArrayList<>());
        }

        // 创建入度数组，indexCourseSides[i]表示课程i的入度（依赖的前置课程数量）
        int[] indexCourseSides = new int[numCourses];

        // 遍历前置课程要求，构建图的邻接表和入度数组
        // prerequisite[1]是前置课程，prerequisite[0]是后续课程
        for (int[] prerequisite : prerequisites) {
            indexCanUnlockCourses.get(prerequisite[1]).add(prerequisite[0]); // 学完前置课程后可以解锁的课程
            indexCourseSides[prerequisite[0]]++; // 增加后续课程的入度
        }

        // 创建队列，用于存放当前可以学习的课程（入度为0的课程）
        Queue<Integer> canLearnCourseQueue = new LinkedList<>();

        // 将所有入度为0的课程（没有前置课程要求的）加入队列作为起点
        for (int i = 0; i < indexCourseSides.length; i++) {
            if (indexCourseSides[i] == 0){
                canLearnCourseQueue.offer(i);
            }
        }

        // 记录已学习的课程数量
        int canLearnCourseCount = 0;

        // 开始拓扑排序
        while(!canLearnCourseQueue.isEmpty()){
            // 学习一门课程，计数器增加
            canLearnCourseCount++;

            // 从队列中取出一门可以学习的课程
            int course = canLearnCourseQueue.poll();

            // 遍历当前学完的课程能解锁的所有后续课程
            for (Integer unlockedCourse : indexCanUnlockCourses.get(course)) {
                // 减少后续课程的入度（表示完成了一个前置课程）
                // 如果入度变为0，表示所有前置课程已完成，可以学习该课程
                if (--indexCourseSides[unlockedCourse] == 0){
                    canLearnCourseQueue.offer(unlockedCourse);
                }
            }
        }

        // 判断是否能学完所有课程：已学课程数等于总课程数
        // 如果小于总课程数，说明存在循环依赖（有向图中的环），无法完成所有课程
        return canLearnCourseCount == numCourses;
    }
}
