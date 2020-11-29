package com.github.soil.datastructures.linkedlist;

/**
 * @author tanruidong
 * @date 2020-05-17 15:30
 */
class HeroNode{
	/**
	 * 编号（更新使用）
	 */
	public int no;
	/**
	 * 真名
	 */
	public String name;
	/**
	 * 昵称
	 */
	public String nickname;
	/**
	 * 下一个节点
	 */
	public HeroNode next;

	public HeroNode(int no, String name, String nickname) {
		this.no = no;
		this.name = name;
		this.nickname = nickname;
	}

	@Override
	public String toString() {
		return "HeroNode{" +
				"no=" + no +
				", name='" + name + '\'' +
				", nickname='" + nickname + '\'' +
				'}';
	}
}
