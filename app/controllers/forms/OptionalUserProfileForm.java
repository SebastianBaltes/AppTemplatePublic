package controllers.forms;

import models.User;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import utils.CountryHelper;


public class OptionalUserProfileForm {
	
	// optional fields:
	private String firstname;
	private String surname;
	private String street;
	private String address;
	private Integer countryListIndex;
	
	public OptionalUserProfileForm() {
	}
	
	public OptionalUserProfileForm(final User user) {
		this.firstname = user.getFirstname();
		this.surname = user.getSurname();
		this.street = user.getStreet();
		this.address = user.getAddress();
		this.countryListIndex = CountryHelper.countryList.indexOf(user.getCountry());
	}
	
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
		u.setFirstname(firstname);
		u.setSurname(surname);
		u.setStreet(street);
		u.setAddress(address);
		u.setCountry(countryListIndex == null ? null : CountryHelper.countryList.get(countryListIndex));
		return u;
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
