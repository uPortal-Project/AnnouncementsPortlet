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

<link type="text/css" rel="stylesheet" href="<c:url value="/css/mobile.css"/>"/>

<div class="content annplt-content">
    <div class="annplt-announcement annplt-view_announcement">
        <div class="annplt-announcement_inner">
        <div class="titlebar">
            <span class="category"><c:out value="${announcement.parent.title}"/></span>
            <c:if test="${displayPublishDate}"><span class="date"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span></c:if>
            <h2><c:out value="${announcement.title}"/></h2>
            <span class="expiration"><spring:message code="displayFull.displayEnd"/>
                <c:choose>
                    <c:when test="${announcement.endDisplay == null}">
                        <spring:message code="displayFull.displayEnd.unspecified"/>
                    </c:when>
                    <c:otherwise>
                        <fmt:formatDate value="${announcement.endDisplay}" dateStyle="long"/>
                    </c:otherwise>
                </c:choose>
            </span>
            <c:if test="${not empty announcement.link}">
                <div class="reference">
                    <spring:message code="display.link.prefix"/> <a href="${announcement.link}"><span><spring:message code="display.link.placeholder"/></span></a>
                </div>
            </c:if>
        </div>
        <div class="body"><c:out value="${announcement.message}" escapeXml="false"/></div>
        </div>
    </div>
</div>

<div class="annplt-toolbar">
    <ul>
        <li>
            <a href="<portlet:renderURL/>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <span><spring:message code="displayFull.back"/></span></a>
        </li>
    </ul>
</div>








