package com.pifive.makemyrun;

public class DirectionsException extends Exception{

	public DirectionsException() {
		super("Unable to fetch valid JSON Google Directions");
	}
	
	public DirectionsException(String msg) {
		super(msg);
	}
	
}
