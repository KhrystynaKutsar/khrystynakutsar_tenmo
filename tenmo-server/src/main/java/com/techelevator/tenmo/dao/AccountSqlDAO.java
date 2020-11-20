package com.techelevator.tenmo.dao;

import java.math.BigDecimal;

import javax.naming.spi.DirStateFactory.Result;
import javax.sql.DataSource;

import org.springframework.data.relational.core.sql.Select;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.jdbc.support.rowset.SqlRowSet;
import org.springframework.stereotype.Component;

import com.techelevator.tenmo.model.Account;

@Component
public class AccountSqlDAO implements AccountDAO {
	
	private JdbcTemplate jdbcTemplate;
	
	public AccountSqlDAO(DataSource dataSource) {
        this.jdbcTemplate = new JdbcTemplate(dataSource);
    }

	@Override
	public BigDecimal findBalanceByUserName(String userName) {
		String sqlFindBalance = ("SELECT balance FROM accounts INNER JOIN users ON users.user_id = accounts.user_id WHERE users.username = ?");
		
		SqlRowSet result = jdbcTemplate.queryForRowSet(sqlFindBalance, userName);
		
		if(result.next()) {
			return result.getBigDecimal("balance");
		}
		return null;
		
	} 


	//private Account mapRowToAccount(SqlRowSet result) {
	//	Account account = new Account();
		
		//account.setAccountId(result.getLong("account_id"));
		//account.setUserID(result.getInt("user_id"));
	//	account.setBalance(result.getBigDecimal("balance"));
		
	//	return account;
	//}

}
