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
<div class="portlet-section-header"><c:out value="${announcement.title}"/></div>
<p>
<span class="portlet-section-text" style="font-size:0.8em;"><spring:message code="displayFull.displayEnd"/> <fmt:formatDate value="${announcement.endDisplay}" dateStyle="long"/></span>
<c:if test="${not empty announcement.link}">
	<br/>
	<span class="portlet-section-text" style="font-size:0.8em;"><spring:message code="display.link.prefix"/> <a href="${announcement.link}"><c:out value="${announcement.link}"/></a></span>
</c:if>
</p>

<c:out value="${announcement.message}" escapeXml="false"/>

<a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL><portlet:param name="action" value="displayAnnouncements"/></portlet:renderURL>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="displayFull.back"/></a>

