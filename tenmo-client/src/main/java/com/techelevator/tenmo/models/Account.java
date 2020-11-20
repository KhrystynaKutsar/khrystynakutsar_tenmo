package com.techelevator.tenmo.models;

import java.math.BigDecimal;

public class Account {


	private Long accountId;
	private int userId;
	private BigDecimal balance;
	
	public Account() {}	
	
	
	public Long getAccountId() {
		return accountId;
	}
	public void setAccountId(Long accountId) {
		this.accountId = accountId;
	}
	public int getUserID() {
		return userId;
	}
	public void setUserID(int userId) {
		this.userId = userId;
	}
	public BigDecimal getBalance() {
		return balance;
	}
	public void setBalance(BigDecimal balance) {
		this.balance = balance;
	}
	
	
}
