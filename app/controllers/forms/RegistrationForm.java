package controllers.forms;

import java.sql.Timestamp;

import javax.validation.Valid;

import models.User;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import utils.CountryHelper;

public class RegistrationForm {

	@Valid
	private PasswordChangeForm mandatory;

	// optional fields:
	private String firstname;
	private String surname;
	private String street;
	private String address;
	private Integer countryListIndex;
	
	public String validate() {
		if (countryListIndex != null) {
			try {
				CountryHelper.countryList.get(countryListIndex);
			}
			catch(final IndexOutOfBoundsException e) {
				return "Ungültiges Land";
			}
		}
		return null;
	}

	public User buildUser() {
		final User u = new User(); 
		u.setEmail(mandatory.getEmail());
		u.setFirstname(firstname);
		u.setSurname(surname);
		u.setStreet(street);
		u.setAddress(address);
		u.setCountry(countryListIndex == null ? null : CountryHelper.countryList.get(countryListIndex));
		u.setLastModified(new Timestamp(System.currentTimeMillis()));
		return u;
	}
	
	public PasswordChangeForm getMandatory() {
		return mandatory;
	}

	public void setMandatory(PasswordChangeForm _mandatory) {
		mandatory = _mandatory;
	}

	public String getFirstname() {
		return firstname;
	}

	public void setFirstname(String _firstname) {
		firstname = _firstname;
	}

	public String getSurname() {
		return surname;
	}

	public void setSurname(String _surname) {
		surname = _surname;
	}

	public String getStreet() {
		return street;
	}

	public void setStreet(String _street) {
		street = _street;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String _address) {
		address = _address;
	}

	public Integer getCountryListIndex() {
		return countryListIndex;
	}

	public void setCountryListIndex(Integer _countryListIndex) {
		countryListIndex = _countryListIndex;
	}
	
	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}	

}
