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
package org.jasig.portlet.announcements.model;

import java.util.Comparator;

/** @author Chris Waymire (cwaymire@unicon.net) */
public enum AnnouncementSortStrategy {
  CREATE_DATE_ASCENDING() {
    public Comparator<Announcement> getComparator() {
      return new Comparator<Announcement>() {
        public int compare(Announcement ann1, Announcement ann2) {
          return ann1.getCreated().compareTo(ann2.getCreated());
        }
      };
    }
  },
  CREATE_DATE_DESCENDING() {
    public Comparator<Announcement> getComparator() {
      return new Comparator<Announcement>() {
        public int compare(Announcement ann1, Announcement ann2) {
          return CREATE_DATE_ASCENDING.getComparator().compare(ann1, ann2) * -1;
        }
      };
    }
  },
  START_DISPLAY_DATE_ASCENDING() {
    public Comparator<Announcement> getComparator() {
      return new Comparator<Announcement>() {
        public int compare(Announcement ann1, Announcement ann2) {
          return ann1.getStartDisplay().compareTo(ann2.getStartDisplay());
        }
      };
    }
  },
  START_DISPLAY_DATE_DESCENDING() {
    public Comparator<Announcement> getComparator() {
      return new Comparator<Announcement>() {
        public int compare(Announcement ann1, Announcement ann2) {
          return START_DISPLAY_DATE_ASCENDING.getComparator().compare(ann1, ann2) * -1;
        }
      };
    }
  },
  END_DISPLAY_DATE_ASCENDING() {
    public Comparator<Announcement> getComparator() {
      return new Comparator<Announcement>() {
        public int compare(Announcement ann1, Announcement ann2) {
          if ((ann1.getEndDisplay() == null) || (ann2.getEndDisplay() == null)) {
            if ((ann1.getEndDisplay() == null) && (ann2.getEndDisplay() == null)) {
              if (ann1.getTitle().equalsIgnoreCase(ann2.getTitle())) {
                return ann1.getId().compareTo(ann2.getId());
              } else {
                return ann1.getTitle().compareTo(ann2.getTitle());
              }
            } else {
              return ann1.getEndDisplay() == null ? 1 : -1;
            }
          } else {
            return ann1.getEndDisplay().compareTo(ann2.getEndDisplay());
          }
        }
      };
    }
  },
  END_DISPLAY_DATE_DESCENDING() {
    public Comparator<Announcement> getComparator() {
      return new Comparator<Announcement>() {
        public int compare(Announcement ann1, Announcement ann2) {
          return END_DISPLAY_DATE_ASCENDING.getComparator().compare(ann1, ann2) * -1;
        }
      };
    }
  },
  DEFAULT() {
    public Comparator<Announcement> getComparator() {
      return START_DISPLAY_DATE_DESCENDING.getComparator();
    }
  };

  private AnnouncementSortStrategy() {}

  public abstract Comparator<Announcement> getComparator();

  public static Comparator<Announcement> getStrategy(String strategyName) {
    AnnouncementSortStrategy strategy = AnnouncementSortStrategy.valueOf(strategyName);
    return strategy != null ? strategy.getComparator() : DEFAULT.getComparator();
  }
}
