/**
 *	Simple script that adds room to subject. 
 */

// SVU - R 4.21/Berlin

function filter() {
	// LOG.debug("hallo");
	var result = true;
	var room = calendarEntry.getRoom();
	if (room != null && !room.equals("null")) {
		var idx = room.indexOf("/");
		if (idx>0) {
			var newRoom = room.substring(0, idx).replaceAll(" ", "").replaceAll("R", "");
			calendarEntry.setSubject(calendarEntry.getSubject() + " (Raum " + newRoom+")");
		}
	}
	return result
}
