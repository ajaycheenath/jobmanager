package com.af.job.config;

/**
 * Custom exception throw when there is job configuration validation failure
 * @author ajay_francis
 *
 */
public class JobConfigValidationException extends Exception {
	
	private static final long serialVersionUID = 1L;
	private static final String MSG_CONFIG_VALIDATION_FAILED = "CONFIG_VALIDATION_FAILED : ";
	
	public JobConfigValidationException(final String error) {
		super(MSG_CONFIG_VALIDATION_FAILED + error);
	}

	

}
