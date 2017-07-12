package me.whiteship.account;

import org.junit.Test;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;

import me.whiteship.accounts.Account;

public class AccountTest {

	@Test
	public void getterSetter(){
		Account account = new Account();
		account.setUsername("whiteship");
		account.setPassword("password");
		
		assertThat(account.getUsername(), is("whiteship"));

	}
}
