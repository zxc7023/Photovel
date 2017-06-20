package com.vo;

public class Friend {
	private User friend1;
	private User friend2;
	private int friend_status;

	public User getFriend1() {
		return friend1;
	}
	public void setFriend1(User friend1) {
		this.friend1 = friend1;
	}
	public User getFriend2() {
		return friend2;
	}
	public void setFriend2(User friend2) {
		this.friend2 = friend2;
	}
	public int getFriend_status() {
		return friend_status;
	}
	public void setFriend_status(int friend_status) {
		this.friend_status = friend_status;
	}
	@Override
	public String toString() {
		return "Friend [friend1=" + friend1 + ", friend2=" + friend2 + ", friend_status=" + friend_status + "]";
	}
	
}