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
<style type="text/css">
.<portlet:namespace/>-row1color { padding: 5px; background-color: #eee; }
.<portlet:namespace/>-row2color { padding: 5px; background-color: #fff; }
.<portlet:namespace/>-emerg { padding: 5px; margin-bottom:5px; color:#c00; background-color: #fff; border: 3px solid #cc3300; }
.<portlet:namespace/>-emerg a, .<portlet:namespace/>-emerg a:visited { text-decoration: none; color: #c00; }
</style>

<c:if test="${not empty emergency}">
	<c:forEach items="${emergency}" var="announcement">
		<div class="<portlet:namespace/>-emerg">
			<strong><a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><img src="<c:url value="/icons/exclamation.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <c:out value="${announcement.title}"/></a></strong> <span class="portlet-section-text" style="font-size:0.9em;">(<fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/>)</span>
			<br/>
			<c:out value="${announcement.abstractText}"/>
		</div>
	</c:forEach>
</c:if>

<c:choose>
    <c:when test="${empty announcements}">
        <p><span class=""><spring:message code="display.no.announcements"/></span></p>
    </c:when>
    <c:otherwise>
        <table width="100%" cellspacing="0" cellpadding="0" class="data">
            <tr>
                <th width="15%"><spring:message code="display.header.topic"/></th>
                <th><spring:message code="display.header.ann"/></th>
            </tr>

        <c:forEach items="${announcements}" var="announcement" varStatus="status">
            <tr>
                <c:choose>
                    <c:when test="${status.index mod 2 == 0}">
                        <td align="center" width="15%" class="<portlet:namespace/>-row1color">
                    </c:when>
                    <c:otherwise>
                        <td align="center" width="15%" class="<portlet:namespace/>-row2color">
                    </c:otherwise>
                </c:choose>
                    <c:out value="${announcement.parent.title}"/>
                    <c:if test="${showDate}">
                        <br/>
                        <span class="portlet-section-text" style="font-size:0.9em;"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span>
                    </c:if>			
                </td>
                <c:choose>
                    <c:when test="${status.index mod 2 == 0}">
                        <td class="<portlet:namespace/>-row1color">
                    </c:when>
                    <c:otherwise>
                        <td class="<portlet:namespace/>-row2color">
                    </c:otherwise>
                </c:choose>
                    <a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a>
                    <br/><c:out value="${announcement.abstractText}"/>
                    <br/>
                    <c:if test="${not empty announcement.link}">
                        <span class="portlet-section-text" style="font-size:0.9em; padding-top:0.2em;"><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></span> 
                    </c:if>			
                </td>
            </tr>
        </c:forEach>
        </table>
    </c:otherwise>
</c:choose>

<table border="0" width="100%">
  <tr>
	<td align="left" style="font-size:0.9em; padding-top:0.5em;">
		<c:if test="${not (from == 0)}">
			<a href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="from" value="${from - increment}"/><portlet:param name="to" value="${to - increment}"/></portlet:renderURL>"><spring:message code="display.link.prev"/> <c:out value="${increment}"/></a>
		</c:if>
		<c:if test="${(not (from == 0)) and hasMore}">&nbsp;&mdash;&nbsp;</c:if>
		<c:if test="${hasMore}">
			<a href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="from" value="${from + increment}"/><portlet:param name="to" value="${to + increment}"/></portlet:renderURL>"><spring:message code="display.link.next"/> <c:out value="${increment}"/></a>
		</c:if>
	</td>
	<td align="right" style="font-size:0.9em; padding-top:0.5em;">
		<c:if test="${not isGuest && not disableEdit}">
			<a style="text-decoration:none;" href="<portlet:renderURL portletMode="edit" windowState="normal"/>"><img src="<c:url value="/icons/pencil.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="display.link.edit"/></a>
		</c:if>
	</td>
  </tr>
</table>
