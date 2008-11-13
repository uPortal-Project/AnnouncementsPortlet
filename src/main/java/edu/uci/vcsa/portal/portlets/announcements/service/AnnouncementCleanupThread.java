/**
 *  Copyright 2008. The Regents of the University of California. All Rights
 *  Reserved. Permission to use, copy, modify, and distribute any part of this
 *  software including any source code and documentation for educational,
 *  research, and non-profit purposes, without fee, and without a written
 *  agreement is hereby granted, provided that the above copyright notice, this
 *  paragraph and the following three paragraphs appear in all copies of the
 *  software and documentation. Those desiring to incorporate this software into
 *  commercial products or use for commercial purposes should contact Office of
 *  Technology Alliances, University of California, Irvine, 380 University
 *  Tower, Irvine, CA 92607-7700, Phone: (949) 824-7295, FAX: (949) 824-2899. IN
 *  NO EVENT SHALL THE UNIVERSITY OF CALIFORNIA BE LIABLE TO ANY PARTY FOR
 *  DIRECT, INDIRECT, SPECIAL, INCIDENTAL, OR CONSEQUENTIAL DAMAGES, INCLUDING,
 *  WITHOUT LIMITATION, LOST PROFITS, CLAIMS OR DEMANDS, OR BUSINESS
 *  INTERRUPTION, ARISING OUT OF THE USE OF THIS SOFTWARE, EVEN IF THE
 *  UNIVERSITY OF CALIFORNIA HAS BEEN ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 *  THE SOFTWARE PROVIDED HEREIN IS ON AN "AS IS" BASIS, AND THE UNIVERSITY OF
 *  CALIFORNIA HAS NO OBLIGATION TO PROVIDE MAINTENANCE, SUPPORT, UPDATES,
 *  ENHANCEMENTS, OR MODIFICATIONS. THE UNIVERSITY OF CALIFORNIA MAKES NO
 *  REPRESENTATIONS AND EXTENDS NO WARRANTIES OF ANY KIND, EITHER IMPLIED OR
 *  EXPRESS, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 *  MERCHANTABILITY OR FITNESS FOR A PARTICULAR PURPOSE, OR THAT THE USE OF THE
 *  SOFTWARE WILL NOT INFRINGE ANY PATENT, TRADEMARK OR OTHER RIGHTS.
 */
/**
 * 
 */
package edu.uci.vcsa.portal.portlets.announcements.service;

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
				announcementService.deleteAnnouncementsPastCurrentTime();
				lastCheckTime = System.currentTimeMillis();
				firstCheck = false;
			}
			try {
				log.debug("Waiting to see if we should check the time...");
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
