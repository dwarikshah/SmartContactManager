package com.springboot.smartcontact.dao;



import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.springboot.smartcontact.entities.Contact;
import com.springboot.smartcontact.entities.User;

public interface ContactRepository extends JpaRepository<Contact, Integer> {
	
	// Pageable have two limit first current page and second is limit of record per page
	// is use for pagination
	@Query("select c from Contact c where c.user.id =:Id")
	public Page<Contact> findContactByUser(@Param("Id") int userId, Pageable pageable);
	
	public List<Contact> findByNameContainingAndUser(String name,User user);
	
}
