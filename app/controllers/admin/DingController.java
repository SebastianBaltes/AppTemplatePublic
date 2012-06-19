package controllers.admin;

import java.util.Collection;

import models.Ding;

import org.codehaus.jackson.node.ArrayNode;
import org.codehaus.jackson.node.ObjectNode;

import play.Logger;
import play.data.Form;
import play.db.ebean.Transactional;
import play.mvc.Controller;
import play.mvc.Result;
import play.mvc.Security;

import com.avaje.ebean.Page;

//@Security.Authenticated(Secured.class)
public class DingController extends Controller {

	private static Form<Ding> testForm = form(Ding.class);

	public static Result tests() {
		return redirect("/ding/list");
	}
 
//	public static Result list(final int page, final String sortBy,
//			final String order, final String filter) {
//		return ok(views.html.testList.render(User.getAuthenticatedUser(),
//				Ding.page(org.id, page, 10, sortBy, order, filter), sortBy,
//				order, filter));
//	}
//
//	public static Result create() {
//		Logger.info("create()");
//		final Ding testEdit = new Ding();
//		testEdit.setOrganisation(User.getAuthenticatedUser().getOrg());
//		final Form<Ding> form = testForm.fill(testEdit);
//		return ok(views.html.testEntity.render(User.getAuthenticatedUser(),
//				form, ViewType.create));
//	}
//
//	private static interface ShowResultClosure {
//		public Result create(User user, Ding test, Form<Ding> form,
//				ViewType viewType);
//	}
//
//	public static Result view(final Long id, final int questionPage,
//			final String questionSortBy, final String questionOrder,
//			final String questionFilter) {
//		return show(id, ViewType.view, new ShowResultClosure() {
//			@Override
//			public Result create(final User user, final Ding test,
//					final play.data.Form<Ding> form, final ViewType viewType) {
//				final Page<Question> page = Question.page(test.getId(),
//						questionPage, 10, questionSortBy, questionOrder,
//						questionFilter);
//				return ok(views.html.testDetails.render(
//						User.getAuthenticatedUser(), test.getId(), form, page,
//						questionSortBy, questionOrder, questionFilter, viewType));
//			};
//		});
//	}
//
//	public static Result edit(final Long id) {
//		return show(id, ViewType.update, new ShowResultClosure() {
//			@Override
//			public Result create(final User user, final Ding test,
//					final Form<Ding> form, final ViewType viewType) {
//				return ok(views.html.testEntity.render(
//						User.getAuthenticatedUser(), form, viewType));
//			}
//		});
//	}
//
//	public static Result update() {
//		return save(ViewType.update);
//	}
//
//	public static Result save() {
//		return save(ViewType.create);
//	}
//
//	private static Result show(final Long id, final ViewType viewType,
//			final ShowResultClosure showResultClojure) {
//		Logger.info("show(" + id + ")");
//		final Ding test = Ding.findById(id);
//		if (test == null) {
//			return notFound("Ding nicht vorhanden!");
//		}
//		for (final User user : test.getUsers()) {
//			Logger.info("user=" + user);
//		}
//		if (isForbidden(viewType, test.getOrganisation().id)) {
//			return forbidden("forbidden");
//		} else {
//			final Form<Ding> form = testForm.fill(test);
//			return showResultClojure.create(User.getAuthenticatedUser(), test,
//					form, viewType);
//		}
//	}
//
//	private static boolean isForbidden(final ViewType viewType, final Long orgId) {
//		return !Secured.isOrgAccessAllowed(session("uuid"), orgId);
//	}
//
//	private static Result save(final ViewType viewType) {
//		Logger.info("save(" + viewType + ")");
//		@SuppressWarnings("unchecked")
//		final AdvancedFormHelper<Ding> formHelper = new AdvancedFormHelper<Ding>(
//				testForm,
//				new AdvancedFormHelper.MultiSelectEntityConverter<Ding, User>() {
//					@Override
//					public String getIdRequestPattern() {
//						return "users\\b.*";
//					}
//
//					@Override
//					public Collection<User> getCollection(final Ding entity) {
//						return entity.getUsers();
//					}
//
//					@Override
//					public User findById(final Long id) {
//						return User.findById(id);
//					}
//				});
//		if (formHelper.filledForm.hasErrors()) {
//			flash("error", "Fehler beim Ausfüllen des Formulars!");
//			return badRequest(views.html.testEntity.render(
//					User.getAuthenticatedUser(), formHelper.filledForm,
//					viewType));
//		}
//		final Ding test = formHelper.entity;
//		if (isForbidden(viewType, test.getOrganisation().getId())) {
//			return forbidden("forbidden");
//		}
//		if (!User.getAuthenticatedUser().isSystemAdmin()) {
//			test.setOrganisation(User.getAuthenticatedUser().getOrg());
//		}
//		test.saveOrUpdate();
//		flash("success", "Ding " + test.getName() + " " + viewType
//				+ " erfolgreich");
//		return redirect(routes.TestController.tests());
//	}
//
//	@Transactional
//	public static Result delete(final Long id) {
//		Logger.info("delete(" + id + ")");
//		final Ding test = Ding.findById(id);
//		if (test == null) {
//			return notFound("Ding nicht vorhanden!");
//		}
//		if (Secured.isOrgAccessAllowed(session("uuid"), test.getOrganisation()
//				.getId())) {
//			test.deleteCascading();
//			flash("success", "Ding erfolgreich gelöscht");
//			return redirect(controllers.routes.TestController.tests());
//		} else {
//			return forbidden("forbidden");
//		}
//	}
//
}
