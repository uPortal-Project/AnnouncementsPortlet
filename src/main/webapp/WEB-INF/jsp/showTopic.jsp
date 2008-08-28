<%@ include file="/WEB-INF/jsp/include.jsp" %>

<c:if test="${includeJQuery}">
<script type="text/javascript" src="<c:url value="/js/jquery-1.2.3.min.js"/>"></script>
</c:if>

<script type="text/javascript">
function <portlet:namespace/>_delete(url) {
	var response = window.confirm('<spring:message code="show.deleteAnn"/>');
	if (response) {
		window.location = url;
	}
}
function <portlet:namespace/>approval(id, newValue) {
	var messages = new Array("<spring:message code="show.scheduled"/>",
			"<spring:message code="show.expired"/>",
			"<spring:message code="show.showing"/>",
			"<spring:message code="show.pending"/>",
			"<spring:message code="show.unpublish"/>",
			"<spring:message code="show.publish"/>",
			"<c:url value="/icons/stop.png"/>",
			"<c:url value="/icons/accept.png"/>");
	var colors = new Array("#070", "#c00", "#070", "#c00");

	$.post("<c:url value="/ajaxApprove"/>",
			{
				annId: id,
				approval: newValue
			},
			function(data) {
				if (newValue == 'true') {
					$("#<portlet:namespace/>annSwitch-"+id+" > img").attr("src", messages[6]);
					$("#<portlet:namespace/>annSwitch-"+id+" > img").attr("alt", messages[4]);
					$("#<portlet:namespace/>annSwitch-"+id).attr("title", messages[4]);
					$("#<portlet:namespace/>annSwitch-"+id).attr("href", "javascript:<portlet:namespace/>approval("+id+",'false');");
				} else {
					$("#<portlet:namespace/>annSwitch-"+id+" > img").attr("src", messages[7]);
					$("#<portlet:namespace/>annSwitch-"+id+" > img").attr("alt", messages[5]);
					$("#<portlet:namespace/>annSwitch-"+id).attr("title", messages[5]);	
					$("#<portlet:namespace/>annSwitch-"+id).attr("href", "javascript:<portlet:namespace/>approval("+id+",'true');");			
				}
				$("#<portlet:namespace/>annStatus-"+id).css("background-color", colors[data.status]);
				$("#<portlet:namespace/>annStatus-"+id).empty().append(messages[data.status]);				
			},
			"json"
	);
}
</script>

<div class="portlet-section-header"><spring:message code="show.annfor"/> <c:out value="${topic.title}"/></div>

