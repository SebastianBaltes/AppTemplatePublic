package global;

import java.sql.Date;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.apache.commons.lang.StringUtils;

import play.data.format.Formatters;

public class Html5DateFormatter extends Formatters.SimpleFormatter<Date> {
	
	@Override
	public Date parse(final String input, final Locale locale) throws ParseException {
		DateFormat df = getFormat();
		java.util.Date utilDate = df.parse(input);
		Date date = new Date(utilDate.getTime());
		return date;
	}

	@Override
	public String print(final Date date, final Locale locale) {
		if (date == null) {
			return "";
		}
		DateFormat df = getFormat();
		return df.format(date);
	}
	
	private static DateFormat getFormat() {
		// HTML5 type="date" ist nicht locale abhängig!
		return new SimpleDateFormat("yyyy-MM-dd");
	}

	public static String reformatLocale(String input, Locale locale) {
		if (StringUtils.isEmpty(input)) {
			return "";
		}
		try {
			java.util.Date date = getFormat().parse(input);
			return formatLocale(date,locale);
		} catch (ParseException e) {
			throw new RuntimeException(e.getMessage(),e);
		}
	}
	
	public static String formatLocale(java.util.Date date, Locale locale) {
		if (date==null) { 
			return "";
		}
		DateFormat localeFormat = DateFormat.getDateInstance(DateFormat.MEDIUM,locale);
		return localeFormat.format(date);
	}
}