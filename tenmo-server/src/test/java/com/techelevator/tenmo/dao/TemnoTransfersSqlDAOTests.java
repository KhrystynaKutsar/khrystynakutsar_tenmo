package com.techelevator.tenmo.dao;


	import static org.junit.Assert.assertEquals;
	import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertTrue;
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
import com.techelevator.tenmo.model.InsufficientAmountException;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.WrongIdException;

	public class TemnoTransfersSqlDAOTests {
		private static SingleConnectionDataSource dataSource;
		private TransfersSqlDAO dao;
		private AccountSqlDAO daoAccount;
		private UserSqlDAO daoUser;
		
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
		//transfer status: 1 - pending, 2 - approved, 3 - rejected
		//transfer types: 1 - request, 2 - send
		
		
		 
		@Before
		public void setup() {
			String sqlTruncateTable = "TRUNCATE TABLE transfers,users, accounts";
			
			String sqlInsertUser = "INSERT INTO users (user_id, username, password_hash) "+
	   								"VALUES(1, 'testAccount', 'testPassword'), (2, 'testAccount2', 'testPassword2'), (3, 'testAccount3', 'testPassword3')";
			String sqlInsertAccount = "INSERT INTO accounts (account_id, user_id, balance) "+
									   "VALUES (1, 1, 1015.00), (2, 2, 2005.00), (3, 3, 3000.00)";
			String sqlInserttransfer = "INSERT INTO transfers (transfer_id, transfer_type_id, transfer_status_id, account_from, account_to, amount) "+
	   									"VALUES(1, 1, 1, 1, 2, 15.00 ), (2, 1, 2, 1, 2, 15.00 ), (3, 2, 2, 2, 3, 20.00 ), (4, 1, 3, 3, 2, 30.00 ), (5, 1, 3, 2, 3, 40.00 )";
		
			
			JdbcTemplate jdbcTemplate = new JdbcTemplate(dataSource);
			
			jdbcTemplate.update(sqlTruncateTable);
			jdbcTemplate.update(sqlInsertUser);
			jdbcTemplate.update(sqlInsertAccount);
			jdbcTemplate.update(sqlInserttransfer);
			 
			

			dao = new TransfersSqlDAO(jdbcTemplate, null, null, dataSource);
		
		}
		
		@After
		public void rollback() throws SQLException {
			dataSource.getConnection().rollback();	
			}
		
		@Test
		public void allTransfers_returns_list_of_transfers_for_user() {
			//Arrange
			List<Transfers> testList = new ArrayList<Transfers>();
			Account theAccount = getAccount(1L, 1, (new BigDecimal("1015.00")));
			User theUser = getUser(1L, "testAccount");
			Transfers theTransfer = getTransfers(2L, "testAccount", "testAccount2", (new BigDecimal("15.00")));
			testList.add(theTransfer);
			
			//Act
			 List<Transfers> transfersFakeList = dao.allTransfers(theUser.getUsername());
			 Transfers firstTransferFake = transfersFakeList.get(0);
			 
			 
			//Assert
			assertNotNull(firstTransferFake);
			assertEquals(testList.size(), transfersFakeList.size());
			
			
			  
		}
		 
		@Test
		public void listAllPendingTransfers_returns_list_for_current_user() {
			//Arrange
			List<Transfers> testList = new ArrayList<Transfers>();
			Account theAccount = getAccount(1L, 1, (new BigDecimal("1015.00")));
			User theUser = getUser(1L, "testAccount");
			Transfers theTransfer = getTransfers(1L, "testAccount", "testAccount2", (new BigDecimal("15.00")));
			testList.add(theTransfer);
			
			//Act
			 List<Transfers> pendingFakeList = dao.listAllPendingTransfers(theUser.getUsername());
			 Transfers firstPendingFake = pendingFakeList.get(0);
			 
			
			//Assert
			assertNotNull(firstPendingFake);
			assertEquals(testList.size(), pendingFakeList.size());
			
		}
		
		@Test
		public void listTransactionDetails_returns_detail_for_selected_transaction() throws Exception {
			//Arrange
			List<Transfers> testList = new ArrayList<Transfers>();
			Account theAccount = getAccount(1L, 1, (new BigDecimal("1015.00")));
			User theUser = getUser(1L, "testAccount");
			Transfers theTransferDetails = getTransferDetails(2L, "testAccount", "testAccount2", "Request", "Approved", (new BigDecimal("15.00")));
			testList.add(theTransferDetails);
			
			//Act
			 Transfers detailsFake = dao.detailsTransfersBasedOnId(2L, theUser.getUsername());
			 
			 
			
			//Assert
			assertNotNull(detailsFake);
			assertEquals(theTransferDetails.getId(), detailsFake.getId());
			
		}
		
//		@Test
//		public void addBucks_adds_bucks_to_selected_account() throws InsufficientAmountException, WrongIdException {
//			//Arrange
//
//			List<Account> testList = new ArrayList<Account>();
//			List<Account> testList2 = new ArrayList<Account>();
//			Account theAccountSend = getAccount(1L, 1, (new BigDecimal("1000.00")));//send $15, new balance $1000
//			Account theAccountTo = getAccount(2L, 2, (new BigDecimal("2020.00")));//receive $15, new balance $2020
//			User theUser = getUser(1L, "testAccount");
//			//Transfers sendBucks = getTransferDetails(2L, "testAccount", "testAccount2", "Request", "Approved", (new BigDecimal("15.00")));
//			testList.add(theAccountSend);
//			testList2.add(theAccountTo);
//			
//			//Act
//			
//			dao.addBucks(2, (new BigDecimal("15.00")), theUser.getUsername());
//			BigDecimal fromAccount = daoAccount.findBalanceByUserName(theUser.getUsername());
//			BigDecimal toAccount = daoAccount.findBalanceByUserName("testAccount2");
//			 
//			  
//			
//			//Assert
//			assertNotNull(fromAccount);
//			assertNotNull(toAccount);
//			assertEquals((new BigDecimal("1000.00")), fromAccount);
//			assertEquals((new BigDecimal("2020.00")), toAccount);
//		}
//		
		public Account getAccount(Long accountId, int userId, BigDecimal balance) {
			Account theAccount = new Account();
			
			theAccount.setAccountId(accountId);
			theAccount.setUserID(userId);
			theAccount.setBalance(balance);
			
			
			return theAccount;
		}
		
		public User getUser(Long id, String userName) {
			User theUser = new User();
			
			theUser.setId(id);
			theUser.setUsername(userName);
			
			
			return theUser;
		}
		
		public Transfers getTransfers(Long id, String fromUser, String toUser, BigDecimal amount) {
			Transfers theTransfer = new Transfers();
			
			theTransfer.setId(id);
			theTransfer.setFromUser(fromUser);
			theTransfer.setToUser(toUser);
			theTransfer.setAmount(amount);
			
			return theTransfer;
		}
		
		public Transfers getTransferDetails(Long id, String fromUser, String toUser, String transferType, String transferStatus, BigDecimal amount) {
			Transfers theDetails = new Transfers();
			
			theDetails.setId(id);
			theDetails.setFromUser(fromUser);
			theDetails.setToUser(toUser);
			theDetails.setTransferType(transferType);
			theDetails.setTransferStatus(transferStatus);
			theDetails.setAmount(amount);
			
			return theDetails;
		}
	

}
