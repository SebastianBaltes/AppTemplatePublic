package controllers.site;

import models.User;
import play.Logger;
import play.data.Form;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;
import authenticate.Authenticated;
import authenticate.site.SiteSecured;
import controllers.forms.FlashScope;
import controllers.forms.OptionalUserProfileForm;
import controllers.forms.PasswordChangeForm;

@Security.Authenticated(SiteSecured.class)
public class UserProfileController extends Controller {

	private static final Form<PasswordChangeForm> passwordChangeForm = form(PasswordChangeForm.class);
	private static final Form<OptionalUserProfileForm> optionalUserProfileForm = form(OptionalUserProfileForm.class);

	public static Result index() {
		final User authUser = Authenticated.getAuthenticatedUser();

		return ok(views.html.site.userProfile.render(
				passwordChangeForm.fill(new PasswordChangeForm(authUser.getEmail())),
				optionalUserProfileForm.fill(new OptionalUserProfileForm(authUser))));
	}

	public static Result changePassword() {
		final User authUser = Authenticated.getAuthenticatedUser();
		final Form<PasswordChangeForm> requestForm = passwordChangeForm.bindFromRequest();

		if (requestForm.hasErrors()) {
			flash().put(FlashScope.ERROR, "Fehler beim Ausfüllen des Formulars!");
			return badRequest(views.html.site.userProfile.render(requestForm,
					optionalUserProfileForm.fill(new OptionalUserProfileForm(authUser))));
		}
		
		final PasswordChangeForm myForm = requestForm.get();
		if (! myForm.isPasswordMatching()) {
			flash().put(FlashScope.ERROR, "Die beiden Passwörter sind nicht gleich!");
			return badRequest(views.html.site.userProfile.render(requestForm,
					optionalUserProfileForm.fill(new OptionalUserProfileForm(authUser))));
		}
		
		final User reloadUser = User.findById(authUser.getId());
		reloadUser.setPasswordHash(Authenticated.createHash(myForm.getPassword1()));
		reloadUser.update();
		
		flash().put(FlashScope.SUCCESS, "Das Passwort wurde erfolgreich geändert!");
		return redirect(routes.UserProfileController.index());
	}

	public static Result changeData() {
		final User authUser = Authenticated.getAuthenticatedUser();
		final Form<OptionalUserProfileForm> requestForm = optionalUserProfileForm.bindFromRequest();
		
		if (requestForm.hasErrors()) {
			flash().put(FlashScope.ERROR, "Fehler beim Ausfüllen des Formulars!");
			return badRequest(views.html.site.userProfile.render(
					passwordChangeForm.fill(new PasswordChangeForm(authUser.getEmail())), requestForm));
		}
		
		final OptionalUserProfileForm  myForm = requestForm.get();
		
		final User u = myForm.buildUser();
		u.setId(authUser.getId());
		u.update();
		
		Authenticated.invalidateCachedUser();
		
		flash().put(FlashScope.SUCCESS, "Ihr Profil wurde erfolgreich geändert!");
		return redirect(routes.UserProfileController.index());
	}

}
