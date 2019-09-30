package com.kloudsync.techexcel2.tool;

import com.kloudsync.techexcel2.info.Customer;

import java.util.Comparator;

public class PinyinComparator implements Comparator<Customer>{

	@Override
	public int compare(Customer lhs, Customer rhs) {
		
		String str1 = (String) lhs.getSortLetters();
	     String str2 = (String) rhs.getSortLetters();
	   
	     if(!str1.matches("[A-Z]")){
	    	 return 1;
	     }else if(!str2.matches("[A-Z]")){
	    	 return -1;
	     }
//	     return str1.compareTo(str2);
		return lhs.getName().compareTo(rhs.getName());
	}


}
