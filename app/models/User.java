package models;

import java.sql.Timestamp;
import java.util.List;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Version;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
@Table(name = "user_account")
public class User extends Model {

	private static final Finder<Long, User> finder = new Finder<Long, User>(Long.class, User.class);

	@Id
	private Long id;
	private String email;
	private String passwordHash;
	@ManyToOne
	private Role role;
	private String timezone = "Europe/Berlin";
    @Version
    private Timestamp lastUpdate;
	private boolean validated;

	// optional fields
	private String firstname;
	private String surname;
	private String street;
	private String address;
	private String country;

	@Column(name="random_pwrecover")
	private String randomPasswordRecoveryString;
	@Column(name="pwrecover_triggered")
	private Timestamp randomPasswordRecoveryTriggerDate;

	public static List<User> findAll() {
		return finder.all();
	}

	public static User findById(final Long id) {
		return finder.byId(id);
	}

	public static User findByEmail(final String email) {
		return finder.where().eq("email", email).findUnique();
	}

	public static void deleteById(final Long id) {
		finder.ref(id).delete();
	}

	public Long getId() {
		return id;
	}

	public void setId(Long _id) {
		id = _id;
	}

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
		return role;
	}

	public void setRole(Role _role) {
		role = _role;
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
	public int hashCode() {
		final int prime = 31;
		int result = super.hashCode();
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) return true;
		if (!super.equals(obj)) return false;
		if (getClass() != obj.getClass()) return false;
		User other = (User) obj;
		if (id == null) {
			if (other.id != null) return false;
		} else if (!id.equals(other.id)) return false;
		return true;
	}

	@Override
	public String toString() {
		return "User [id=" + id + ", email=" + email + ", role=" + role + ", timezone=" + timezone + ", lastModified="
				+ lastModified + ", firstname=" + firstname + ", surname=" + surname + ", street=" + street
				+ ", address=" + address + ", country=" + country + ", randomPasswordRecoveryTriggerDate="
				+ randomPasswordRecoveryTriggerDate + "]";
	}

}
