package com.machinestalk.android.utilities;

import java.io.UnsupportedEncodingException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * Any reusable validation methods like email, phone number, name, postal code
 * validation should be contained here.
 *
 * 
 */
@SuppressWarnings("WeakerAccess")
public final class ValidationUtility {

	private ValidationUtility() {
	}

	/**
	 * Validates if the supplied string meets the criteria of a valid email format.
	 * 
	 * @param emailAddress The input email address string
	 * @return {@code true} if email address is valid, {@code false} otherwise
	 */
	public static boolean isValidEmailAddress (CharSequence emailAddress) {

		Pattern pattern;
		Matcher matcher;
		String emailPattern = "^[_A-Za-z0-9-]+(\\.[_A-Za-z0-9-]+)*@[A-Za-z0-9]+(\\.[A-Za-z0-9]+)*(\\.[A-Za-z]{2,})$";
		pattern = Pattern.compile (emailPattern);
		matcher = pattern.matcher (emailAddress);
		return matcher.matches ();
	}

	/**
	 * Validates if the the supplied string is a valid phone number
	 * @param phoneNumber
	 * @return
	 */
	public static boolean isValidPhoneNumber (String phoneNumber) {
//		Pattern pattern1 = Pattern.compile ("\\d{3}-\\d{7}");
//		Pattern pattern2 = Pattern.compile ("\\d{9}");
//		Matcher matcher1 = pattern1.matcher (phoneNumber);
//		Matcher matcher2 = pattern2.matcher (phoneNumber);
		
		return (phoneNumber.startsWith("5") || phoneNumber.startsWith("٥"))&& compterOccurrences(phoneNumber) == 9;
	}
	public static boolean isLessThanPhoneNumber (String phoneNumber) {
//		Pattern pattern1 = Pattern.compile ("\\d{3}-\\d{7}");
//		Pattern pattern2 = Pattern.compile ("\\d{9}");
//		Matcher matcher1 = pattern1.matcher (phoneNumber);
//		Matcher matcher2 = pattern2.matcher (phoneNumber);

		return  compterOccurrences(phoneNumber) < 9;
	}


	public static int compterOccurrences(String maChaine)
	{
		int nb = 0;
		for (int i=0; i < maChaine.length(); i++)
		{
				nb++;
		}
		return nb;
	}
	public static String replaceArabicNumbers(String original) {
		return original
				.replaceAll("١","1")
				.replaceAll("٢","2")
				.replaceAll("٣","3")
				.replaceAll("٤","4")
				.replaceAll("٥","5")
				.replaceAll("٦","6")
				.replaceAll("٧","7")
				.replaceAll("٨","8")
				.replaceAll("٩","9");

	}

	/**
	 * Validates if the provided string meets the criteria of a valid name.
	 * <br/> NOTE : Just checks if string is empty or not
	 * @param name The input name string
	 * @return {@code true} if name is valid, {@code false} otherwise
	 */
	public static boolean isValidName (String name) {

		return !StringUtility.isEmptyOrNull (name);
	}
}
