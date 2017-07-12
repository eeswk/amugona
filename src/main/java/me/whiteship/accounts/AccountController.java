package me.whiteship.accounts;

import javax.validation.Valid;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
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
	
	@ExceptionHandler(UserDuplicatedException.class)
	public ResponseEntity handleuserDuplicatedException (UserDuplicatedException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[ " + e.getUsername() + " ] 중복된 username 입니다.");
		errorResponse.setCode("duplicated.username.exception");
		return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	
	@RequestMapping("/hello")
	public String hello() {
		return "Hello Spring Boot";
	}
}
