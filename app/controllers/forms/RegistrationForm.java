package controllers.forms;

import javax.validation.Valid;
import models.User;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class RegistrationForm {

	@Valid
	private PasswordChangeForm mandatory;
	@Valid
	private OptionalUserProfileForm optionalUserProfileForm;

	public User buildUser() {
		final User u = optionalUserProfileForm.buildUser();
		u.setEmail(mandatory.getEmail());
		return u;
	}

	public PasswordChangeForm getMandatory() {
		return mandatory;
	}

	public void setMandatory(PasswordChangeForm _mandatory) {
		mandatory = _mandatory;
	}

	public OptionalUserProfileForm getOptionalUserProfileForm() {
		return optionalUserProfileForm;
	}

	public void setOptionalUserProfileForm(OptionalUserProfileForm _optionalUserProfileForm) {
		optionalUserProfileForm = _optionalUserProfileForm;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
