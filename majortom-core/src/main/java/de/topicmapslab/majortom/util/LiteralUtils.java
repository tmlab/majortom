/*******************************************************************************
 * Copyright 2010, Topic Map Lab ( http://www.topicmapslab.de )
 * 
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 * 
 *   http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 ******************************************************************************/
package de.topicmapslab.majortom.util;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.net.URI;
import java.net.URISyntaxException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Pattern;

import de.topicmapslab.majortom.model.namespace.Namespaces;

/**
 * Utility class for literals. Checks the type of literals and transform string
 * literals to a literal of a specific type.
 * 
 * @author Sven Krosse
 * @email krosse@informatik.uni-leipzig.de
 * 
 */
public class LiteralUtils {

	/**
	 * regular expression of date
	 */
	private final static Pattern datePattern = Pattern.compile("[-]?[0-9][0-9][0-9][0-9][0-9]*[-](0[1-9]|1[0-2])[-](0[1-9]|[1-2][0-9]|3[0-1])");
	/**
	 * regular expression of time
	 */
	private final static Pattern timePattern = Pattern.compile("[0-9][0-9]:[0-9][0-9]:[0-9][0-9](\\.[0-9]+)?(Z|[+|-][0-9][0-9]:[0-9][0-9])?");
	/**
	 * regular expression of dateTime
	 */
	private final static Pattern dateTimePattern = Pattern.compile(datePattern.pattern() + "T" + timePattern.pattern());
	private final static Pattern alternativeDateTimePattern = Pattern.compile(datePattern.pattern() + " " + timePattern.pattern());
	/**
	 * regular expression of decimal
	 */
	private final static Pattern decimalPattern = Pattern.compile("[+|-]?([0-9]+[.][0-9]+|[.][0-9]+)(E[+|-]?[0-9]{1,5})?");
	/**
	 * regular expression of integer
	 */
	private final static Pattern integerPattern = Pattern.compile("[+|-]?[0-9]+");
	/**
	 * regular expression of char
	 */
	private final static Pattern charPattern = Pattern.compile("[^\"\\\\]");
	/**
	 * regular expression of quoted strings
	 */
	private final static Pattern quotedStringPattern = Pattern.compile("\"" + charPattern.pattern() + "*\"");
	/**
	 * regular expression of triple quoted strings
	 */
	private final static Pattern tripleQuotedStringPattern = Pattern.compile("\"\"\"((\"\"|\")?" + charPattern.pattern() + ")*\"\"\"");

	/**
	 * translation patterns of date
	 */
	private static final List<String> datePatterns = new LinkedList<String>();
	static {
		datePatterns.add("yyyy-MM-dd");
		datePatterns.add("yyyyy-MM-dd");
		datePatterns.add("yyyyyy-MM-dd");
		datePatterns.add("yyyyyyy-MM-dd");
	}
	/**
	 * translation patterns of time
	 */
	private static final List<String> timePatterns = new LinkedList<String>();
	static {
		timePatterns.add("HH:mm:ss");
		timePatterns.add("HH:mm:ss.S");
		timePatterns.add("HH:mm:ss.SS");
		timePatterns.add("HH:mm:ss.SSS");
		timePatterns.add("HH:mm:ss.SSSS");
		timePatterns.add("HH:mm:ss.SSSSS");
		timePatterns.add("HH:mm:ss'Z'");
		timePatterns.add("HH:mm:ss.S'Z'");
		timePatterns.add("HH:mm:ss.SS'Z'");
		timePatterns.add("HH:mm:ss.SSS'Z'");
		timePatterns.add("HH:mm:ss.SSSS'Z'");
		timePatterns.add("HH:mm:ss.SSSSS'Z'");
		timePatterns.add("HH:mm:ss-HH:mm");
		timePatterns.add("HH:mm:ss.S-HH:mm");
		timePatterns.add("HH:mm:ss.SS-HH:mm");
		timePatterns.add("HH:mm:ss.SSS-HH:mm");
		timePatterns.add("HH:mm:ss.SSSS-HH:mm");
		timePatterns.add("HH:mm:ss.SSSSS-HH:mm");
		timePatterns.add("HH:mm:ss+HH:mm");
		timePatterns.add("HH:mm:ss.S+HH:mm");
		timePatterns.add("HH:mm:ss.SS+HH:mm");
		timePatterns.add("HH:mm:ss.SSS+HH:mm");
		timePatterns.add("HH:mm:ss.SSSS+HH:mm");
		timePatterns.add("HH:mm:ss.SSSSS+HH:mm");
	}

