package me.whiteship.account;

import static org.hamcrest.CoreMatchers.is;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.put;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.print;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.junit4.SpringJUnit4ClassRunner;
import org.springframework.test.context.web.WebAppConfiguration;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.context.WebApplicationContext;

import com.fasterxml.jackson.databind.ObjectMapper;

import jdk.net.SocketFlow.Status;
import me.whiteship.Application;
import me.whiteship.accounts.Account;
import me.whiteship.accounts.AccountDto;
import me.whiteship.accounts.AccountService;

@RunWith(SpringJUnit4ClassRunner.class)
@SpringBootTest(classes = Application.class)
@WebAppConfiguration
@Transactional
public class AccountControllerTest {

	@Autowired
	WebApplicationContext wac;
	
	@Autowired
	ObjectMapper objectMapper; 
	
	@Autowired
	AccountService service;
	
	MockMvc mockMvc;
	
	@Before
	public void setUp() {
		//공용변수로
		mockMvc = MockMvcBuilders.webAppContextSetup(wac).build();;
				
	}
	
	
	@Test
	public void createAccount() throws Exception{

		
		AccountDto.Create createDto = accountCreateDto();
		
		
		ResultActions result =  mockMvc.perform(post("/accounts").contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isCreated());	
		
		// TODO JSON Path
		result.andExpect(jsonPath("$.username", is("whiteship")));
		
		result = mockMvc.perform(post("/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.code", is("duplicated.username.exception")));
		
		
	}
	
	@Test
	public void createAccount_BadRequest() throws Exception {
		AccountDto.Create createDto = new AccountDto.Create();
		createDto.setUsername("   ");
		createDto.setPassword("1234");
		
		ResultActions result = mockMvc.perform(post("/accounts")
				.contentType(MediaType.APPLICATION_JSON)
				.content(objectMapper.writeValueAsString(createDto)));
		
		result.andDo(print());
		result.andExpect(status().isBadRequest());
		result.andExpect(jsonPath("$.code", is("bad.request")));
	}
	
	@Test
	public void getAccounts() throws Exception{
		AccountDto.Create createDto = accountCreateDto();
		service.createAccount(createDto);
		
		ResultActions result = mockMvc.perform(get("/accounts"));
		
//		{"content":
//		[{"id":1,"username":"whiteship","fullName":null,"joined":1499959342908,"updated":1499959342908}],
//		"totalElements":1,
//		"totalPages":1,
//		"last":true,
//		"number":0,
//		"size":20,
//		"sort":null,
//		"first":true,
//		"numberOfElements":1}
		
		result.andDo(print());
		result.andExpect(status().isOk());
	}
	
    private AccountDto.Create accountCreateDto() {
        AccountDto.Create createDto = new AccountDto.Create();
        createDto.setUsername("whiteship");
        createDto.setPassword("password");
        return createDto;
    }

    @Test
    public void getAccount() throws Exception {
        AccountDto.Create createDto = accountCreateDto();
//        AccountDto.Create createDto = new AccountDto.Create();
//        createDto.setUsername("whiteship");
//        createDto.setPassword("password");
        
        Account account = service.createAccount(createDto);
        System.out.println("account id"+account.getId());

        ResultActions result = mockMvc.perform(get("/accounts/" + account.getId()));

        result.andDo(print());
        result.andExpect(status().isOk());
    }
    
    @Test
    public void updateAccount() throws Exception {
    	 AccountDto.Create createDto = accountCreateDto();
    	 Account account = service.createAccount(createDto);
    	 
    	 AccountDto.Update updateDto = new AccountDto.Update();
    	 updateDto.setFullName("keesun baik");
    	 updateDto.setPassword("pass");
    	 
    	 ResultActions result = mockMvc.perform(put("/accounts/" + account.getId())
    			 .contentType(MediaType.APPLICATION_JSON)
    			 .content(objectMapper.writeValueAsString(updateDto)));
    	 
    	 result.andDo(print());
    	 result.andExpect(status().isOk());
    	 result.andExpect(jsonPath("$.fullName", is("keesun baik")));
    	 
    	
    }



	

}
