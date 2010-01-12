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
<c:if test="${portalAdmin}">
<script type="text/javascript">
function <portlet:namespace/>_delete(url) {
	var response = window.confirm('<spring:message code="baseAdmin.confirmDeleteTopic"/>');
	if (response) {
		window.location = url;
	}
}
</script>
</c:if>

<div class="portlet-section-header"><spring:message code="baseAdmin.topics"/></div>

<table cellpadding="0" cellspacing="0" class="data" width="100%">
<c:choose>
	<c:when test="${portalAdmin}">
		<c:forEach items="${allTopics}" var="topic">
		<tr>
			<td>
				<c:out value="${topic.title}"/>
			</td> 
            <td>
				( <c:out value="${topic.displayingAnnouncementCount}"/>, <c:out value="${topic.scheduledAnnouncementCount}"/>, <c:out value="${topic.pendingAnnouncementCount}"/> )
			</td>
			<td>
				<a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>"><img src="<c:url value="/icons/cog.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.manage"/>"/></a>&nbsp;&nbsp;
				<c:if test="${topic.subscriptionMethod != 4}">
					<a href="<portlet:renderURL><portlet:param name="action" value="addTopic"/><portlet:param name="edit" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.edit"/>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.edit"/>"/></a>&nbsp;&nbsp;
					<a href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL><portlet:param name="action" value="deleteTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="baseAdmin.delete"/>"><img src="<c:url value="/icons/bin_empty.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.delete"/>"/></a>
				</c:if>
			</td>
		</tr>
		</c:forEach>
	</c:when>
	<c:otherwise>
		<c:forEach items="${adminTopics}" var="topic">
		<tr>
			<td>
				<c:out value="${topic.title}"/>
			</td> 
            <td>
				( <c:out value="${topic.displayingAnnouncementCount}"/>, <c:out value="${topic.scheduledAnnouncementCount}"/>, <c:out value="${topic.pendingAnnouncementCount}"/> )
			</td>
			<td>
				<a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>"><img src="<c:url value="/icons/cog.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.manage"/>"/></a>&nbsp;&nbsp;
				<c:if test="${topic.subscriptionMethod != 4}">
					<a href="<portlet:renderURL><portlet:param name="action" value="addTopic"/><portlet:param name="edit" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.edit"/>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.edit"/>"/></a>&nbsp;&nbsp;
				</c:if>
			</td>
		</tr>
		</c:forEach>
		<c:forEach items="${otherTopics}" var="topic">
		<tr>
			<td>
				<c:out value="${topic.title}"/>
			</td> 
			<td>
				<a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>"><img src="<c:url value="/icons/cog.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.manage"/>"/></a>&nbsp;&nbsp;
			</td>
		</tr>
		</c:forEach>
	</c:otherwise>
</c:choose>

</table>

<c:if test="${portalAdmin}">
	<br/>
	<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addTopic"/></portlet:renderURL>">
	<img src="<c:url value="/icons/add.png"/>" border="0" height="16" width="16" style="vertical-align:middle;"/> <spring:message code="baseAdmin.addnew"/>
	</a>
</c:if>