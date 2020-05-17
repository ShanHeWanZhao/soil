package com.github.soil.datastructures.linkedlist;

/**
 * @author Trd
 * @date 2020-02-29 20:57
 */
public class SingleLinkedList {
	//  头结点，固定不变
	private HeroNode head = new HeroNode(0,"","");
	// 尾节点（初始就是头结点）
	private HeroNode tail = head;

	/**
	 * 末尾添加（无序）
	 * @param node
	 */
	public void addTail(HeroNode node){
		tail.next = node;
		tail = node;
	}

	/**
	 * 有序不重复添加
	 * @param nextNode
	 */
	public void addByOrder(HeroNode nextNode){
		// 添加的数据排序位是否重复的标记
		boolean flag = false;
		// 重点：lastNode记录的是要插入节点的前一个节点
		HeroNode lastNode = head;
		while (true){
			// 是否队尾判断
			if (lastNode.next == null){
				break;
			}
			// 重复数据判断
			if (lastNode.no == nextNode.no){
				flag = true;
				break;
			}
			// 排序比较（这里最好画个图，好理解点）
			if (lastNode.next.no > nextNode.no){
				break;
			}
			lastNode = lastNode.next;
		}
		if (flag){
			System.out.println("元素重复了，no为："+lastNode.no);
		}else{
			// 节点连接（这里有点绕）
			nextNode.next = lastNode.next;
			lastNode.next = nextNode;
		}
	}

	/**
	 * 根据HeroNode的no来更新节点
	 * @param newNode
	 * @return
	 */
	public boolean updateNodeByno(HeroNode newNode){
		// 是否存在能更新的节点标记
		boolean flag = false;
		HeroNode temp = head.next;
		while (true){
			// 尾节点判断
			if (temp == null){
				break;
			}
			// no相同判断
			if (temp.no == newNode.no){
				flag = true;
				break;
			}
			temp = temp.next;
		}
		// 找到了能更新的节点
		if (flag){
			temp.name = newNode.name;
			temp.nickname = newNode.nickname;
			return true;
		}
		System.out.println("未找到no为"+newNode.no+"的节点");
		return false;
	}

	/**
	 * 根据HeroNode的no值来删除对应的节点
	 * @param no
	 * @return 是否删除成功
	 */
	public boolean deleteNodeByno(int no){
		boolean flag = false;
		// lastNode为要删除节点的前一个节点
		HeroNode lastNode = head;
		// targetNode为准备删除的节点
		HeroNode targetNode = null;
		while (true){
			// 尾节点判断
			if (lastNode.next == null){
				break;
			}
			// 是否存在目标节点判断
			if (lastNode.next.no == no){
				targetNode = lastNode.next;
				flag = true;
				break;
			}
			lastNode = lastNode.next;
		}
		if (flag){
			lastNode.next = targetNode.next;
			return true;
		}
		System.out.println("未发现节点no为"+no+"的节点，无法删除");
		return false;
	}
	public void showData(){
		HeroNode temp = head.next;
		while (temp != null){
			System.out.println(temp);
			temp = temp.next;
		}
	}

	@Override
	public String toString() {
		return "SingleLinkedList{" +
				"head=" + head +
				", tail=" + tail +
				'}';
	}
}