	/**
	 * Method checks if the given string literal can be represented as integer
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is an integer literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isInteger(final String literal) {
		return integerPattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as decimal
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is a decimal literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isDecimal(final String literal) {
		return decimalPattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as date
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is a date literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isDate(final String literal) {
		return datePattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as time
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is a time literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isTime(final String literal) {
		return timePattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as dateTime
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is a dateTime literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isDateTime(final String literal) {
		return dateTimePattern.matcher(literal).matches() || alternativeDateTimePattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as quoted
	 * string literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is a quoted string literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isQuotedString(final String literal) {
		return quotedStringPattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as triple
	 * quoted string literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is a triple quoted string
	 *         literal, <code>false</code> otherwise.
	 */
	public static final boolean isTripleQuotedString(final String literal) {
		return tripleQuotedStringPattern.matcher(literal).matches();
	}

	/**
	 * Method checks if the given string literal can be represented as integer
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is an integer literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isString(final String literal) {
		return isQuotedString(literal) || isTripleQuotedString(literal);
	}

	/**
	 * Method checks if the given string literal can be represented as IRI
	 * literal.
	 * 
	 * @param literal
	 *            the literal
	 * @return <code>true</code> if the literal is an IRI literal,
	 *         <code>false</code> otherwise.
	 */
	public static final boolean isIri(final String literal) {
		try {
			new URI(literal);
		} catch (URISyntaxException e) {
			return false;
		}
		return true;
	}

	/**
	 * Method formats the given literal as integer literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the integer literal
	 * @throws NumberFormatException
	 *             thrown if literal cannot be format as number
	 */
	public static final BigInteger asInteger(final String literal) throws NumberFormatException {
		return BigInteger.valueOf(Long.parseLong(literal));
	}

	/**
	 * Method formats the given literal as decimal literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the decimal literal
	 * @throws NumberFormatException
	 *             thrown if literal cannot be format as number
	 */
	public static final BigDecimal asDecimal(final String literal) throws NumberFormatException {
		return BigDecimal.valueOf(Double.parseDouble(literal));
	}

	/**
	 * Method formats the given literal as date literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the date literal
	 * @throws ParseException
	 *             thrown if literal cannot be format as date
	 */
	public static final Calendar asDate(final String literal) throws ParseException {
		Date date = null;
		for (String pattern : datePatterns) {
			try {
				date = new SimpleDateFormat(pattern).parse(literal);
			} catch (ParseException e) {
				// VOID
			}
		}
		if (date == null) {
			throw new ParseException("Invalid date pattern", -1);
		}
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c;
	}

	/**
	 * Method formats the given literal as time literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the time literal
	 * @throws ParseException
	 *             thrown if literal cannot be format as time
	 */
	public static final Calendar asTime(final String literal) throws ParseException {
		Date date = null;
		for (String pattern : timePatterns) {
			try {
				date = new SimpleDateFormat(pattern).parse(literal);
			} catch (ParseException e) {
				// VOID
			}
		}
		if (date == null) {
			throw new ParseException("Invalid time pattern", -1);
		}
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c;
	}

	/**
	 * Method formats the given literal as dateTime literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the dateTime literal
	 * @throws ParseException
	 *             thrown if literal cannot be format as dateTime
	 */
	public static final Calendar asDateTime(final String literal) throws ParseException {
		Date date = null;
		for (String dp : datePatterns) {
			for (String tp : timePatterns) {
				try {
					date = new SimpleDateFormat(dp + "'T'" + tp).parse(literal);
				} catch (ParseException e) {
					// VOID
				}
				try {
					date = new SimpleDateFormat(dp + "' '" + tp).parse(literal);
				} catch (ParseException e) {
					// VOID
				}
			}
		}
		if (date == null) {
			throw new ParseException("Invalid dateTime pattern", -1);
		}
		Calendar c = new GregorianCalendar();
		c.setTime(date);
		return c;
	}

