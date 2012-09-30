package com.pifive.makemyrun;

@SuppressWarnings("serial")
public class NoLocationException extends Exception {

	public NoLocationException() {
	}

	public NoLocationException(String detailMessage) {
		super(detailMessage);
	}

	public NoLocationException(Throwable throwable) {
		super(throwable);
	}

	public NoLocationException(String detailMessage, Throwable throwable) {
		super(detailMessage, throwable);
	}

}
