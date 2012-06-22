package models;

import java.sql.Timestamp;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Table;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_account")
public class User extends CrudModel<User> {

    public static final UserFinder find = new UserFinder();

	public String email;
	public String passwordHash;
	public String role;
	public String timezone = "Europe/Berlin";
	public boolean validated;

	// optional fields
	public String firstname;
	public String surname;
	public String address;
	public String country;
	public String zipCode;
	public String city;

	@Column(name="random_pwrecover")
	public String randomPasswordRecoveryString;
	@Column(name="pwrecover_triggered")
	public Timestamp randomPasswordRecoveryTriggerDate;


	public String getEmail() {
		return email;
	}

	public void setEmail(String _email) {
		email = _email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String _passwordHash) {
		passwordHash = _passwordHash;
	}

	public Role getRole() {
		return Role.valueOf(role);
	}

	public void setRole(Role _role) {
		role = _role.name();
	}

	public String getTimezone() {
		return timezone;
	}

	public void setTimezone(String _timezone) {
		timezone = _timezone;
	}
	
	public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public void setValidated(boolean validated) {
		this.validated = validated;
	}

	public boolean isValidated() {
		return validated;
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

	public String getCity() {
		return city;
	}

	public void setCity(String city) {
		this.city = city;
	}

	public String getZipCode() {
		return zipCode;
	}

	public void setZipCode(String zipCode) {
		this.zipCode = zipCode;
	}

	public String getAddress() {
		return address;
	}

	public void setAddress(String _address) {
		address = _address;
	}

	public String getCountry() {
		return country;
	}

	public void setCountry(String _country) {
		country = _country;
	}

	public String getRandomPasswordRecoveryString() {
		return randomPasswordRecoveryString;
	}

	public void setRandomPasswordRecoveryString(String _randomPasswordRecoveryString) {
		randomPasswordRecoveryString = _randomPasswordRecoveryString;
	}

	public Timestamp getRandomPasswordRecoveryTriggerDate() {
		return randomPasswordRecoveryTriggerDate;
	}

	public void setRandomPasswordRecoveryTriggerDate(Timestamp _randomPasswordRecoveryTriggerDate) {
		randomPasswordRecoveryTriggerDate = _randomPasswordRecoveryTriggerDate;
	}

    @Override
    public String label() {
        return email;
    }

	@Override
	protected CrudFinder<User> getCrudFinder() {
		return find;
	}

	public static class UserFinder extends CrudFinder<User> {
		
		public UserFinder() {
			super(new Finder<Long, User>(Long.class, User.class),"email");
		}

		public User byEmail(String email) {
			return byLabel(email);
		}
	}
	
}
