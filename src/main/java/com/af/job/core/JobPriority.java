package com.af.job.core;

/**
 * Enum to store Job Prority
 * @author ajay_francis
 *
 */
public enum JobPriority {
	HIGH(1),
	MEDIUM(2),
	LOW(3);
	
	private int value;
	private JobPriority(int value){
		this.value = value;
	}
	
	public int getValue()
	{
	  return this.value;
	}
}
