package models;

import com.avaje.ebean.annotation.EnumValue;

public enum DingEnum {
	
	@EnumValue("C")
	C, 
	
	@EnumValue("J")
	Java, 
	
	@EnumValue("C+")
	Cpluplus, 
	
	@EnumValue("OC")
	ObjectiveC, 
	
	@EnumValue("CS")
	CSharp, 
	
	@EnumValue("VB")
	VisualBasic, 
	
	@EnumValue("PH")
	Php, 
	
	@EnumValue("PY")
	Python, 
	
	@EnumValue("PE")
	Perl, 
	
	@EnumValue("RU")
	Ruby, 
	
	@EnumValue("JS")
	Javascript
}
