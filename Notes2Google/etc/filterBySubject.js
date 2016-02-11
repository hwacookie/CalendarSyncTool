/**
 * A script that inhibits sync of the given entry if the subject contains "Kinder abholen"
 * 
 * The following fields may be read or written with the appropriate set/get/add methods: 
 * 
 * private String subject;
 * 
 * private String body;
 * 
 * private Person chair;
 * 
 * private String location;
 * 
 * private String room;
 * 
 * private List<Person> attendees;
 * 
 * private ArrayList<Date> startDates;
 * 
 * private ArrayList<Date> endDates;
 * 
 * private Date lastModified;
 * 
 * private String uniqueID;
 * 
 * private Date alarmTime;
 */

function filter() {
	var result = true;
	if ((calendarEntry.getSubject().contains("Kinder abholen"))
	|| (calendarEntry.getSubject().contains("#Holiday"))) {
		result = false;
	}
	return result
}
