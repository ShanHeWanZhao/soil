package com.github.soil.datastructures.queue;
import java.util.Scanner;

/**
 * @author Trd
 * @date 2020-02-29 19:59
 */
public class CircleArrayQueueTest {

	public static void main(String[] args) {
		// 创建一个队列
		CircleArrayQueue queue = new CircleArrayQueue(6);
		// 接收用户输入
		char key = ' ';
		Scanner scanner = new Scanner(System.in);//
		boolean loop = true;
		System.out.println("s(show): 显示队列");
		System.out.println("e(exit): 退出程序");
		System.out.println("a(addQueue): 添加数据到队列");
		System.out.println("g(getQueue): 从队列取出数据");
		System.out.println("h(head): 查看队列头的数据");
		System.out.println("l(length): 队列元素的个数");
		//输出一个菜单
		while (loop) {
			//接收一个字符
			key = scanner.next().charAt(0);
			switch (key) {
				case 's':
					System.out.println(queue.showQueue());
					break;
				// 添加数据
				case 'a':
					try {
						System.out.println("添加:  ");
						int value = scanner.nextInt();
						queue.addQueue(value);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}

					break;
				//取出数据
				case 'g':
					try {
						int res = queue.getQueue();
						System.out.printf("取出的数据是%d\n", res);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					break;
				//查看队列头的数据
				case 'h':
					try {
						int res = queue.showHead();
						System.out.printf("队列头的数据是%d\n", res);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
					break;
				// 队列元素的个数
				case 'l':
					System.out.println("队列有"+ queue.size()+"个元素");
					break;
				case 'e': //退出
					scanner.close();
					loop = false;
					break;
				default:
					break;
			}
		}

		System.out.println("程序退出~~");
	}
}
