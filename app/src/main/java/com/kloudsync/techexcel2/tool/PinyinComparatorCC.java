package com.kloudsync.techexcel2.tool;

import java.util.Comparator;

import com.kloudsync.techexcel2.info.CountryCodeInfo;

public class PinyinComparatorCC implements Comparator<CountryCodeInfo> {

	@Override
	public int compare(CountryCodeInfo lhs, CountryCodeInfo rhs) {

		String str1 = (String) lhs.getSortLetters();
		String str2 = (String) rhs.getSortLetters();

		if (!str1.matches("[A-Z]")) {
			return 1;
		} else if (!str2.matches("[A-Z]")) {
			return -1;
		}
//		return str1.compareTo(str2);
		return lhs.getName().compareTo(rhs.getName());
	}

}
