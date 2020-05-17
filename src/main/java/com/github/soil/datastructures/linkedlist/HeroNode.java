package com.github.soil.datastructures.linkedlist;

/**
 * @author tanruidong
 * @date 2020-05-17 15:30
 */
class HeroNode{
	public int no;
	public String name;
	public String nickname;
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
