package controllers.forms;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import play.data.validation.Constraints.Email;
import play.data.validation.Constraints.Required;

public class EMailForm {

	@Required
	@Email
	private String email;

	public EMailForm() {
	}
	
	public EMailForm(String _email) {
		email = _email;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String _email) {
		email = _email;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
