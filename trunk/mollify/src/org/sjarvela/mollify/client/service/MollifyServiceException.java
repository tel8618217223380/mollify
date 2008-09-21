package org.sjarvela.mollify.client.service;


public class MollifyServiceException extends Exception {
	private static final long serialVersionUID = 1L;
	
	ServiceError error;

	public MollifyServiceException(ServiceError error) {
		super();
		this.error = error;
	}

	public ServiceError getError() {
		return error;
	}
}
