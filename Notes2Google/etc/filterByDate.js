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
	
	//remove the AllDayEvents
	var begin = java.util.Calendar.getInstance();
	var end = java.util.Calendar.getInstance();
	begin.setTime(calendarEntry.getStartDates().get(0));
	end.setTime(calendarEntry.getEndDates().get(0));
	var beginHour = begin.get(java.util.Calendar.HOUR_OF_DAY);
	var endHour = end.get(java.util.Calendar.HOUR_OF_DAY);
	
	if (endHour - beginHour > 10) {
		result = false;
	}
	
	return result
}