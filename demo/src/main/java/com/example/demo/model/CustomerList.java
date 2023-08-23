package com.example.demo.model;

import java.util.ArrayList;

public class CustomerList {
   ArrayList<Customer> list= new ArrayList<>();
   
   public CustomerList(Customer customer){
	   list.add(customer);
   }
   
   public ArrayList<Customer> getCustomerList(){
	   return list;
   }
}
