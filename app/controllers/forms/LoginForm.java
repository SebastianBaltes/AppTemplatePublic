package controllers.forms;

import static play.data.validation.Constraints.*;
import org.apache.commons.lang.builder.ReflectionToStringBuilder;

public class LoginForm {

	@Required
	@Email
	private String email;
	@Required
	@MinLength(4)
	private String password;

	public String getEmail() {
		return email;
	}

	public void setEmail(String _email) {
		email = _email;
	}

	public String getPassword() {
		return password;
	}

	public void setPassword(String _password) {
		password = _password;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
