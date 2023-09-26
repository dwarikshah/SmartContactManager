package com.springboot.smartcontact.Controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.springboot.smartcontact.dao.UserRepository;
import com.springboot.smartcontact.entities.User;
import com.springboot.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
public class MainController {
	
	@Autowired
	private BCryptPasswordEncoder passwordEncoder;

	@Autowired
	UserRepository userRepository;

	@GetMapping("adduser")
	@ResponseBody
	public String user() {

		User u = new User();
		u.setName("dwarik");
		u.setEmail("shahdwarik@gmail.com");
		u.setPassword("123456");
		u.setAbout("Test");

		userRepository.save(u);

		return "working";
	}

	@RequestMapping("/")
	public String home(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "home";
	}

	@RequestMapping("/about")
	public String about(Model m) {
		m.addAttribute("title", "Home - Smart Contact Manager");
		return "about";
	}

	@RequestMapping("/signup")
	public String signup(Model m) {
		m.addAttribute("title", "Register - Smart Contact Manager");
		m.addAttribute("user", new User());
		return "signup";
	}

	@PostMapping("/do_register")
	public String registeruser(@Validated @ModelAttribute("user") User user, BindingResult result1,
			@RequestParam(value = "agreement", defaultValue = "False") boolean agreement, 
			Model model, HttpSession session) {

		try {
			if (!agreement) {
				System.out.println("You have not agreed the the terms and conditions");
				throw new Exception("You have not agreed the the terms and conditions");
			}

			if (result1.hasErrors()) {
				System.out.println(result1.toString());
				model.addAttribute("user", user);
				return "signup";
			}

			user.setRole("ROLE_USER");
			user.setEnabled(true);
			user.setImageUrl("dafault.png");
			user.setPassword(passwordEncoder.encode(user.getPassword()));

			User result = this.userRepository.save(user);

			model.addAttribute("user", new User());

			session.setAttribute("message", new Message("Successfully Registered !!", "alert-success"));

		} catch (Exception e) {
			e.printStackTrace();
			model.addAttribute("user", user);
			session.setAttribute("message", new Message("Something Went Wrong !!" + e.getMessage(), "alert-danger"));
		}
		return "signup";
	}
	
	@RequestMapping("/signin")
	public String login(Model m) {
		m.addAttribute("title", "Login Here");
		return "login";
	}
	
}
