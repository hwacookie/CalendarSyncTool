/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.notes;

import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.Vector;

import lotus.domino.Database;
import lotus.domino.Document;
import lotus.domino.DocumentCollection;
import lotus.domino.Item;
import lotus.domino.NotesException;
import lotus.domino.Session;
import lotus.domino.View;
import de.mbaaba.calendar.ItemNotFoundException;
import de.mbaaba.calendar.Person;

/**
 * The Class NotesPerson.
 */
public class NotesPerson extends Person {
	private static Database addressDB;

	public static void init(Session aSession) throws NotesException {
		openAddressDB(aSession);
	}

	private static void openAddressDB(Session aSession) throws NotesException {
		Vector<?> books = aSession.getAddressBooks();
		Enumeration<?> e = books.elements();
		Database db2;
		while (e.hasMoreElements()) {
			db2 = (Database) e.nextElement();
			if (db2.isPublicAddressBook()) {
				// String msg = " " + db2.getFilePath();
				addressDB = db2;
				addressDB.open();
				break;
			}
		}
	}

	public static List<Person> findPerson(String aName) throws ItemNotFoundException {
		List<Person> res = new ArrayList<Person>();
		try {
			View userView = addressDB.getView("($Users)");

			int indexOf = aName.indexOf("/");
			if (indexOf > 0) {
				aName = aName.substring(0, indexOf);
			}

			DocumentCollection allDocumentsByKey = userView.getAllDocumentsByKey(aName);
			Document firstDocument = allDocumentsByKey.getFirstDocument();
			while (firstDocument != null) {
				Vector<?> items = firstDocument.getItems();
				NotesPerson person = new NotesPerson();
				for (Object item : items) {
					if (item instanceof Item) {
						Item item2 = (Item) item;
						person.mapItem(item2);
					}
				}
				res.add(person);
				firstDocument = allDocumentsByKey.getNextDocument();
			}
		} catch (NotesException e) {
			throw new ItemNotFoundException("Person could not be found in Database.", e);
		}
		return res;
	}

	private void mapItem(Item aItem) throws NotesException {

		String itemName = aItem.getName();
		if (itemName.equals("FirstName")) {
			setFirstName(aItem.getValueString());
		} else if (itemName.equals("LastName")) {
			setLastName(aItem.getValueString());
		} else if (itemName.equals("InternetAddress")) {
			setINetAdress(aItem.getValueString());
		} else if (itemName.equals("OfficePhoneNumber")) {
			setPhoneJob(aItem.getValueString());
		} else if (itemName.equals("CellPhoneNumber")) {
			setPhoneMobile(aItem.getValueString());
//		} else if (itemName.equals("Chair")) {
//		} else if (itemName.equals("INetRequiredNames")) {
//			try {
//				if (aItem.getValues() != null) {
//					Vector<?> v = aItem.getValues();
//					for (Object object : v) {
//						String s = object.toString();
//						if (s.matches(".+@(.*\\.)+.+")) {
//						}
//					}
//				}
//			} catch (NotesException e) {
//				// TODO: ignore Exceptions ??
//			}
		}

	}

}