<table width="100%" class="data">
	<c:choose>
		<c:when test="${empty announcements}">
			<tr>
				<td>
				<spring:message code="show.none"/>
				</td>
			</tr>
		</c:when>
		<c:otherwise>
			<tr>
				<th><spring:message code="show.head.status"/></th>
				<th><spring:message code="show.head.title"/></th>
				<th><spring:message code="show.head.displaying"/></th>
				<th>&nbsp;</th>
			</tr>
			<c:forEach items="${announcements}" var="ann">
				<tr>
					<c:choose>
						<c:when test="${ann.published}">
							<c:choose>
								<c:when test="${ann.startDisplay > now}">
									<td id="<portlet:namespace/>annStatus-${ann.id}" style="background-color:#070;color:#fff;font-weight:bold;vertical-align:middle;" align="center"><spring:message code="show.scheduled"/></td>
								</c:when>
								<c:when test="${ann.endDisplay < now}">
									<td id="<portlet:namespace/>annStatus-${ann.id}" style="background-color:#c00;color:#fff;font-weight:bold;vertical-align:middle;" align="center"><spring:message code="show.expired"/></td>
								</c:when>
								<c:otherwise>
									<td id="<portlet:namespace/>annStatus-${ann.id}" style="background-color:#070;color:#fff;font-weight:bold;vertical-align:middle;" align="center"><spring:message code="show.showing"/></td>
								</c:otherwise>
							</c:choose>
						</c:when>
						<c:otherwise>
							<td id="<portlet:namespace/>annStatus-${ann.id}" style="background-color:#c00;color:#fff;font-weight:bold;vertical-align:middle;" align="center"><spring:message code="show.pending"/></td>
						</c:otherwise>
					</c:choose>

					<td><c:out value="${ann.title}"/></td>
					<td><fmt:formatDate value="${ann.startDisplay}" dateStyle="short"/> - <fmt:formatDate value="${ann.endDisplay}" dateStyle="short"/></td>

					<td>
						
						<c:if test="${((not user.moderator) and user.userName eq ann.author) or user.moderator}">
							<a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${ann.id}"/></portlet:renderURL>" title="<spring:message code="show.viewedit"/>"><img alt="<spring:message code="show.viewedit"/>" src="<c:url value="/icons/pencil.png"/>" height="16" width="16"/></a>
						</c:if>
						<c:if test="${user.moderator}">
							<a href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL><portlet:param name="action" value="deleteAnnouncement"/><portlet:param name="annId" value="${ann.id}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="show.delete"/>"><img alt="<spring:message code="show.delete"/>" src="<c:url value="/icons/bin_empty.png"/>" height="16" width="16"/></a>
							<c:choose>
								<c:when test="${ann.published}">
									<a id="<portlet:namespace/>annSwitch-${ann.id}" href="javascript:<portlet:namespace/>approval(${ann.id},'false');" title="<spring:message code="show.unpublish"/>"><img alt="<spring:message code="show.unpublish"/>" src="<c:url value="/icons/stop.png"/>" height="16" width="16"/></a>
								</c:when>
								<c:otherwise>
									<a id="<portlet:namespace/>annSwitch-${ann.id}" href="javascript:<portlet:namespace/>approval(${ann.id},'true');" title="<spring:message code="show.publish"/>"><img alt="<spring:message code="show.publish"/>" src="<c:url value="/icons/accept.png"/>" height="16" width="16"/></a>
								</c:otherwise>
							</c:choose>
						</c:if>
					</td>
				</tr>
			</c:forEach>
		</c:otherwise>
	</c:choose>
</table>

<br/>
<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>">
<img src="<c:url value="/icons/add.png"/>" height="16" width="16" style="vertical-align:middle" border="0"/> <spring:message code="show.addAnn"/></a>
<br/>
<br/>

<c:if test="${user.admin}">

	<div class="portlet-section-header"><spring:message code="show.header.permissions"/> <c:out value="${topic.title}"/></div>
	
	<table width="100%" class="data">
		<tr>
			<th><spring:message code="general.admins"/></th>
			<th><spring:message code="general.moderators"/></th>
			<th><spring:message code="general.authors"/></th>
			<th><spring:message code="general.audience"/></th>
		</tr>
		<tr>
			<td valign="top">
				<c:forEach items="${topic.admins}" var="member">
					<c:out value="${member}"/><br/>
				</c:forEach>
				<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="admins"/></portlet:renderURL>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" style="vertical-align:middle"/> <spring:message code="show.permissions.edit"/></a>
			</td>
			<td valign="top">
				<c:forEach items="${topic.moderators}" var="member">
					<c:out value="${member}"/><br/>
				</c:forEach>
				<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="moderators"/></portlet:renderURL>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" style="vertical-align:middle"/> <spring:message code="show.permissions.edit"/></a>
			</td>
			<td valign="top">
				<c:forEach items="${topic.authors}" var="member">
					<c:out value="${member}"/><br/>
				</c:forEach>
				<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="authors"/></portlet:renderURL>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" style="vertical-align:middle"/> <spring:message code="show.permissions.edit"/></a>
			</td>
			<td valign="top">
				<c:forEach items="${topic.audience}" var="member">
					<c:out value="${member}"/><br/>
				</c:forEach>
				<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="audience"/></portlet:renderURL>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" style="vertical-align:middle"/> <spring:message code="show.permissions.edit"/></a>
			</td>
		</tr>
	</table>
	<br/>

</c:if>

<a style="text-decoration:none;" href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>"><img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>