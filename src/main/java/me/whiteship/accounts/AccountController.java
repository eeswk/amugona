package me.whiteship.accounts;

import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;
import me.whiteship.commons.ErrorResponse;

@RestController
public class AccountController {

	private static final String ErrorResponse = null;

	@Autowired
	private AccountService service;
	
	@Autowired
	private AccountRepository repository;
	
	@Autowired
	private ModelMapper modelMapper;
	
	@RequestMapping(value="/accounts", method = RequestMethod.POST)
	public ResponseEntity createAccount(@RequestBody @Valid AccountDto.Create create,
													   BindingResult result){
		
		if(result.hasErrors()){
			// TODO 에러 응답 본문
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			// TODO BindingResult 안에 들어있는 에러 정보 사용하기.
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}
		
		Account newAccount = service.createAccount(create);
		//서비스처리가 잘됐다, 안됐다. 확인
		/*
		1. 리턴 타입으로 판단  if(newAccount == null) 
		2. 파라미터 이용 service.createAccount(create, result);
		 	if(result.hasErrors()) 좀더 직관적임
		3. 서비스에서 예외를 던짐  아래줄로 내려오면 서비스호출이 잘됐다고 판단 코드깔끔
		
		1~2번의 좋은점은 return new ResponseEntity(HttpStatus.BAD_REQUEST)
		3번 경우 ExceptionHandler 사용하면 됨
		 */
		
		return new ResponseEntity(modelMapper.map(newAccount, AccountDto.Response.class), HttpStatus.CREATED);
//		return new ResponseEntity(newAccount, HttpStatus.CREATED);
		
		
	}
	
	
	//글로벌로 빼면 모든 곳에서 발생되는 특정 익셉션
//	@ExceptionHandler(UserDuplicatedException.class)
//	public ResponseEntity handleuserDuplicatedException (UserDuplicatedException e) {
//		ErrorResponse errorResponse = new ErrorResponse();
//		errorResponse.setMessage("[ " + e.getUsername() + " ] 중복된 username 입니다.");
//		errorResponse.setCode("duplicated.username.exception");
//		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
//	}
	
	
	//메트릭스베리어블 @MatrixVariable /pet/42;q=11;r22
	//페이징 Spring jpa pagable
	// accounts?page=0&size=20&sort=username&sort=joined,desc pageable에서 받아줌
	// NSPA 1. JSP 2. Thymleaf
	// SPA   3. 앵귤러 4. 리액트
	@RequestMapping(value = "/accounts", method=RequestMethod.GET)
	public ResponseEntity getAccounts(Pageable pageable) {
		Page<Account> page =  repository.findAll(pageable);
		//TODO stream() vs parallelStream()
		List<AccountDto.Response> content =  page.getContent().parallelStream()
				.map(account -> modelMapper.map(account, AccountDto.Response.class))
				.collect(Collectors.toList());
		PageImpl<AccountDto.Response> result = new PageImpl<>(content, pageable, page.getTotalElements());
		return new ResponseEntity<>(result, HttpStatus.OK);
		
	}
	
	
	@RequestMapping("/hello")
	public String hello() {
		return "Hello Spring Boot";
	}
}
