/**
 * Licensed to Apereo under one or more contributor license
 * agreements. See the NOTICE file distributed with this work
 * for additional information regarding copyright ownership.
 * Apereo licenses this file to you under the Apache License,
 * Version 2.0 (the "License"); you may not use this file
 * except in compliance with the License.  You may obtain a
 * copy of the License at the following location:
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.jasig.portlet.announcements.model.validators;

import org.jasig.portlet.announcements.model.Announcement;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.springframework.validation.Errors;
import org.springframework.validation.ValidationUtils;
import org.springframework.validation.Validator;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;

/**
 * <p>AnnouncementValidator class.</p>
 *
 * @author Erik A. Olsson (eolsson@uci.edu)
 *     <p>$LastChangedBy$ $LastChangedDate$
 * @version $Id: $Id
 */
public class AnnouncementValidator implements Validator {
  private final Log logger = LogFactory.getLog(getClass());

  private final boolean allowOpenEndDate;
  private final boolean allowEmptyMessage;

  /**
   * <p>Constructor for AnnouncementValidator.</p>
   */
  public AnnouncementValidator() {
    this(false, false);
  }

  /**
   * <p>Constructor for AnnouncementValidator.</p>
   *
   * @param allowOpenEndDate a boolean.
   * @param allowEmptyMessage a boolean.
   */
  public AnnouncementValidator(boolean allowOpenEndDate, boolean allowEmptyMessage) {
    this.allowOpenEndDate = allowOpenEndDate;
    this.allowEmptyMessage = allowEmptyMessage;
  }

  /* (non-Javadoc)
   * @see org.springframework.validation.Validator#supports(java.lang.Class)
   */
  /** {@inheritDoc} */
  public boolean supports(Class<?> clazz) {
    return Announcement.class.isAssignableFrom(clazz);
  }

  /* (non-Javadoc)
   * @see org.springframework.validation.Validator#validate(java.lang.Object, org.springframework.validation.Errors)
   */
  /** {@inheritDoc} */
  public void validate(Object obj, Errors errors) {
    ValidationUtils.rejectIfEmptyOrWhitespace(errors, "title", "addAnn.title.required.error");
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors, "abstractText", "addAnn.abstract.required.error");
    if (!allowEmptyMessage) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "message", "addAnn.message.required.error");
    }
    ValidationUtils.rejectIfEmptyOrWhitespace(
        errors, "startDisplay", "addAnn.start.required.error");
    if (!allowOpenEndDate) {
      ValidationUtils.rejectIfEmptyOrWhitespace(errors, "endDisplay", "addAnn.end.required.error");
    }

    Announcement test = (Announcement) obj;
    if (test.getLink() != null && !"".equals(test.getLink().trim())) {
      if (!validUrlFormat(test.getLink()))
        errors.rejectValue("link", "addAnn.link.malformed.error");
    }
    UnwantedCharacterScrubber vHelper = new UnwantedCharacterScrubber();
    logger.debug(String.format("Original announcement abstract: [%s]", test.getAbstractText()));
    test.setAbstractText(vHelper.scrubUnwantedCharacters(test.getAbstractText()));
    logger.debug(String.format("Converted announcement abstract: [%s]", test.getAbstractText()));
    logger.debug(String.format("Original announcement title: [%s]", test.getTitle()));
    test.setTitle(vHelper.scrubUnwantedCharacters(test.getTitle()));
    logger.debug(String.format("Converted announcement title: [%s]", test.getTitle()));

    Date startDisplay = test.getStartDisplay();
    Date endDisplay = test.getEndDisplay();
    Date now = new Date();

    if (startDisplay != null) {
      Calendar calStart = new GregorianCalendar();
      calStart.setTime(startDisplay);

      if (calStart.get(Calendar.YEAR) > 2050) {
        errors.rejectValue("startDisplay", "addAnn.toofaraway");
      }

      if (calStart.get(Calendar.YEAR) < 2008) {
        errors.rejectValue("startDisplay", "addAnn.tooold");
      }
    }
    if (endDisplay != null) {
      Calendar calEnd = new GregorianCalendar();
      calEnd.setTime(endDisplay);
      if (calEnd.get(Calendar.YEAR) > 2050) {
        errors.rejectValue("endDisplay", "addAnn.toofaraway");
      }
      if (calEnd.get(Calendar.YEAR) < 2008) {
        errors.rejectValue("endDisplay", "addAnn.tooold");
      }
    }
    if (endDisplay != null && startDisplay != null) {
      Calendar calStart = new GregorianCalendar();
      calStart.setTime(startDisplay);
      Calendar calEnd = new GregorianCalendar();
      calEnd.setTime(endDisplay);

      if (endDisplay.before(now) && !endDisplay.equals(startDisplay)) {
        errors.rejectValue("endDisplay", "addAnn.endDisplay.dateinpast");
      }
      if (startDisplay.after(endDisplay)) {
        errors.rejectValue("startDisplay", "addAnn.startDisplay.afterenddisplay");
      }
      if (startDisplay.equals(endDisplay)) {
        errors.rejectValue("endDisplay", "addAnn.endDisplay.sameAs.startDisplay");
      }
    }
  }

  private boolean validUrlFormat(String link) {
    URL test;
    try {
      test = new URL(link);
    } catch (MalformedURLException e) {
      return false;
    }
    link = test.toString();

    if (!link.startsWith("http://") && !link.startsWith("https://")) {
      return false;
    }
    return true;
  }
}