	/**
	 * Method formats the given literal as string literal.
	 * 
	 * @param literal
	 *            the quoted string literal
	 * @return the string literal without quotes
	 */
	public static final String asQuotedString(final String literal) {
		return literal.substring(1, literal.length() - 1);
	}

	/**
	 * Method formats the given literal as string literal.
	 * 
	 * @param literal
	 *            the triple quoted string literal
	 * @return the string literal without quotes
	 */
	public static final String asTripleQuotedString(final String literal) {
		return literal.substring(3, literal.length() - 3);
	}

	/**
	 * Method formats the given literal as string literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the string literal without quotes
	 */
	public static final String asString(final String literal) {
		if (isQuotedString(literal)) {
			return asQuotedString(literal);
		} else if (isTripleQuotedString(literal)) {
			return asTripleQuotedString(literal);
		} else {
			return literal;
		}
	}

	/**
	 * Method formats the given literal as IRI literal.
	 * 
	 * @param literal
	 *            the string literal
	 * @return the string literal without quotes
	 * @throws URISyntaxException
	 *             thrown if given literal isn't a valid IRI
	 */
	public static final URI asIri(final String literal) throws URISyntaxException {
		return new URI(literal);
	}

	/**
	 * Transform the given literal to a literal of the specified type given by
	 * dataType IRI.
	 * 
	 * @param literal
	 *            the literal value to convert
	 * @param datatType
	 *            the IRI of the data-type
	 * @return the transformed literal or a string literal if the value does not
	 *         matches the regular expression of the given type.
	 * @throws Exception
	 *             thrown if transformation fails
	 */
	public static Object asLiteral(final String literal, final String datatType) throws Exception {
		final String dataType_ = XmlSchemeDatatypes.toExternalForm(datatType);
		/*
		 * handle as date?
		 */
		if (dataType_.equalsIgnoreCase(Namespaces.XSD.DATE) && isDate(literal)) {
			return asDate(literal);
		}
		/*
		 * handle as time?
		 */
		else if (dataType_.equalsIgnoreCase(Namespaces.XSD.TIME) && isTime(literal)) {
			return asTime(literal);
		}
		/*
		 * handle as dateTime?
		 */
		else if (dataType_.equalsIgnoreCase(Namespaces.XSD.DATETIME) && isDateTime(literal)) {
			return asDateTime(literal);
		}
		/*
		 * handle as integer?
		 */
		else if (dataType_.equalsIgnoreCase(Namespaces.XSD.INTEGER) && isInteger(literal)) {
			return asInteger(literal);
		}
		/*
		 * handle as decimal?
		 */
		else if ((dataType_.equalsIgnoreCase(Namespaces.XSD.DECIMAL) || dataType_.equalsIgnoreCase(Namespaces.XSD.FLOAT)) && isDecimal(literal)) {
			return asDecimal(literal);
		}
		/*
		 * handle as URI?
		 */
		else if (dataType_.equalsIgnoreCase(Namespaces.XSD.ANYURI) && isIri(literal)) {
			return asIri(literal);
		}
		/*
		 * handle as string
		 */
		return literal;
	}

	/**
	 * Method checks if the second calendar is before or after the first one and
	 * if the distance between the both calendars is smaller than the second
	 * calendar represented time range.
	 * 
	 * @param relative
	 *            the calendar
	 * @param value
	 *            the calendar to check
	 * @param double the range representation
	 * @return <code>true</code> if the second calendar is in the range,
	 *         <code>false</code> otherwise.
	 */
	public static boolean inRange(Calendar relative, Calendar value, double range) {
		long distance = Math.abs(relative.getTimeInMillis() - value.getTimeInMillis());
		return distance <= range;
	}
}
