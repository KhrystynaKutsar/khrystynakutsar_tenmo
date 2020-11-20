package com.techelevator.tenmo.dao;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import com.techelevator.tenmo.model.InsufficientAmountException;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.WrongIdException;

public interface TransfersDAO {

	
	//Transfers detailsTransfersBasedOnId(long transferIdDetails);
	List<Transfers> allTransfers(String userName);
	List<Transfers> listAllPendingTransfers(String userName);
	void addBucks(int id, BigDecimal transferAmount, String userName)throws InsufficientAmountException, WrongIdException;
	void postToTransfers(int id, BigDecimal transferAmount, String userName);
	Transfers detailsTransfersBasedOnId(long transferIdDetails, String userName) throws Exception;
}

