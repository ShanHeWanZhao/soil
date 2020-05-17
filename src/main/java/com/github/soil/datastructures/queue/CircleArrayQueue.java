package com.github.soil.datastructures.queue;

/**
 * 环形数组队列。
 * 为了让头尾指针循环起来，核心就是取模（%）
 *
 * @author Trd
 * @date 2020-02-29 13:49
 */
public class CircleArrayQueue {
	private int[] arr;
	/**
	 * 容量
	 */
	private int capacity;
	/**
	 * 队列头（队列第一个元素的索引位置,初始为0）
	 */
	private int front;
	/**
	 * 队列尾（队列最后一个元素的后一个索引位置，初始为0）。
	 * 该变量指向的位置不能存放数据，是为了当rear=front时，队列只可能为空，不可能为满
	 */
	private int rear;

	public CircleArrayQueue(int capacity) {
		/*		加1是因为环形数组做队列时，队尾的数据并不会用来存储数据
		 	这样rear==front就表示队列为空，所以+1就是扩充到目标容量
		 */
		this.capacity = capacity + 1;
		this.arr = new int[capacity + 1];
	}


	public void addQueue(int a) {
		checkIsFull();
		arr[rear] = a;
		// rear后移，避免越界，所以取模
		rear = (rear + 1) % capacity;
	}

	public int getQueue() {
		checkIsEmpty();
		int value = arr[front];
		front = (front + 1) % capacity;
		return value;
	}

	public String showQueue() {
		StringBuilder data = new StringBuilder("数据内容为: ");
		for (int i = front; i < front + size(); i++) {
			// 避免i越界
			int value = arr[i % capacity];
			data.append("  "+value);
		}
		return data.toString();
	}

	public int showHead() {
		checkIsEmpty();
		return arr[front];
	}

	/**
	 * 剩余的元素个数
	 *
	 * @return
	 */
	public int size() {
		return (rear - front + capacity) % capacity;
	}

	/**
	 * 队列是否满了
	 *
	 * @return true代表已满
	 */
	public boolean isFull() {
		return (rear + 1) % capacity == front;
	}

	/**
	 * 队列是否为空
	 *
	 * @return true代表已空
	 */
	public boolean isEmpty() {
		return rear == front;
	}

	private void checkIsEmpty() {
		if (isEmpty()) {
			throw new RuntimeException("队列没元素，取个锤子啊你");
		}
	}

	private void checkIsFull() {
		if (isFull()) {
			throw new RuntimeException("队列满了，求求你别装了！！！");
		}
	}
}
