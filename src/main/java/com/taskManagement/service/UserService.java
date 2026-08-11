package com.taskManagement.service;

import java.util.List;

import com.taskManagement.dto.JwtAuthenticationResponse;
import com.taskManagement.dto.LoginDto;
import com.taskManagement.dto.UserDto;
import com.taskManagement.entity.Users;

public interface UserService {

	public UserDto createUser(UserDto userDto, Boolean isAdmin);

	public List<UserDto> getAllUsers();

	public String deleteUser(long userid);

	public List<UserDto> getAllActiveUsers();

	public void deactivateUser(Long userid);

	public Users dtoToEntity(UserDto userDto);

	public UserDto entityToDto(Users users);

	public UserDto getUserByName(String name);

	public UserDto getUserById(Long userId);

	public JwtAuthenticationResponse login(LoginDto loginDto);

}
