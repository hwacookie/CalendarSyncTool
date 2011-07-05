/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */

package de.mbaaba.directions;

import java.util.List;

//CHECKSTYLE:OFF because this class was generated.
public class Legs {
	private Distance distance;

	private Duration duration;

	private String end_address;

	private End_location end_location;

	private String start_address;

	private Start_location start_location;

	private List<Steps> steps;

	private List<Via_waypoint> via_waypoint;

	public Distance getDistance() {
		return this.distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public Duration getDuration() {
		return this.duration;
	}

	public void setDuration(Duration duration) {
		this.duration = duration;
	}

	public String getEnd_address() {
		return this.end_address;
	}

	public void setEnd_address(String end_address) {
		this.end_address = end_address;
	}

	public End_location getEnd_location() {
		return this.end_location;
	}

	public void setEnd_location(End_location end_location) {
		this.end_location = end_location;
	}

	public String getStart_address() {
		return this.start_address;
	}

	public void setStart_address(String start_address) {
		this.start_address = start_address;
	}

	public Start_location getStart_location() {
		return this.start_location;
	}

	public void setStart_location(Start_location start_location) {
		this.start_location = start_location;
	}

	public List<Steps> getSteps() {
		return this.steps;
	}

	public void setSteps(List<Steps> steps) {
		this.steps = steps;
	}

	public List<Via_waypoint> getVia_waypoint() {
		return this.via_waypoint;
	}

	public void setVia_waypoint(List<Via_waypoint> via_waypoint) {
		this.via_waypoint = via_waypoint;
	}
}
