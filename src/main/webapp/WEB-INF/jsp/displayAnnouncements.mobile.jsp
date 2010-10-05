<%@ include file="/WEB-INF/jsp/include.jsp" %>
<%-- 
    Licensed to Jasig under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Jasig licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License. You may obtain a
    copy of the License at:
    
    http://www.apache.org/licenses/LICENSE-2.0
    
    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on
    an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied. See the License for the
    specific language governing permissions and limitations
    under the License.    
--%>

<link type="text/css" rel="stylesheet" href="<c:url value="/css/mobile.css"/>"/>

<div class="content annplt-content">

    <c:if test="${not empty emergency}">
        <div class="annplt-emergency">
            <c:forEach items="${emergency}" var="announcement">
                <div class="annplt-announcement">
                    <div class="announcement_inner">
                        <div class="titlebar">
                            <span class="category"><c:out value="${announcement.parent.title}"/></span>
                            <span class="date"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span>
                            <h2><a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><img src="<c:url value="/icons/exclamation.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <c:out value="${announcement.title}"/></a></h2>
                        </div>
                        <p class="article"><c:out value="${announcement.abstractText}"/></p>
                    </div>
                </div>
            </c:forEach>
        </div>
    </c:if>
    
    <div class="annplt-announcements">
        <span class="title">Announcements</span>
        <c:forEach items="${announcements}" var="announcement" varStatus="status">
            <div class="annplt-announcement">
                <div class="announcement_inner">
                    <div class="titlebar">
                        <span class="category"><c:out value="${announcement.parent.title}"/></span>
                        <c:if test="${showDate}">
                            <span class="date"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span>
                        </c:if>
                        <h2><a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a></h2>
                        <c:if test="${not empty announcement.link}">
                            <div class="reference"><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><span><spring:message code="display.link.placeholder"/></span></a></div>
                        </c:if>
                    </div>
                    <p class="article"><c:out value="${announcement.abstractText}"/></p>
                </div>
            </div>
        </c:forEach>
    </div>
    
    <div class="annplt-pagination">
        <ul>
            <c:if test="${not (from == 0)}">
                <li><a href="<portlet:renderURL><portlet:param name="from" value="${from - increment}"/><portlet:param name="to" value="${to - increment}"/></portlet:renderURL>"><spring:message code="display.link.prev"/> <c:out value="${increment}"/></a></li>
            </c:if>
            <c:if test="${hasMore}">
                <li><a href="<portlet:renderURL><portlet:param name="from" value="${from + increment}"/><portlet:param name="to" value="${to + increment}"/></portlet:renderURL>"><spring:message code="display.link.next"/> <c:out value="${increment}"/></a></li>
            </c:if>
        </ul>
    </div>

</div>

<c:if test="${not isGuest && not disableEdit}">
    <div class="annplt-toolbar">
        <ul>
            <li class="settings">
                <a style="text-decoration:none;" href="<portlet:renderURL portletMode="edit"/>"><img src="<c:url value="/icons/pencil.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <span><spring:message code="display.link.edit"/></span></a>
            </li>
        </ul>
    </div>
</c:if>
