package com.example.k8s.springbootk8smysql.controller;


import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.example.k8s.springbootk8smysql.entity.User;
import com.example.k8s.springbootk8smysql.repository.UserRepository;

@RequestMapping("/api")
@RestController
public class UserController {

    @Autowired
    private UserRepository userRepository;

    @GetMapping("/")
	public ResponseEntity<?> hello(){
    	List<String> strings = new ArrayList<>();
    	strings.add("Hello There");
    	strings.add("Hello The api");
    	strings.add("Hello There is working");
    	return ResponseEntity.ok(strings);
	}
	@PostMapping("/addUser")
	public String saveUser(@RequestBody User emp) {
		userRepository.save(emp);
		return "User added successfully::"+emp.getId();
		
	}
	
	@GetMapping("/users")
	public List<User> getUsers() {
		return userRepository.findAll();
	}

	@GetMapping("/findUser/{id}")
	public Optional<User> getUser(@PathVariable Long id) {
		return userRepository.findById(id);
	}
	
	@GetMapping("/deleteUser/{id}")
	public String deleteUser(@PathVariable Long id) {
		userRepository.deleteById(id);
		return "Deleted User Successfully::"+id;
	}
	

}
