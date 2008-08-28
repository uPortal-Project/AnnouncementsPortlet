<%@ include file="/WEB-INF/jsp/include.jsp" %>

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

