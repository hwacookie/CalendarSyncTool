/**
 *	Simple script that just replaces the body-text of the meeting with "n/a". 
 */


function filter() {
	var result = true;
	if (!calendarEntry.isConfidential()) {
		calendarEntry.setBody("n/a");
	}
	return result
}
