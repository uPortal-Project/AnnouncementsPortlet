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

<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>

<rs:aggregatedResources path="skin-mobile.xml"/>

<div data-role="content" class="portlet-content">

    <ul data-role="listview" class="feed">
        <c:forEach items="${emergency}" var="announcement">
            <li class="emergency">
                <a href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>">
                    <h3>${ announcement.title }</h3>
                    <p>${ announcement.abstractText }</p>
                </a>
            </li>
        </c:forEach>
        <c:forEach items="${announcements}" var="announcement" varStatus="status">
            <li>
                <a href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>">
                    <h3>${ announcement.title }</h3>
                    <p>${ announcement.abstractText }</p>
                </a>
            </li>
        </c:forEach>
    </ul>
    
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

    <c:if test="${not isGuest && not disableEdit}">
        <div class="utilities">
            <a data-role="button" href="<portlet:renderURL portletMode='edit'/>"><spring:message code="display.link.edit"/></a>
        </div>
    </c:if>

</div>

