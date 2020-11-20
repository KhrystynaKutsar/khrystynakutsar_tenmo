package com.techelevator.tenmo.dao;

import java.beans.Statement;
import java.math.BigDecimal;
import java.security.Principal;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import javax.sql.DataSource;


import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.InsufficientAmountException;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.WrongIdException;

@Component
public class TransfersSqlDAO implements TransfersDAO {
	private AccountDAO accountDAO;
	private JdbcTemplate jdbcTemplate;
	private UserDAO userDAO;
	private DataSource dataSource;

	public TransfersSqlDAO(JdbcTemplate jdbcTemplate, AccountDAO accountDAO, UserDAO userDAO, DataSource dataSource) {
		this.jdbcTemplate = jdbcTemplate;
		this.accountDAO = accountDAO;
		this.userDAO = userDAO;
		this.dataSource = dataSource;
	}

	

	@Override
	public List<Transfers> allTransfers(String userName) {
		  
		    List<Transfers> transfers = new ArrayList<>();
		    String sql = "SELECT transfers.transfer_id, (SELECT username FROM users WHERE transfers.account_from = users.user_id )AS from_user, " + 
		    		"(SELECT username FROM users WHERE transfers.account_to = users.user_id )AS to_user, " + 
		    		"transfers.amount  FROM transfers " + 
		    		"INNER JOIN accounts ON transfers.account_from = accounts.account_id OR transfers.account_to = accounts.account_id " + 
		    		"INNER JOIN users ON accounts.user_id = users.user_id " + 
		    		"INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " + 
		    		"INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " + 
		    		"WHERE users.username = ? AND transfers.transfer_status_id != 1";
		    SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userName);
		    while(results.next()) {
		      Transfers theTransfer = mapRowToTransfers(results);
		      transfers.add(theTransfer);
		    } 
		    return transfers;
		  } 
	@Override
	public Transfers detailsTransfersBasedOnId(long transferIdDetails, String userName) throws Exception  {
		Transfers transfers = null;
		List<Transfers> myTransfersList = allTransfers(userName);
		
			for(Transfers t : myTransfersList) {
				long thisID = t.getId();

				if ( transferIdDetails == thisID) {
		
		    String sql = "SELECT  transfers.transfer_id, " + 
		    		"(SELECT username FROM users WHERE transfers.account_from = users.user_id )AS from_user, " + 
		    		"(SELECT username FROM users WHERE transfers.account_to = users.user_id )AS to_user, " + 
		    		"transfer_types.transfer_type_desc AS transfer_type, transfer_statuses.transfer_status_desc AS transfer_status, transfers.amount  FROM transfers " + 
		    		"INNER JOIN accounts ON transfers.account_from = accounts.account_id OR transfers.account_to = accounts.account_id " + 
		    		"INNER JOIN users ON accounts.user_id = users.user_id " + 
		    		"INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " + 
		    		"INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " + 
		    		"WHERE transfers.transfer_id = ? AND transfers.transfer_status_id != 1";
		    SqlRowSet results = jdbcTemplate.queryForRowSet(sql, transferIdDetails);
				
		    while(results.next()) {
		      transfers = mapRowToTransfersById(results);
		      return transfers;
		    	}
		    }
		} 
		   throw new Exception();
	 }
			

	@Override
	public List<Transfers> listAllPendingTransfers(String userName) {
		 List<Transfers> transfers = new ArrayList<>();
		    String sql = "SELECT transfers.transfer_id, (SELECT username FROM users WHERE transfers.account_from = users.user_id )AS from_user, " +
		    			 "(SELECT username FROM users WHERE transfers.account_to = users.user_id )AS to_user, " +
		    			 "transfers.amount  FROM transfers " +
		    			 "INNER JOIN accounts ON transfers.account_from = accounts.account_id OR transfers.account_to = accounts.account_id " +
		    			 "INNER JOIN users ON accounts.user_id = users.user_id " +
		    			 "INNER JOIN transfer_types ON transfers.transfer_type_id = transfer_types.transfer_type_id " +
		    			 "INNER JOIN transfer_statuses ON transfers.transfer_status_id = transfer_statuses.transfer_status_id " +
		    			 "WHERE users.username = ? AND transfers.transfer_status_id = 1";
		  
		    SqlRowSet results = jdbcTemplate.queryForRowSet(sql, userName);
		    while(results.next()) {
		      Transfers pendingTransfer = mapRowToTransfers(results);
		      transfers.add(pendingTransfer);
		    }
		    return transfers;
	}

	private Transfers mapRowToTransfers(SqlRowSet results) {
		Transfers theTransfers = new Transfers();
		
		theTransfers.setId(results.getLong("transfer_id"));
		theTransfers.setFromUser(results.getString("from_user"));
		theTransfers.setToUser(results.getString("to_user"));
		theTransfers.setAmount(results.getBigDecimal("amount")); 
		return theTransfers;
		}
	private Transfers mapRowToTransfersById(SqlRowSet results) {
		Transfers theTransfers = new Transfers();	
		theTransfers.setId(results.getLong("transfer_id"));
		theTransfers.setFromUser(results.getString("from_user"));
		theTransfers.setToUser(results.getString("to_user"));
		theTransfers.setTransferType(results.getString("transfer_type"));
		theTransfers.setTransferStatus(results.getString("transfer_status"));
		theTransfers.setAmount(results.getBigDecimal("amount")); 
		return theTransfers;
	}

	@Override
	public void addBucks(int id, BigDecimal amount, String userName) throws InsufficientAmountException, WrongIdException{

		int userId = userDAO.findIdByUsername(userName);  
		
		BigDecimal userBalance = accountDAO.findBalanceByUserName(userName);
		
		List<User> list = userDAO.findAll();
		List<Integer> idList = new ArrayList<>();
		
		for(int i = 0; i<list.size(); i++) {
			int idInt = Integer.valueOf((list.get(i).getId().toString()));
			idList.add(idInt);
		}
		

		if (userId != id && idList.contains(id)) {
		

			if (userBalance.compareTo(amount) >= 0) {  
			  
					String sqlAdd = "UPDATE accounts SET balance = balance + ? WHERE user_id = ?";
					jdbcTemplate.update(sqlAdd, amount, id);
					String sqlSubtract = "UPDATE accounts SET balance = balance - ? WHERE user_id = (SELECT user_id FROM users WHERE username = ?)";
					jdbcTemplate.update(sqlSubtract, amount, userName);

				} else {
			 
					throw new InsufficientAmountException();
					}
		
				
			} else {
		throw new WrongIdException();
			}
			
	}
	
	
	@Override
	public void postToTransfers(int id, BigDecimal amount, String userName) {

		int userId = userDAO.findIdByUsername(userName);

		String sql = "INSERT INTO transfers(transfer_type_id, transfer_status_id, account_from, account_to, amount) VALUES (2, 2, ?, ?, ?)";
		jdbcTemplate.update(sql, userId, id, amount);

	}

}