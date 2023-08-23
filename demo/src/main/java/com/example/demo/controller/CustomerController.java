package com.example.demo.controller;

import java.util.List;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import com.example.demo.model.Customer;
import com.example.demo.model.Token;
import com.example.demo.service.CustomerService;


@Controller
public class CustomerController {
	@GetMapping("/login")
    public String loginForm() {
    	return "login";
    }
	
	@Autowired
    private CustomerService customerService;
	@Autowired
	Token token;

    @PostMapping("/authenticate")
    public String authenticate(
            @RequestParam("user_id") String loginId,
            @RequestParam("password") String password,
            Model model
    ) {
        String response = customerService.authenticate(loginId, password);
        model.addAttribute("response", response);
        
        token.setToken(response);
        
        //System.out.println(token.getToken());
        return "success"; // Return the name of the result view
    }
    
    @GetMapping("/customerList")
    public String getCustomerList(Model model) {
        // Assuming you've obtained the token from authentication earlier
        String access_token = token.getToken();

        List<Customer> customers = customerService.getCustomerList(access_token);
        model.addAttribute("customers", customers);

        return "result"; // Thymeleaf template to display the list
    }
    
    @GetMapping("/customerForm")
    public String customerFrom(Model model) {
    	model.addAttribute("customer", new Customer());
    	return "customerForm";
    }
    
    @PostMapping("/addCustomer")
    public String addCustomer(@ModelAttribute("customer") Customer customer) {
        // Assuming you've obtained the token from authentication earlier
        String access_token = token.getToken();

        ResponseEntity<String> response = customerService.createCustomer(access_token, customer);

        if (response.getStatusCode().is2xxSuccessful()) {
            // Successfully created
            return "redirect:/customerList"; // Redirect to the customer list page
        } else {
            // Handle error
            return "error"; // Return an error page
        }
    }
    
    @PostMapping("/deleteCustomer")
    public String deleteCustomer(@RequestParam String uuid) {
        // Get the token from your authentication process
        String access_token = token.getToken();; 

        ResponseEntity<String> response = customerService.deleteCustomer(access_token, uuid);

        // Handle response based on status code
        if (response.getStatusCode() == HttpStatus.OK) {
            // Successfully deleted
        } else if (response.getStatusCode() == HttpStatus.INTERNAL_SERVER_ERROR) {
            // Error in deletion
        } else if (response.getStatusCode() == HttpStatus.BAD_REQUEST) {
            // UUID not found
        }

        // Redirect back to the customer list page
        return "redirect:/customerList";
    }
    
}
