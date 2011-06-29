/**
 * Simple script that just replaces the subject-text of the meeting with "n/a".
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
	if (!calendarEntry.isConfidential()) {
		calendarEntry.setSubject("n/a");
	}
	return result
}
