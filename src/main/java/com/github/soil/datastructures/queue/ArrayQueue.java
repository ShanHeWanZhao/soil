package com.github.soil.datastructures.queue;
/**
 * 非环形数组队列
 * 该实现的方法：
 *		（1）添加
 *		（2）删除
 *	缺陷：
 *		（1）数组不能循坏使用
 * @author Trd
 * @date 2020-02-29 13:52
 */
public class ArrayQueue{
	
	private int[] arr;
	/**
	 * 	 最大容量
	 */
	private int maxSize;
	/**
	 * 	 队列头（第一个元素的前一个索引值，初始化为-1）
	 */
	private int front;
	/**
	 * 	队列尾（队列最后一个元素的索引值，初始化为-1）
	 */
	private int rear;

	public ArrayQueue(int maxSize){
		this.maxSize = maxSize;
		this.arr = new int[maxSize];
		this.front = -1;
		this.rear = -1;
	}

	public void addQueue(int a){
		checkIsFull();
		rear++;
		arr[rear] = a;
	}
	public int getQueue(){
		checkIsEmpty();
		return arr[++front];
	}
	public String showQueue(){
		StringBuilder builder = new StringBuilder("队列的元素有: ");
		for (int i = front + 1; i <= rear;i++){
			builder.append(arr[i]+"  ");
		}
		return builder.toString();
	}
	public int showHead(){
		checkIsEmpty();
		return arr[front+1];
	}

	/**
	 * 队列是否满了
	 * @return true代表已满
	 */
	public boolean isFull(){
		return rear == maxSize - 1;
	}

	/**
	 * 队列是否为空
	 * @return true代表已空
	 */
	public boolean isEmpty(){
		return front == rear;
	}
	private void checkIsEmpty(){
		if (isEmpty()){
			throw new RuntimeException("队列没元素，取个锤子啊你");
		}
	}
	private void checkIsFull(){
		if (isFull()){
			throw new RuntimeException("队列满了，求求你别装了！！！");
		}
	}
}
