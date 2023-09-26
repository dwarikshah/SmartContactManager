package com.springboot.smartcontact.Controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import java.security.Principal;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ClassPathResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.multipart.MultipartFile;

import com.springboot.smartcontact.dao.ContactRepository;
import com.springboot.smartcontact.dao.UserRepository;
import com.springboot.smartcontact.entities.Contact;
import com.springboot.smartcontact.entities.User;
import com.springboot.smartcontact.helper.Message;

import jakarta.servlet.http.HttpSession;

@Controller
@RequestMapping("/user")
public class UserController {

	@Autowired
	UserRepository userRepository;

	@Autowired
	ContactRepository contactRepository;

	@ModelAttribute
	public void addCommonData(Model model, Principal principal) {

		String userName = principal.getName();

		User user = userRepository.getUserbyUserName(userName);

		model.addAttribute("user", user);

	}

	@RequestMapping("/index")
	public String dashboard(Model model, Principal principal) {
		model.addAttribute("title", "User Dashboard");
		return "normal/user_dashboard";
	}

	@GetMapping("/add-contact")
	public String addContact(Model model) {
		model.addAttribute("title", "Add Contact");
		model.addAttribute("contact", new Contact());

		return "normal/add_contact";
	}

	@PostMapping("process-contact")
	public String processContact(@ModelAttribute Contact contact, @RequestParam("profileImage") MultipartFile image,
			Principal principal, HttpSession session) {
		try {
			String userName = principal.getName();
			User user = userRepository.getUserbyUserName(userName);

			// processing and uploading image
			if (image.isEmpty()) {

				contact.setImage("contact.png");

			} else {

				contact.setImage(image.getOriginalFilename());

				File savefile = new ClassPathResource("static/img").getFile();

				Path path = Paths.get(savefile.getAbsolutePath() + File.separator + image.getOriginalFilename());

				Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);
			}

			contact.setUser(user);
			user.getContacts().add(contact);

			User result = userRepository.save(user);

			// success message
			session.setAttribute("message", new Message("Your contact added successfully !! ADD More....", "success"));

		} catch (Exception e) {
			System.out.println(e.getMessage());
			e.printStackTrace();

			// error message
			session.setAttribute("message", new Message("Something Went Wrong ! try again", "danger"));
		}

		return "normal/add_contact";

	}

	@GetMapping("show-contacts/{page}")
	public String viewcontact(@PathVariable("page") int page, Model m, Principal principal, HttpSession session) {

		m.addAttribute("title", "show user contacts");

		String userName = principal.getName();

		User user = this.userRepository.getUserbyUserName(userName);

		// starting page is o for Pagerequest
		PageRequest of = PageRequest.of(page - 1, 3);

		Page<Contact> contacts = this.contactRepository.findContactByUser(user.getId(), of);
//		if(contacts == null || contacts.size()==0) {
//			session.setAttribute("message", new Message("No Contacts... !!","success"));
//		}
		m.addAttribute("contacts", contacts);
		m.addAttribute("currentPage", page);
		m.addAttribute("totalPages", contacts.getTotalPages());

		return "normal/show_contacts";
	}

	@RequestMapping("/contact/{cId}")
	public String showContactDetial(@PathVariable("cId") int id, Model m, Principal principal) {

		String name = principal.getName();
		User userbyUserName = userRepository.getUserbyUserName(name);

		Optional<Contact> findById = contactRepository.findById(id);

		if (userbyUserName.getId() == findById.get().getUser().getId()) {

			findById.ifPresent((findById1) -> {
				m.addAttribute("contact", findById1);
				m.addAttribute("title", findById.get().getName());
			});

		}

		return "normal/contact_detail";

	}

	@GetMapping("/delete-contact/{cId}")
	public String deleteContact(@PathVariable("cId") int id, Principal principal, HttpSession session)
			throws IOException {

		String name = principal.getName();
		User user = userRepository.getUserbyUserName(name);

		Contact contact = contactRepository.findById(id).get();

		// delete image from folder
		if (contact.getImage() != null && contact.getImage() != "contact.png") {

			File file = new ClassPathResource("static/img").getFile();
			Path path = Paths.get(file.getAbsolutePath() + File.separator + contact.getImage());

			if (Files.exists(path)) {
				Files.delete(path);
			}

		}

		if (user.getId() == contact.getUser().getId()) {
			//contactRepository.deleteById(id);
			contactRepository.delete(contact);
			session.setAttribute("message", new Message("delete sucessfully.. !!", "success"));
		}
		return "redirect:/user/show-contacts/1";
	}

	// update contact
	@PostMapping("/update-contactForm/{cId}")
	public String updateForm(@PathVariable("cId") int cid, Model m) {

		m.addAttribute("title", "Update Contact");

		Contact contact = contactRepository.findById(cid).get();
		m.addAttribute("contact", contact);

		return "normal/update_contact";
	}

	@PostMapping("/update-contact")
	public String updateContact(@ModelAttribute Contact contact,
			@RequestParam("profileImage") MultipartFile image, HttpSession session,
			Principal principal) throws IOException {

		String name = principal.getName();
		User user = userRepository.getUserbyUserName(name);

		contact.setUser(user);

		Contact contact2 = contactRepository.findById(contact.getcId()).get();

		if (!image.isEmpty()) {
			
			//delete old image
			if(!contact2.getImage().equals("contact.png")) {
				File deletefile = new ClassPathResource("static/img").getFile();
				File file1 = new File(deletefile, contact2.getImage());
				file1.delete();
			}
			
			// new image
			File file = new ClassPathResource("static/img").getFile();

			Path path = Paths.get(file.getAbsolutePath() + File.separator + image.getOriginalFilename());

			Files.copy(image.getInputStream(), path, StandardCopyOption.REPLACE_EXISTING);

			contact.setImage(image.getOriginalFilename());

		}

		else {
			
			contact.setImage(contact2.getImage());
			
		}

		contactRepository.save(contact);
		
		session.setAttribute("message", new Message("Updated Successfully ...", "success"));
		
		return "redirect:/user/contact/"+contact.getcId();
	}
	
	@RequestMapping("/profile")
	public String userForm(Model m) {
		m.addAttribute("title", "User Profile");
		return "normal/user_profile";
	}

}
