package com.github.soil.datastructures.linkedlist;

import org.junit.Test;

/**
 * @author Trd
 * @date 2020-02-29 21:03
 */
public class LinkedListTest {
	private HeroNode node1 = new HeroNode(1, "大娃", "1娃来了");
	private HeroNode node2 = new HeroNode(2, "二娃", "2娃来了");
	private HeroNode node3 = new HeroNode(3, "三娃", "3娃来了");
	private HeroNode node4 = new HeroNode(4, "四娃", "4娃来了");
	private SingleLinkedList list = new SingleLinkedList();
	@Test
	public void fun1(){
		list.addTail(node1);
//		list.addTail(node2);
		list.addTail(node3);
		list.addTail(node4);
		list.showData();
	}
	@Test
	public void fun2(){
		list.addByOrder(node4);
	    list.addByOrder(node1);
	    list.addByOrder(node3);
	    list.addByOrder(node2);
	    list.showData();
	}
	@Test
	public void fun3(){
		list.addByOrder(node1);
		list.addByOrder(node4);
		list.addByOrder(node2);
//		list.addByOrder(node3);
		list.showData();
		System.out.println(list.updateNodeByno(new HeroNode(2,"aaa","bbb")));
		list.showData();
	}

	@Test
	public void deleteTest(){
		list.addByOrder(node1);
		list.addByOrder(node4);
		list.addByOrder(node2);
		list.addByOrder(node3);
		list.showData();
		System.out.println(list.deleteNodeByno(4));
		list.showData();
	}
}
