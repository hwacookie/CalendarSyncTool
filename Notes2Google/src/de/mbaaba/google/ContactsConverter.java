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
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.Set;
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
 * This is a test template
 */

public class ContactsConverter {

	private String password;
	private String username;

	Properties vw = new Properties();
	private List<String> allNumbers;

	public ContactsConverter(String aUsername, String aPassword) {
		username = aUsername;
		password = aPassword;
		InputStream inStream = getClass().getClassLoader().getResourceAsStream(
				"vorwahlen.txt");
		try {
			vw.load(inStream);
			inStream.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Set<Object> allKeys = vw.keySet();
		allNumbers = new ArrayList<String>();
		for (Object object : allKeys) {
			allNumbers.add(object.toString());
		}
	}

	public void convertAll() {
		try {

			// Create a new Contacts service
			ContactsService myService = new ContactsService("My Application");
			myService.setUserCredentials(username, password);

			// Get a list of all entries
			URL metafeedUrl = new URL(
					"http://www.google.com/m8/feeds/contacts/" + username
							+ "/base");
			CalendarSyncTool.println("Getting Contacts entries...\n");
			ContactFeed resultFeed = myService.getFeed(metafeedUrl,
					ContactFeed.class);

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

	private void convertEntryBlock(List<ContactEntry> entries)
			throws IOException, ServiceException {
		for (ContactEntry contactEntry : entries) {

			boolean fixed = fixTitle(contactEntry);
			fixed = fixed || fixPhoneNumbers(contactEntry);

			if (fixed) {
				contactEntry.update();
			}

			if (!((contactEntry.hasImAddresses())
					|| contactEntry.hasPhoneNumbers()
					|| contactEntry.hasPostalAddresses() || contactEntry
					.hasEmailAddresses())) {
				CalendarSyncTool.printerr("Contact \""
						+ contactEntry.getTitle().getPlainText()
						+ "\" contains no information whatsoever");
			}
		}
		CalendarSyncTool.println("\nTotal Entries: " + entries.size());
	}

	private boolean fixPhoneNumbers(ContactEntry contactEntry) {
		if (contactEntry.hasPhoneNumbers()) {
			List<PhoneNumber> phoneNumbers = contactEntry.getPhoneNumbers();
			for (PhoneNumber phone : phoneNumbers) {
				String s = phone.getPhoneNumber().trim();
				s = fixPhoneNumber(s);
			}
		}
		return false;
	}

	private String fixPhoneNumber(String s) {
		String country = "+49";
		String city = "";
		String number = "";
		String res = "";
		String noCountry = s;
		if (s.startsWith("+49") && (s.length() > 3)) {
			noCountry = s.substring(3).trim();
		}

		// remove "()"
		if (noCountry.startsWith("(")) {
			noCountry = noCountry.substring(1);
		}
		int idx = noCountry.indexOf(")");
		if (idx > 0) {
			noCountry = noCountry.substring(0, idx)
					+ noCountry.substring(idx + 1);
		}

		if (!noCountry.startsWith("0")) {
			noCountry = "0" + noCountry;
		}
		for (String thisVw : allNumbers) {
			if (noCountry.startsWith(thisVw)) {
				number = noCountry.substring(thisVw.length());
				city = thisVw.substring(1);
				break;
			}
		}
		if (city.length() > 0) {
			res = country.trim() + " " + city.trim() + " " + number.trim();
		}

		return res;
	}

	private boolean fixTitle(ContactEntry contactEntry) {
		if (contactEntry.hasEmailAddresses()) {
			TextConstruct title = contactEntry.getTitle();
			List<Email> emailAddresses = contactEntry.getEmailAddresses();
			for (Email email : emailAddresses) {
				String s = email.getDisplayName();

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
				if ((newTitle != null)
						&& (!newTitle.equals(title.getPlainText()))) {
					CalendarSyncTool.println("Will replace title \""
							+ title.getPlainText() + "\" with \"" + newTitle
							+ "\"");
					setTitle(contactEntry, newTitle);
					return true;
				}
			}
		}
		return false;
	}

	private static void setTitle(ContactEntry contactEntry, String t2) {
		TextConstruct title = TextConstruct.create(Type.TEXT, t2, null);
		contactEntry.setTitle(title);
	}

	private String prettyTitle(String address) {
		address = convertCamelCase(address);
		String res = "";
		StringTokenizer tok = new StringTokenizer(address, ".-_ ");
		while (tok.hasMoreTokens()) {
			String token = tok.nextToken();
			token = uppercaseFirstChar(token);
			token = addDotIfShort(token);
			token = removeNumbers(token);
			res = res + " " + token;
		}
		res = res.trim();
		StringTokenizer tok2 = new StringTokenizer(address, ",");
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

	private String removeNumbers(String token) {
		String regex = "(\\d+)(.*)";
		Pattern p = Pattern.compile(regex);
		Matcher m = p.matcher(token);
		String res = token;
		if (m.matches()) {
			res = m.group(2);
		} else {
			regex = "(\\D+)(\\d*)";
			p = Pattern.compile(regex);
			m = p.matcher(token);
			if (m.matches()) {
				res = m.group(1);
			}
		}

		return res;
	}

	private String uppercaseFirstChar(String first) {
		if ("von".equals(first)) {
			return first;
		}
		if (first.length() > 1) {
			return first.substring(0, 1).toUpperCase() + first.substring(1);
		} else if (first.length() == 1) {
			return first.substring(0, 1).toUpperCase();
		} else {
			return "";
		}
	}

	private String addDotIfShort(String first) {
		if (first.length() == 1) {
			first = first + ".";
		}
		return first;
	}

	private String convertCamelCase(String full) {
		String res = "";
		char[] charArray = full.toCharArray();
		int lastI = 0;
		for (int i = 1; i < charArray.length; i++) {
			char c0 = charArray[i - 1];
			char c1 = charArray[i];
			if ((Character.isLowerCase(c0)) && (Character.isUpperCase(c1))) {
				res = res + full.substring(lastI, i) + " ";
				lastI = i;
			}
		}
		if (lastI > 0) {
			res = res + full.substring(lastI);
		} else {
			res = full;
		}
		return res;
	}

	public static void main(String[] args) {
		System.getProperties().put("proxySet", "true");
		System.getProperties().put("proxyHost", "10.0.13.240");
		System.getProperties().put("proxyPort", "4834");
		ContactsConverter contactsConverter = new ContactsConverter(args[0],
				args[1]);

		contactsConverter.convertAll();

		System.out.println(contactsConverter
				.fixPhoneNumber("+49 (30) 72325773"));
		System.out.println(contactsConverter.fixPhoneNumber("+493072325773"));
		System.out.println(contactsConverter.fixPhoneNumber("03320323332"));
		System.out.println(contactsConverter.fixPhoneNumber("033203 23332"));
		// contactsConverter.convertAll();
	}
}
