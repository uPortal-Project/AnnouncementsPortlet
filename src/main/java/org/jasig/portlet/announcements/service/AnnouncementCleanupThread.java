/**
 * Licensed to Jasig under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Jasig licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License. You may obtain a
 * copy of the License at:
 * 
 * http://www.apache.org/licenses/LICENSE-2.0
 * 
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on
 * an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied. See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.service;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

import org.apache.log4j.Logger;

/**
 * @author Erik A. Olsson (eolsson@uci.edu)
 *
 * $LastChangedBy$
 * $LastChangedDate$
 *
 */
public class AnnouncementCleanupThread extends Thread {

	private IAnnouncementService announcementService;
	private int hourToCheck = 3;  // military time
	private int minuteToCheck = 0;
	private int checkInterval = 60; 	// seconds
	private long maxCheckIntervalMillis = 43200000L;	// 12 hours
	private boolean keepRunning;
	
	private static Logger log = Logger.getLogger(AnnouncementCleanupThread.class);
	
	public AnnouncementCleanupThread() {
		setDaemon(true);
		keepRunning = true;
	}
	
	public void stopThread() {
		keepRunning = false;
		log.info("Stopping cleanup thread...");
		this.interrupt();
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Thread#run()
	 */
	@Override
	public void run() {
		Date now;
		Calendar nowCal = new GregorianCalendar();
		long lastCheckTime = System.currentTimeMillis();
		boolean firstCheck = true;
		
		while (true && keepRunning) {
			now = new Date();
			nowCal.setTime(now);
			
			/**
			 * If the current hour of the day = the hour to check AND
			 * the current minute of the hour = the minute to check (plus a range of 2 minutes) AND
			 * the current time is later than the last time we checked + the required interval
			 */
			if (nowCal.get(Calendar.HOUR_OF_DAY) == hourToCheck && 
				nowCal.get(Calendar.MINUTE) <= (minuteToCheck + 1) &&
				(firstCheck || System.currentTimeMillis() > (lastCheckTime + maxCheckIntervalMillis))) {
				
				log.info("Going to delete old announcements at "+now.toString());
				//UOC Customization.
				//we no longer want to auto delete events. 
				//announcementService.deleteAnnouncementsPastCurrentTime();
				lastCheckTime = System.currentTimeMillis();
				firstCheck = false;
			}
			try {
				log.trace("Waiting to see if we should check the time...");
				sleep(checkInterval * 1000);
			} catch (InterruptedException e) {
				break;
			}
		}
	}

	/**
	 * @param announcementService the announcementService to set
	 */
	public void setAnnouncementService(IAnnouncementService announcementService) {
		this.announcementService = announcementService;
	}

	/**
	 * @param checkInterval the checkInterval to set
	 */
	public void setCheckInterval(int checkInterval) {
		this.checkInterval = checkInterval;
	}

	/**
	 * @param hourToCheck the hourToCheck to set
	 */
	public void setHourToCheck(int hourToCheck) {
		this.hourToCheck = hourToCheck;
	}

	/**
	 * @param minuteToCheck the minuteToCheck to set
	 */
	public void setMinuteToCheck(int minuteToCheck) {
		this.minuteToCheck = minuteToCheck;
	}

	/**
	 * @param maxCheckIntervalMillis the maxCheckIntervalMillis to set
	 */
	public void setMaxCheckIntervalMillis(long maxCheckIntervalMillis) {
		this.maxCheckIntervalMillis = maxCheckIntervalMillis;
	}
	
}
