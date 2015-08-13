package com.losandes.utils;

import java.io.Serializable;
import java.util.concurrent.TimeUnit;

public class Time implements Serializable{
	private static final long serialVersionUID = 4915716311965511894L;
	private long amount;
	private TimeUnit unit;
	public Time(long amount, TimeUnit unit) {
		this.amount = amount;
		this.unit = unit;
	}
	public long getAmount() {
		return amount;
	}
	public TimeUnit getUnit() {
		return unit;
	}
	public long toMillis(){
		switch (unit) {
		case HOURS:
			return amount*60*60*1000;
		case SECONDS:
			return amount*1000;
		case MINUTES:
			return amount*60000;
		case DAYS:
			return amount*24*60*60*1000;
		case MICROSECONDS:
			return amount/1000;
		case NANOSECONDS:
			return amount/1000000;
		default:
			return amount;
		}
	}
}
