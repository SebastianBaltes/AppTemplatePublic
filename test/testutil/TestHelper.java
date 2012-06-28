package testutil;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertFalse;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertNull;
import static org.junit.Assert.assertTrue;
import static play.test.Helpers.contentType;
import static play.test.Helpers.flash;
import static play.test.Helpers.status;
import static play.mvc.Http.Status.BAD_REQUEST;
import static play.mvc.Http.Status.OK;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.junit.Assert;

import models.User;

import play.mvc.Http.Flash;
import play.mvc.Result;

import authenticate.Authenticated;

import com.avaje.ebean.Ebean;

import controllers.forms.FlashScope;

public class TestHelper {

	@SuppressWarnings("unchecked")
	public static int getRowCount(final Class clazz) {
		return Ebean.createQuery(clazz).findRowCount();
	}

	public static <T> T findLast(final Class<T> clazz) {
		final List<T> findList = Ebean.createQuery(clazz).order("id DESC").findList();
		if (findList != null && !findList.isEmpty()) {
			return findList.get(0);
		}
		return null;
	}

	public static void assertFlashSuccess(final Result r, final String containsMessage) {
		final Flash f = flash(r);
		assertFalse(f.isEmpty());
		assertNull(f.get(FlashScope.ERROR));
		assertNull(f.get(FlashScope.WARN));
		assertNotNull(f.get(FlashScope.SUCCESS));
		assertTrue(f.get(FlashScope.SUCCESS).contains(containsMessage));
	}

	public static void assertFlashError(final Result r, final String containsMessage) {
		final Flash f = flash(r);
		assertFalse(f.isEmpty());
		assertNotNull(f.get(FlashScope.ERROR));
		assertNull(f.get(FlashScope.WARN));
		assertNull(f.get(FlashScope.SUCCESS));
		assertTrue(f.get(FlashScope.ERROR).contains(containsMessage));
	}

	public static void assertResultOk(final Result r) {
		assertNotNull(r);
		assertEquals(OK, status(r));
		assertEquals("text/html", contentType(r));
	}

	public static void assertResultBad(final Result r) {
		assertNotNull(r);
		assertEquals(BAD_REQUEST, status(r));
		assertEquals("text/html", contentType(r));
	}

	public static void assertPasswordHash(final String clearPassword, final User u) {
		assertNotNull(clearPassword);
		assertNotNull(u);
		assertEquals(Authenticated.createHash(clearPassword), u.getPasswordHash());
		assertFalse(clearPassword.equals(u.getPasswordHash()));
	}

	public static List<String> matchGroups(final String text, final String pattern) {
		final Pattern p = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);
		final Matcher matcher = p.matcher(text);
		if (!matcher.find()) return null;

		final int count = matcher.groupCount();
		final List<String> groups = new ArrayList<String>(count);
		for (int i = 0; i <= count; i++)	groups.add(matcher.group(i));
		return groups; 
	}

	public static void log(final String msg) {
		System.err.println("_______________________" + msg);
	}
	
	public static void sleep(final long ms) {
		try {
			Thread.sleep(ms);
		} catch (InterruptedException e) {
			e.printStackTrace();
			Assert.fail();
		}
	}
}
