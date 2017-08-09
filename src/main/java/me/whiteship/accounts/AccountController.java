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
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;

import me.whiteship.commons.ErrorResponse;

@RestController
public class AccountController {


	@Autowired
	private AccountService service;

	@Autowired
	private AccountRepository repository;

	@Autowired
	private ModelMapper modelMapper;

	@RequestMapping(value = "/accounts", method = RequestMethod.POST)
	public ResponseEntity createAccount(@RequestBody @Valid AccountDto.Create create, BindingResult result) {

		if (result.hasErrors()) {
			// TODO 에러 응답 본문
			ErrorResponse errorResponse = new ErrorResponse();
			errorResponse.setMessage("잘못된 요청입니다.");
			errorResponse.setCode("bad.request");
			// TODO BindingResult 안에 들어있는 에러 정보 사용하기.
			return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
		}

		Account newAccount = service.createAccount(create);
		// 서비스처리가 잘됐다, 안됐다. 확인
		/*
		 * 1. 리턴 타입으로 판단 if(newAccount == null) 2. 파라미터 이용
		 * service.createAccount(create, result); if(result.hasErrors()) 좀더 직관적임
		 * 3. 서비스에서 예외를 던짐 아래줄로 내려오면 서비스호출이 잘됐다고 판단 코드깔끔
		 * 
		 * 1~2번의 좋은점은 return new ResponseEntity(HttpStatus.BAD_REQUEST) 3번 경우
		 * ExceptionHandler 사용하면 됨
		 */

		return new ResponseEntity(modelMapper.map(newAccount, AccountDto.Response.class), HttpStatus.CREATED);
		// return new ResponseEntity(newAccount, HttpStatus.CREATED);

	}

	// 메트릭스베리어블 @MatrixVariable /pet/42;q=11;r22
	// 페이징 Spring jpa pagable
	// accounts?page=0&size=20&sort=username&sort=joined,desc pageable에서 받아줌
	// NSPA 1. JSP 2. Thymleaf
	// SPA 3. 앵귤러 4. 리액트
	@RequestMapping(value = "/accounts", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public PageImpl<AccountDto.Response> getAccounts(Pageable pageable) {
		Page<Account> page = repository.findAll(pageable);
		// TODO stream() vs parallelStream()
		List<AccountDto.Response> content = page.getContent().parallelStream()
				.map(account -> modelMapper.map(account, AccountDto.Response.class)).collect(Collectors.toList());
		return new PageImpl<>(content, pageable, page.getTotalElements());
		// PageImpl<AccountDto.Response> result = new PageImpl<>(content,
		// pageable, page.getTotalElements());
		// return new ResponseEntity<>(result, HttpStatus.OK);

	}
	

	
	

	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.GET)
	@ResponseStatus(HttpStatus.OK)
	public  AccountDto.Response getAccount(@PathVariable Long id) {
		Account account =  service.getAccount(id);
		
		//서비스로 변경
//		Account account = repository.findOne(id);
//		if (account == null) {
//			return new ResponseEntity(HttpStatus.NOT_FOUND);
//		}

		//@ResponseStatus로 변경
//		AccountDto.Response result = modelMapper.map(account, AccountDto.Response.class);
//		return new ResponseEntity(result, HttpStatus.OK);
		
		return modelMapper.map(account, AccountDto.Response.class);
	}
	
	

	// 전체업데이트 PUT
	// - (username="whiteship", password:"pass", fullName:"keesun baik")

	// 부분업데이트 : PATCH
	// - (username:"whiteship")
	// - (password:"pass")
	// - (username:"whiteship", password:"pass")
	@RequestMapping(value = "/accounts/{id}", method = RequestMethod.PUT)
	public ResponseEntity updateAccount(@PathVariable Long id, 
														@RequestBody @Valid AccountDto.Update updatDto,
														BindingResult result) {
		if (result.hasErrors()) {
			return new ResponseEntity<>(HttpStatus.BAD_REQUEST);
		}
		
		Account updateAccount = service.updateAccount(id, updatDto);
		return new ResponseEntity<>(modelMapper.map(updateAccount, AccountDto.Response.class),
				HttpStatus.OK);
	}

/*	
	@RequestMapping(value="/index", method=RequestMethod.GET)
	public String index(Model model) {
		model.addAttribute("accounts", repository.findAll());
		return "index";  // index.jsp --> <c:forEach items=${accounts} var="account">
		// ${account.password} 직접 사용하지 않아도 되는 부분 굳이 DTO를 쓰지 않았던 이유
		// 현재 API 경우는 차원이 다름. 전송시 json으로 account 객체가 다 나가므로 DTO를 써야함
		
	}
*/
	
	@RequestMapping(value = "/accounts/{id}", method =RequestMethod.DELETE)
	public ResponseEntity deleteAccount(@PathVariable Long id) {
		service.deleteAccount(id);
		return new ResponseEntity<>(HttpStatus.NO_CONTENT);
	}
	
	
	
	
	
	
	// 글로벌로 빼면 모든 곳에서 발생되는 특정 익셉션
	@ExceptionHandler(UserDuplicatedException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleuserDuplicatedException(UserDuplicatedException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[ " + e.getUsername() + " ] 중복된 username 입니다.");
		errorResponse.setCode("duplicated.username.exception");
		return errorResponse;
		// return new ResponseEntity<>(errorResponse, HttpStatus.BAD_REQUEST);
	}
	
	@ExceptionHandler(AccountNotFoundException.class)
	@ResponseStatus(HttpStatus.BAD_REQUEST)
	public ErrorResponse handleAccountNotFoundException(AccountNotFoundException e) {
		ErrorResponse errorResponse = new ErrorResponse();
		errorResponse.setMessage("[ " + e.getId() + " ]에 해당하는 계정이 없습니다.");
		errorResponse.setCode("account.not.found.exception");
		return errorResponse;
	}
	
	

	@RequestMapping("/hello")
	public String hello() {
		return "Hello Spring Boot";
	}
}
