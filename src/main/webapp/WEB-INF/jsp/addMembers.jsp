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
<portlet:actionURL var="actionUrl" escapeXml="false">
	<portlet:param name="action" value="addMembers"/>
	<portlet:param name="topicId" value="${topic.id}"/>
	<portlet:param name="groupKey" value="${groupKey}"/>
</portlet:actionURL>

<portlet:actionURL var="actionUrlUser" escapeXml="false">
	<portlet:param name="action" value="addUser"/>
	<portlet:param name="topicId" value="${topic.id}"/>
	<portlet:param name="groupKey" value="${groupKey}"/>
</portlet:actionURL>
<link href="<c:url value="/css/baseAdmin.css"/>" rel="stylesheet" type="text/css" />
<div class="announcements-portlet-toolbar">
	<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>">
	<img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.backtotopic"/></a>
	<div class="announcements-portlet-secondary">
		<a style="text-decoration:none;" href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>">
		<img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>
	</div>
</div>
<div class="portlet-section-header">
	<h2 class="title" role="heading"><spring:message code="addMembers.assigning"/></h2>
<c:choose>
	<c:when test="${groupKey eq 'admins'}"><spring:message code="general.admins"/></c:when>
	<c:when test="${groupKey eq 'moderators'}"><spring:message code="general.moderators"/></c:when>
	<c:when test="${groupKey eq 'authors'}"><spring:message code="general.authors"/></c:when>
	<c:when test="${groupKey eq 'audience'}"><spring:message code="general.audience"/></c:when>
</c:choose>
</div>
<div class="announcements-portlet-permissions">
	
	<div class="announcements-portlet-col">
		<form:form method="post" action="${actionUrl}" commandName="selection">
			<h3 class="title" role="heading"><spring:message code="addMembers.availableRoles"/></h3>
			<ul>
				<c:forEach items="${roles}" var="roleIter" varStatus="roleCounter">
					<c:if test="${not roleIter.person}">
						<li><form:checkbox path="selectedRoles" value="${roleIter.name}" /> <label for="selectedRoles${roleCounter.count}"><c:out value="${roleIter.name}"/></label></li>
					</c:if>
				</c:forEach>
			</ul>
			<button type="submit" class="portlet-form-button"><spring:message code="addMembers.update"/></button>
		</form:form>
	</div>
	<div class="announcements-portlet-col">
		<form method="post" action="${actionUrlUser}">
			<h3 class="title" role="heading"><spring:message code="addMembers.users"/></h3>
			<ul>
				<c:forEach items="${roles}" var="roleIter">
					<c:if test="${roleIter.person}">
					<li>
						<c:out value="${roleIter.personName}"/> <a href="<portlet:actionURL escapeXml="false"><portlet:param name="userKey" value="${roleIter.name}"/><portlet:param name="action" value="deleteUser"/><portlet:param name="groupKey" value="${groupKey}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>" title="<spring:message code="addMembers.deleteUser"/>"><img alt="<spring:message code="addMembers.deleteUser"/>" src="<c:url value="/icons/delete.png"/>" height="16" width="16"/></a>
					</li>
					</c:if>
				</c:forEach>
			</ul>
			<label for="add-username"><spring:message code="addMembers.addUser"/></label><br/>
			<input id="add-username" type="text" name="userAdd" class="portlet-form-input-field" size="30" maxlength="128" />
			<button type="submit" class="portlet-form-button"><spring:message code="addMembers.addUserButton"/></button>
		</form>
	</div>
</div>


