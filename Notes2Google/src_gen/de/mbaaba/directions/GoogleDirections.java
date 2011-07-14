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
public class GoogleDirections {
	private List<Routes> routes;

	private String status;

	public List<Routes> getRoutes() {
		return this.routes;
	}

	public void setRoutes(List<Routes> routes) {
		this.routes = routes;
	}

	public String getStatus() {
		return this.status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
}
