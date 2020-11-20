package com.techelevator.tenmo.controller;

import java.math.BigDecimal;
import java.security.Principal;
import java.util.List;

import javax.validation.Valid;

import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.config.annotation.authentication.builders.AuthenticationManagerBuilder;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.bind.annotation.PathVariable;

import com.fasterxml.jackson.annotation.JsonProperty;
import com.techelevator.tenmo.dao.AccountDAO;
import com.techelevator.tenmo.dao.TransfersDAO;
import com.techelevator.tenmo.dao.UserDAO;
import com.techelevator.tenmo.dao.UserSqlDAO;
import com.techelevator.tenmo.model.Account;
import com.techelevator.tenmo.model.InsufficientAmountException;
import com.techelevator.tenmo.model.LoginDTO;
import com.techelevator.tenmo.model.RegisterUserDTO;
import com.techelevator.tenmo.model.Transfers;
import com.techelevator.tenmo.model.User;
import com.techelevator.tenmo.model.UserAlreadyExistsException;
import com.techelevator.tenmo.model.WrongIdException;
import com.techelevator.tenmo.security.jwt.JWTFilter;
import com.techelevator.tenmo.security.jwt.TokenProvider;

/**
 * Controller to authenticate users.
 */
@RestController
public class AuthenticationController {

    private final TokenProvider tokenProvider;
    private final AuthenticationManagerBuilder authenticationManagerBuilder;
    private UserDAO userDAO;
    private AccountDAO accountDAO;
    private TransfersDAO transfersDAO;
    

    public AuthenticationController(TokenProvider tokenProvider, AuthenticationManagerBuilder authenticationManagerBuilder, UserDAO userDAO, AccountDAO accountDAO, TransfersDAO transfersDAO) {
        this.tokenProvider = tokenProvider;
        this.authenticationManagerBuilder = authenticationManagerBuilder;
        this.userDAO = userDAO;
        this.accountDAO = accountDAO;
        this.transfersDAO = transfersDAO;
    }

    @RequestMapping(value = "/login", method = RequestMethod.POST)
    public ResponseEntity<LoginResponse> login(@Valid @RequestBody LoginDTO loginDto) {

        UsernamePasswordAuthenticationToken authenticationToken =
                new UsernamePasswordAuthenticationToken(loginDto.getUsername(), loginDto.getPassword());

        Authentication authentication = authenticationManagerBuilder.getObject().authenticate(authenticationToken);
        SecurityContextHolder.getContext().setAuthentication(authentication);
        String jwt = tokenProvider.createToken(authentication, false);
        
        User user = userDAO.findByUsername(loginDto.getUsername());

        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.add(JWTFilter.AUTHORIZATION_HEADER, "Bearer " + jwt);
        return new ResponseEntity<>(new LoginResponse(jwt, user), httpHeaders, HttpStatus.OK);
    }

    @ResponseStatus(HttpStatus.CREATED)
    @RequestMapping(value = "/register", method = RequestMethod.POST)
    public void register(@Valid @RequestBody RegisterUserDTO newUser) {
        try {
            User user = userDAO.findByUsername(newUser.getUsername());
            throw new UserAlreadyExistsException();
        } catch (UsernameNotFoundException e) {
            userDAO.create(newUser.getUsername(),newUser.getPassword());
        }
    }
    
    /**
     * gets balance
     * 
     */
    
    @RequestMapping(value = "/balance", method = RequestMethod.GET)
    public BigDecimal accountBalance(Principal principal) {
    	
		BigDecimal account = accountDAO.findBalanceByUserName(principal.getName());
		
		return account;
		
		
    }
    /**
     * 
     * gets all transfers
     * 
     */
    @RequestMapping(value = "/transfers", method = RequestMethod.GET)
    public List<Transfers> getAllTransfers(Principal principal) {
     	
    	return transfersDAO.allTransfers(principal.getName());
    }
    
    /**
     * 
     * returns pending transfers
     */
    
    @RequestMapping(value = "/pending_requests", method = RequestMethod.GET)
    public List<Transfers> viewPendingRequests(Principal principal) {
    	
    	return transfersDAO.listAllPendingTransfers(principal.getName());
    }
    /**
     * 
     * gets transfer details by ID 
     * @throws Exception 
     *
     */
    @RequestMapping(value = "/transfers/{transferIdDetails}", method = RequestMethod.GET)
    public Transfers viewTransferDetailsById(@PathVariable long transferIdDetails, Principal principal) throws Exception {
     	
    	return transfersDAO.detailsTransfersBasedOnId(transferIdDetails, principal.getName());
    }
     
    /**
     * returns a list of users
     */
    @RequestMapping(value = "/users", method = RequestMethod.GET)
    public List<User> getAllUsers() {
     	
    	return userDAO.findAll();
    }
    
    /*
     * 
     * Adds Bucks to receiving user
     */
    
    @RequestMapping(value = "getting_bucks/{id}/{amount}", method = RequestMethod.PUT)
    public void addBucksToRecievingUser(@PathVariable int id, @PathVariable BigDecimal amount, Principal principal) throws InsufficientAmountException, WrongIdException {
    	
    	transfersDAO.addBucks(id, amount, principal.getName());
    }
    
    /*  
     * 
     * logs transfers
     */
    
    @RequestMapping(value = "post_to_transfer/{id}/{amount}", method = RequestMethod.POST)
    public void postTrasfersToTransferTable(@PathVariable int id, @PathVariable BigDecimal amount, Principal principal) {
    	
    	transfersDAO.postToTransfers(id, amount, principal.getName());
    }
    
    
    /**
     * Object to return as body in JWT Authentication.
     */
    static class LoginResponse {

        private String token;
        private User user;

        LoginResponse(String token, User user) {
            this.token = token;
            this.user = user;
        }

        @JsonProperty("token")
        String getToken() {
            return token;
        }

        void setToken(String token) {
            this.token = token;
        }

        @JsonProperty("user")
		public User getUser() {
			return user;
		}

		public void setUser(User user) {
			this.user = user;
		}
    }
}

