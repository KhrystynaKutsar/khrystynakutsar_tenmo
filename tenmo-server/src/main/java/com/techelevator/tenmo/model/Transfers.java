package com.techelevator.tenmo.model;

import java.math.BigDecimal;

public class Transfers {

	private long id;
	private Integer typeId;
	private Integer StatusId;
	private Integer accountFrom;
	private Integer accountTo;
	private BigDecimal amount;
	private String userName;
	private String fromUser;
	private String toUser;
	private String transferType;
	private String transferStatus;
	
	
	
	public String getFromUser() {
		return fromUser;
	}
	public void setFromUser(String fromUser) {
		this.fromUser = fromUser;
	}
	public String getToUser() {
		return toUser;
	}
	public void setToUser(String toUser) {
		this.toUser = toUser;
	}
	public String getTransferType() {
		return transferType;
	}
	public void setTransferType(String transferType) {
		this.transferType = transferType;
	}
	public String getTransferStatus() {
		return transferStatus;
	}
	public void setTransferStatus(String transferStatus) {
		this.transferStatus = transferStatus;
	}
	public long getId() {
		return id;
	}
	public void setId(long id) {
		this.id = id;
	}
	public Integer getTypeId() {
		return typeId;
	}
	public void setTypeId(Integer typeId) {
		this.typeId = typeId;
	}
	public Integer getStatusId() {
		return StatusId;
	}
	public void setStatusId(Integer statusId) {
		StatusId = statusId;
	}
	public Integer getAccountFrom() {
		return accountFrom;
	}
	public void setAccountFrom(Integer accountFrom) {
		this.accountFrom = accountFrom;
	}
	public Integer getAccountTo() {
		return accountTo;
	}
	public void setAccountTo(Integer accountTo) {
		this.accountTo = accountTo;
	}
	public BigDecimal getAmount() {
		return amount;
	}
	public void setAmount(BigDecimal amount) {
		this.amount = amount;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
}
