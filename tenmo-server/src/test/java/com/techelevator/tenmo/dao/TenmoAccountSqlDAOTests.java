package com.techelevator.tenmo.dao;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNotEquals;

import java.math.BigDecimal;
import java.sql.SQLException;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import javax.activation.DataSource;

import org.junit.After;
import org.junit.AfterClass;
import org.junit.Assert;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.FixMethodOrder;
import org.junit.runners.MethodSorters;
import org.springframework.dao.support.DaoSupport;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.datasource.SingleConnectionDataSource;


import com.techelevator.tenmo.dao.AccountSqlDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.User;

public class TenmoAccountSqlDAOTests {
	private static SingleConnectionDataSource dataSource;
	private AccountSqlDAO dao;
	
	@BeforeClass
	public static void setupDataSource() {
		dataSource = new SingleConnectionDataSource();
		dataSource.setUrl("jdbc:postgresql://localhost:5432/tenmo");
		dataSource.setUsername("postgres");
		dataSource.setPassword("postgres1");
		
		dataSource.setAutoCommit(false);
	}

	@AfterClass
	public static void destoryDataSource() {
		dataSource.destroy();
	} 
	
	@Before
	public void setup() {
		String sqlTruncateTable = "TRUNCATE TABLE users, accounts CASCADE ";
		
		String sqlInsertUser = "INSERT INTO users (user_id, username, password_hash) "+
				   				"VALUES(1, 'testAccount', 'testPassword')";
		String sqlInsertAccount = "INSERT INTO accounts (account_id, user_id, balance) "+
								   "VALUES (1, 1, 1015.00)";
	
		
		JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
		
		jdbcTemplate.update(sqlTruncateTable);
		jdbcTemplate.update(sqlInsertUser); 
		jdbcTemplate.update(sqlInsertAccount);
		
		

		dao = new AccountSqlDAO(dataSource);
	}
	
	@After 
	public void rollback() throws SQLException {  
		dataSource.getConnection().rollback();	
		}
	
	@Test
	public void getBalance_returns_balance() {
		//Arrange
		Account theAccount = getAccount(1L, 1, (new BigDecimal("1015.00")));
		User theUser = getUser(1L, "testAccount", "testPassword");
		
		
		//Act
		 BigDecimal accountFake = dao.findBalanceByUserName(theUser.getUsername());
		
		
		//Assert
		assertNotNull(accountFake);
		assertEquals(theAccount.getBalance(), accountFake);
		
		
		
	}
	
	private Account getAccount(Long accountId, int userId, BigDecimal balance) {
		Account theAccount = new Account();
		
		theAccount.setAccountId(accountId);
		theAccount.setUserID(userId);
		theAccount.setBalance(balance);
		
		
		return theAccount;
	}
	
	private User getUser(Long id, String userName, String password) {
		User theUser = new User();
		
		theUser.setId(id);
		theUser.setUsername(userName);
		theUser.setPassword(password);
		
		return theUser;
	}
	
	
}
