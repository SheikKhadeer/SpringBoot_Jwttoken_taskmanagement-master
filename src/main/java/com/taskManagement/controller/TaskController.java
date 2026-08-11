package com.taskManagement.controller;

import java.time.LocalDate;
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
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.taskManagement.dto.TaskDto;
import com.taskManagement.enums.TaskPriority;
import com.taskManagement.enums.TaskStatus;
import com.taskManagement.service.TaskService;

@RestController
@RequestMapping("/api/tasks")
public class TaskController {

	@Autowired
	private TaskService taskService;

	private static final Logger logger = LoggerFactory.getLogger(TaskController.class);

	// BOTH CAN ADMIN AND USER
	@PostMapping("/{userid}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<TaskDto> saveTask(@PathVariable(name = "userid") long userid,
			@RequestBody @Valid TaskDto taskDto) {
		return new ResponseEntity<>(taskService.saveTask(userid, taskDto), HttpStatus.CREATED);
	}

	@GetMapping("/all")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<List<TaskDto>> getAllTasks() {
		logger.info("fetching all tasks...");
		return new ResponseEntity<>(taskService.getAllTasks(), HttpStatus.OK);
	}

	@GetMapping("/dueDate/{dueDate}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<List<TaskDto>> getOverdueTasks(@PathVariable("dueDate") LocalDate date) {

		return new ResponseEntity<>(taskService.getTasksByOverDueDate(date), HttpStatus.OK);
	}

	@GetMapping("/priority/{priority}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<List<TaskDto>> getTasksByPriority(@PathVariable("priority") TaskPriority priority) {
		return new ResponseEntity<>(taskService.getTasksByPriority(priority), HttpStatus.OK);
	}

	@GetMapping("/status/{status}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<List<TaskDto>> getTasksByStatus(@PathVariable("status") TaskStatus status) {
		return new ResponseEntity<>(taskService.getTasksByStatus(status), HttpStatus.OK);

	}

	// delete the task
	@DeleteMapping("/{userid}/tasks/{taskid}")
	@PreAuthorize("hasAnyRole('ROLE_ADMIN','ROLE_USER')")
	public ResponseEntity<String> deleteTask(@PathVariable(name = "userid") long userid,
			@PathVariable(name = "taskid") long taskid) {
		logger.info("Attempting to delete task with ID {} ", taskid);
		taskService.deleteTask(userid, taskid);

		return new ResponseEntity<>("Task deleted succesfully", HttpStatus.OK);
	}

	// ONLY USER

	// GET ALL TASKS
	@GetMapping("/user/{userid}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<List<TaskDto>> getTasksByUserId(@PathVariable(name = "userid") Long userid) {
		logger.info("fetching all tasks...");
		return new ResponseEntity<>(taskService.getAllTasksByUserId(userid), HttpStatus.OK);
	}

	// GET individual task
	@GetMapping("/{userid}/tasks/{taskid}")
	@PreAuthorize("hasRole('ROLE_USER')")
	public ResponseEntity<TaskDto> getTaskById(@PathVariable(name = "userid") Long userid,
			@PathVariable(name = "taskid") Long taskid) {
		return new ResponseEntity<>(taskService.getTaskById(userid, taskid), HttpStatus.OK);
	}

}
