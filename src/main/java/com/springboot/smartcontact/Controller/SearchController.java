package com.springboot.smartcontact.Controller;

import java.security.Principal;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import com.springboot.smartcontact.dao.ContactRepository;
import com.springboot.smartcontact.dao.UserRepository;
import com.springboot.smartcontact.entities.Contact;
import com.springboot.smartcontact.entities.User;

@RestController
public class SearchController {
	
	@Autowired
	UserRepository userRepository;

	@Autowired
	ContactRepository contactRepository;
	
	@GetMapping("/search/{query}")
	public ResponseEntity<?> search(@PathVariable("query") String query,Principal principal){
		
		User user = userRepository.getUserbyUserName(principal.getName());
		List<Contact> contact = contactRepository.findByNameContainingAndUser(query, user);
		
		return ResponseEntity.ok(contact);
	}
	
}
