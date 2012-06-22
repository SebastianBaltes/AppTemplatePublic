package global;

import java.sql.Timestamp;
import java.text.ParseException;
import java.util.Locale;

import play.data.format.Formatters;

public class TimeStampFormatter extends Formatters.SimpleFormatter<Timestamp> {
	
	@Override
	public Timestamp parse(final String input,
			final Locale locale) throws ParseException {
		return Timestamp.valueOf(input);
	}

	@Override
	public String print(final Timestamp timestamp,
			final Locale locale) {
		if (timestamp == null) {
			return "";
		}
		return timestamp.toString();
	}
}