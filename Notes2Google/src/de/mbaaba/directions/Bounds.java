/* --------------------------------------------------------------------------
 * @author Hauke Walden
 * @created 28.06.2011 
 * Copyright 2011 by Hauke Walden 
 * All rights reserved.
 * --------------------------------------------------------------------------
 */


package de.mbaaba.directions;


public class Bounds{
   	private Northeast northeast;
   	private Southwest southwest;

 	public Northeast getNortheast(){
		return this.northeast;
	}
	public void setNortheast(Northeast northeast){
		this.northeast = northeast;
	}
 	public Southwest getSouthwest(){
		return this.southwest;
	}
	public void setSouthwest(Southwest southwest){
		this.southwest = southwest;
	}
}
