package global;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Locale;

import play.data.format.Formatters;

public class BigDecimalFormatter extends Formatters.SimpleFormatter<BigDecimal> {
	
	@Override
	public BigDecimal parse(final String input, final Locale locale) throws ParseException {
		DecimalFormat format = getDecimalFormat(locale);
		return (BigDecimal)format.parse(input);
	}

	@Override
	public String print(final BigDecimal bigdecimal, final Locale locale) {
		if (bigdecimal == null) {
			return "";
		}
		DecimalFormat format = getDecimalFormat(locale);
		return format.format(bigdecimal);
	}
	
	private DecimalFormat getDecimalFormat(final Locale locale) {
		DecimalFormat decfrm = (DecimalFormat)NumberFormat.getInstance(locale);
		decfrm.setParseBigDecimal(true);
		return decfrm;
	}
}
