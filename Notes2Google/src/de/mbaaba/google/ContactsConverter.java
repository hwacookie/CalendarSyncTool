/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.google;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.LineNumberReader;
import java.io.Reader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.StringTokenizer;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.google.gdata.client.contacts.ContactsService;
import com.google.gdata.data.ITextConstruct.Type;
import com.google.gdata.data.Link;
import com.google.gdata.data.TextConstruct;
import com.google.gdata.data.contacts.ContactEntry;
import com.google.gdata.data.contacts.ContactFeed;
import com.google.gdata.data.extensions.Email;
import com.google.gdata.data.extensions.PhoneNumber;
import com.google.gdata.util.AuthenticationException;
import com.google.gdata.util.ServiceException;

import de.mbaaba.calendar.CalendarSyncTool;

/**
 * The Class ContactsConverter is used to normalize contacts (fixes phone numbers, creates names from eMail Adresses etc.)
 */
public class ContactsConverter {

	private static final int THREE_CHARS = 3;

	private String password;

	private String username;

	HashMap<String, String> areaCode2City;

	public ContactsConverter(String aUsername, String aPassword) {
		username = aUsername;
		password = aPassword;
		areaCode2City = new HashMap<String, String>();
		try {
			readPrefixes();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void readPrefixes() throws IOException {
		InputStream inStream = getClass().getClassLoader().getResourceAsStream("vorwahlen.txt");

		Reader reader = new InputStreamReader(inStream);
		LineNumberReader lnr = new LineNumberReader(reader);
		String number = lnr.readLine();
		String place = lnr.readLine().trim();
		while (number != null) {
			areaCode2City.put(number, place);
			number = lnr.readLine();
			place = lnr.readLine().trim();
		}
	}

	public void convertAll() {
		try {

			// Create a new Contacts service
			ContactsService myService = new ContactsService("My Application");
			myService.setUserCredentials(username, password);

			// Get a list of all entries
			URL metafeedUrl = new URL("http://www.google.com/m8/feeds/contacts/" + username + "/base");
			CalendarSyncTool.println("Getting Contacts entries...\n");
			ContactFeed resultFeed = myService.getFeed(metafeedUrl, ContactFeed.class);

			List<ContactEntry> entries = resultFeed.getEntries();
			convertEntryBlock(entries);
			Link linkToNextPage = resultFeed.getNextLink();
			while (linkToNextPage != null) {
				metafeedUrl = new URL(linkToNextPage.getHref());
				resultFeed = myService.getFeed(metafeedUrl, ContactFeed.class);
				entries = resultFeed.getEntries();
				convertEntryBlock(entries);
				linkToNextPage = resultFeed.getNextLink();
			}
		} catch (AuthenticationException e) {
			e.printStackTrace();
		} catch (MalformedURLException e) {
			e.printStackTrace();
		} catch (ServiceException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	private void convertEntryBlock(List<ContactEntry> aEntries) throws IOException, ServiceException {
		for (ContactEntry contactEntry : aEntries) {

			boolean fixed = fixTitle(contactEntry);
			fixed = fixed || fixPhoneNumbers(contactEntry);

			if (fixed) {
				contactEntry.update();
			}

			if (!((contactEntry.hasImAddresses()) || contactEntry.hasPhoneNumbers() || contactEntry.hasPostalAddresses() || contactEntry
					.hasEmailAddresses())) {
				CalendarSyncTool.printerr("Contact \"" + contactEntry.getTitle().getPlainText()
						+ "\" contains no information whatsoever");
			}
		}
		CalendarSyncTool.println("\nTotal Entries: " + aEntries.size());
	}

	private boolean fixPhoneNumbers(ContactEntry aContactEntry) {
		if (aContactEntry.hasPhoneNumbers()) {
			List<PhoneNumber> phoneNumbers = aContactEntry.getPhoneNumbers();
			for (PhoneNumber phone : phoneNumbers) {
				String s = phone.getPhoneNumber().trim();
				s = fixPhoneNumber(s);
			}
		}
		return false;
	}

	private String fixPhoneNumber(String aString) {
		String country = "+49";
		String city = "";
		String number = "";
		String res = "";
		String areaCodeString = aString;
		if (aString.startsWith("+49") && (aString.length() > THREE_CHARS)) {
			areaCodeString = aString.substring(THREE_CHARS).trim();
		}

		// remove "()"
		if (areaCodeString.startsWith("(")) {
			areaCodeString = areaCodeString.substring(1);
		}
		int idx = areaCodeString.indexOf(")");
		if (idx > 0) {
			areaCodeString = areaCodeString.substring(0, idx) + areaCodeString.substring(idx + 1);
		}

		if (!areaCodeString.startsWith("0")) {
			areaCodeString = "0" + areaCodeString;
		}

		city = areaCode2City.get(areaCodeString);

		if (city.length() > 0) {
			res = country.trim() + " " + city.trim() + " " + number.trim();
		}

		return res;
	}

	private boolean fixTitle(ContactEntry aContactEntry) {
		if (aContactEntry.hasEmailAddresses()) {
			TextConstruct title = aContactEntry.getTitle();
			List<Email> emailAddresses = aContactEntry.getEmailAddresses();
			for (Email email : emailAddresses) {
				//String s = email.getDisplayName();

				String full = email.getAddress();
				int atPos = full.indexOf("@");
				if (atPos >= 0) {
					full = full.substring(0, atPos);
				}
				String newTitle = null;
				if ((title.getPlainText().length() == 0)) {
					newTitle = prettyTitle(full);
				} else {
					full = title.getPlainText();
					atPos = full.indexOf("@");
					if (atPos >= 0) {
						full = full.substring(0, atPos);
					}
					newTitle = prettyTitle(full);
				}
				if ((newTitle != null) && (!newTitle.equals(title.getPlainText()))) {
					CalendarSyncTool.println("Will replace title \"" + title.getPlainText() + "\" with \"" + newTitle + "\"");
					setTitle(aContactEntry, newTitle);
					return true;
				}
			}
		}
		return false;
	}

	private static void setTitle(ContactEntry aContactEntry, String aTitle) {
		TextConstruct title = TextConstruct.create(Type.TEXT, aTitle, null);
		aContactEntry.setTitle(title);
	}

	private String prettyTitle(String aAddress) {
		aAddress = convertCamelCase(aAddress);
		String res = "";
		StringTokenizer tok = new StringTokenizer(aAddress, ".-_ ");
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			token = uppercaseFirstChar(token);
			token = addDotIfShort(token);
			token = removeNumbers(token);
			res = res + " " + token;
		}
		res = res.trim();
		StringTokenizer tok2 = new StringTokenizer(aAddress, ",");
		if (tok2.hasMoreTokens()) {
			String last = tok2.nextToken().trim();
			if (tok2.hasMoreTokens()) {
				String first = tok2.nextToken().trim();
				res = first + " " + last;
			}
		}
		res = res.trim();
		return res.trim();
	}

	private String removeNumbers(String aToken) {
		String regex = "(\\d+)(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(aToken);
		String res = aToken;
		if (m.matches()) {
			res = m.group(2);
		} else {
			regex = "(\\D+)(\\d*)";
			p = Pattern.compile(regex);
			m = p.matcher(aToken);
			if (m.matches()) {
				res = m.group(1);
			}
		}

		return res;
	}

	private String uppercaseFirstChar(String aString) {
		if ("von".equals(aString)) {
			return aString;
		}
		if (aString.length() > 1) {
			return aString.substring(0, 1).toUpperCase() + aString.substring(1);
		} else if (aString.length() == 1) {
			return aString.substring(0, 1).toUpperCase();
		} else {
			return "";
		}
	}

	private String addDotIfShort(String aString) {
		if (aString.length() == 1) {
			aString = aString + ".";
		}
		return aString;
	}

	private String convertCamelCase(String aFullName) {
		String res = "";
		char[] charArray = aFullName.toCharArray();
		int lastI = 0;
		for (int i = 1; i < charArray.length; i++) {
			char c0 = charArray[i - 1];
			char c1 = charArray[i];
			if ((Character.isLowerCase(c0)) && (Character.isUpperCase(c1))) {
				res = res + aFullName.substring(lastI, i) + " ";
				lastI = i;
			}
		}
		if (lastI > 0) {
			res = res + aFullName.substring(lastI);
		} else {
			res = aFullName;
		}
		return res;
	}

	public static void main(String[] aArgs) {
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("proxyHost", "10.0.13.240");
		System.getProperties().put("proxyPort", "4834");
		ContactsConverter contactsConverter = new ContactsConverter(aArgs[0], aArgs[1]);

		contactsConverter.convertAll();
	}
}
