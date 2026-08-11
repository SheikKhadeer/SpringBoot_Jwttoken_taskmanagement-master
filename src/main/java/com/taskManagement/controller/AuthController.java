package com.taskManagement.controller;

import java.util.List;

import javax.validation.Valid;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.taskManagement.dto.JwtAuthenticationResponse;
import com.taskManagement.dto.LoginDto;
import com.taskManagement.dto.UserDto;
import com.taskManagement.exception.UserNotFound;
import com.taskManagement.service.UserService;

@RestController
@RequestMapping("/api/users")
public class AuthController {

	@Autowired
	private UserService userService;

	private static final Logger logger = LoggerFactory.getLogger(AuthController.class);

	// NO authentication needed

	@PostMapping
	public ResponseEntity<UserDto> createUser(@RequestBody @Valid UserDto userDto,
			@RequestParam(name="isAdmin",required = false, defaultValue = "false") boolean isAdmin) {
		System.out.println("entering into it: "+isAdmin);
		logger.info("Request to create user :{}", userDto.getEmail());
		return new ResponseEntity<>(userService.createUser(userDto, isAdmin), HttpStatus.CREATED);
	}

	@PostMapping("/login")
	public ResponseEntity<JwtAuthenticationResponse> loginUser(@RequestBody LoginDto loginDto) {
		JwtAuthenticationResponse response = userService.login(loginDto);
		return ResponseEntity.ok(response);
	}

	// admin only
	@GetMapping
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<UserDto>> getAllUsers() {
		logger.info("fetching all users...");
		List<UserDto> users = userService.getAllUsers();
		logger.info("fetched {} users succesfully", users.size());
		return new ResponseEntity<>(users, HttpStatus.OK);
	}

	@GetMapping("/active")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<List<UserDto>> getAllActiveUsers() {
		List<UserDto> activeUsers = userService.getAllActiveUsers();
		return new ResponseEntity<>(activeUsers, HttpStatus.OK);
	}

	@PutMapping("/deactivate/{userid}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deactivateUser(@PathVariable(name = "userid") Long userid) {
		userService.deactivateUser(userid);
		return new ResponseEntity<String>("user deactivated succesfully", HttpStatus.OK);
	}

	// delete the user
	@DeleteMapping("/delete/{userid}")
	@PreAuthorize("hasRole('ROLE_ADMIN')")
	public ResponseEntity<String> deleteTask(@PathVariable(name = "userid") Long userid) {

		logger.info("Attempting to delete user with ID {} ", userid);
		try {
			String response = userService.deleteUser(userid);
			logger.info("User with ID {} deleted successfully");
			return new ResponseEntity<>(response, HttpStatus.OK);
		} catch (UserNotFound ex) {
			logger.error("failed to delete user with ID {} :{}", userid, ex.getMessage());
			throw ex;
		}
	}

	// BOTH CAN ADMIN AND USER
	@GetMapping("/{userid}")
	@PreAuthorize("hasRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<UserDto> getUserById(@PathVariable(name = "userid") Long userid) {
		logger.info("User with ID {} :",userid);
		UserDto userDto = userService.getUserById(userid);
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}

	@GetMapping("/name/{name}")
	@PreAuthorize("hasRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<UserDto> getUserByName(@PathVariable(name = "name") String name) {
		UserDto userDto = userService.getUserByName(name);
		return new ResponseEntity<>(userDto, HttpStatus.OK);
	}

}
