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

<rs:aggregatedResources path="skin.xml"/>
<link href="<c:url value="/css/baseAdmin.css"/>" rel="stylesheet" type="text/css" />

<div id="<portlet:namespace/>">
<div class="announcements-portlet-toolbar">
    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view"><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ topic.id }"/></portlet:renderURL>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.backtotopic"/></a>
    <div class="announcements-portlet-secondary">
	    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>"><img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>
	</div>
</div>
<div class="portlet-section-header">
	<h2 class="title" role="heading"><spring:message code="showHistory.history"/></h2>
</div>
<table width="100%" cellspacing="0" cellpadding="0" id="historyTable" class="tablesorter">
<thead>
	<tr>
		<th width="15%"><spring:message code="showHistory.header.topic"/></th>
		<th><spring:message code="showHistory.header.ann"/></th>
		<th><spring:message code="showHistory.header.start"/></th>
		<th><spring:message code="showHistory.header.end"/></th>
		<th><spring:message code="showHistory.header.repost"/></th>
		<th><spring:message code="showHistory.header.delete"/></th>
	</tr>
</thead>
<tbody>
<c:forEach items="${announcements}" var="ann">
	<tr>
		<td><c:out value="${ann.parent.title}"/></td>
		<td><a title="<spring:message code="showHistory.preview"/>"  href="<portlet:renderURL><portlet:param name="action" value="previewAnnouncement"/><portlet:param name="annId" value="${ ann.id }"/></portlet:renderURL>"><c:out value="${ann.title}"/></a><br /><c:out value="${ann.abstractText}"/></td>
		<td><fmt:formatDate value="${ann.startDisplay}" dateStyle="short"/></td>
		<td><fmt:formatDate value="${ann.endDisplay}" dateStyle="short"/></td>
		<td><a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${ann.id}"/></portlet:renderURL>" title="<spring:message code="showHistory.viewedit"/>"><img alt="<spring:message code="showHistory.viewedit"/>" src="<c:url value="/icons/pencil.png"/>" border="0" height="16" width="16"/></a></td>
		<td><a href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL escapeXml="false"><portlet:param name="action" value="deleteAnnouncement"/><portlet:param name="annId" value="${ann.id}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="showHistory.delete"/>"><img border="0" alt="<spring:message code="showHistory.delete"/>" src="<c:url value="/icons/bin_empty.png"/>" height="16" width="16"/></a></td>
	</tr>
</c:forEach>
</tbody>
</table>



<script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.tablesorter.js"/>"></script>
<script type="text/javascript">
var <portlet:namespace/>  = jQuery.noConflict(true);
<portlet:namespace/>(document).ready(function(){
		<portlet:namespace/>("#historyTable").tablesorter( {sortList: [[1,0], [2,0]]} );
	}
);
</script>

</div>