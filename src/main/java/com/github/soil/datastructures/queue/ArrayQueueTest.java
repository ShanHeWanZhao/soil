package com.github.soil.datastructures.queue;

import java.util.Scanner;

/**
 * @author Trd
 * @date 2020-02-27 21:26
 */
public class ArrayQueueTest {

	public static void main(String[] args) {
		//创建一个队列
		ArrayQueue queue = new ArrayQueue(3);
		char key = ' '; //接收用户输入
		Scanner scanner = new Scanner(System.in);//
		boolean loop = true;
		System.out.println("s(show): 显示队列");
		System.out.println("e(exit): 退出程序");
		System.out.println("a(addQueue): 添加数据到队列");
		System.out.println("g(getQueue): 从队列取出数据");
		System.out.println("h(head): 查看队列头的数据");
		//输出一个菜单
		while(loop) {
			key = scanner.next().charAt(0);//接收一个字符
			switch (key) {
				case 's':
					System.out.println(queue.showQueue());
					break;
				case 'a':
					try{
						System.out.println("添加: ");
						int value = scanner.nextInt();
						queue.addQueue(value);
					}catch(Exception e){
						System.out.println(e.getMessage());
					}

					break;
				case 'g': //取出数据
					try {
						int res = queue.getQueue();
						System.out.printf("取出的数据是%d\n", res);
					} catch (Exception e) {
						// TODO: handle exception
						System.out.println(e.getMessage());
					}
					break;
				case 'h': //查看队列头的数据
					try {
						int res = queue.showHead();
						System.out.printf("队列头的数据是%d\n", res);
					} catch (Exception e) {
						System.out.println(e.getMessage());
					}
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
