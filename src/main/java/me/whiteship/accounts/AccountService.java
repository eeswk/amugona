package me.whiteship.accounts;

import java.util.Date;


import org.modelmapper.ModelMapper;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.extern.slf4j.Slf4j;
import me.whiteship.accounts.AccountDto.Update;

@Service
@Transactional
@Slf4j
public class AccountService {
	
//	private Logger logger = LoggerFactory.getLogger(this.getClass());

	@Autowired
	private AccountRepository repository;
	
	@Autowired
	private ModelMapper modelMapper;

	public Account createAccount(AccountDto.Create dto) {
//		Account account = new Account();
//		account.setUsername(dto.getUsername());
//		account.setPassword(dto.getPassword());
		
		Account account = modelMapper.map(dto, Account.class);
		
//		Account account = new Account();
//		BeanUtils.copyProperties(dto, account);
		
		//TODO 유효한 username인지 판단
        String username = dto.getUsername();
        if (repository.findByUsername(username) != null) {
            log.error("user duplicated exception. {}", username);
            throw new UserDuplicatedException(username);
        }
		
		
		//TODO password 해싱
		
 		Date now = new Date();
		account.setJoined(now);
		account.setUpdated(now);
		
		return repository.save(account);
		
	}

	public Account updateAccount(Long id, AccountDto.Update updatDto) {
		Account account = getAccount(id);
		account.setPassword(updatDto.getPassword());
		account.setFullName(updatDto.getFullName());
		return repository.save(account);
		// TODO Auto-generated method stub
		
	}

	public Account getAccount(Long id)   {
		Account account = repository.findOne(id);
		if(account == null) {
			throw new AccountNotFoundException(id);
		}
		return account;
	}
}
