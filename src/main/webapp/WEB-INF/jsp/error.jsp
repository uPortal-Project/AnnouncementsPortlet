<%@ include file="/WEB-INF/jsp/include.jsp" %>

<div class="portlet-section-header"><spring:message code="error.header"/></div>

<c:out value="${exception.message}"/>

<br/><br/>
<a href="<portlet:renderURL portletMode="view" windowState="normal"/>"><spring:message code="error.goback"/></a>