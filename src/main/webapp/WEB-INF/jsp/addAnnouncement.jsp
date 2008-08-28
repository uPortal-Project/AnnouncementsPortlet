<%@ include file="/WEB-INF/jsp/include.jsp" %>

<script type="text/javascript" src="<c:url value="/date-picker/js/datepicker.js"/>"></script>
<script type="text/javascript" src="<c:url value="/tinymce/tiny_mce.js"/>"></script>

<style type="text/css">
<!--
@import url(<c:url value="/date-picker/css/datepicker.css"/>);
-->
</style>

<portlet:actionURL var="actionUrl">
	<portlet:param name="action" value="addAnnouncement"/>
	<portlet:param name="topicId" value="${topicId}"/>
</portlet:actionURL>

<div class="portlet-section-header"><spring:message code="addAnnouncement.header"/> <c:out value="${topicTitle}"/></div>

<form:form commandName="announcement" method="post" action="${actionUrl}">
<table width="100%" cellpadding="3">
	<tr>
		<td valign="top" width="20%">
			<spring:message code="addAnnouncement.title"/>
		</td>
		<td>
			<form:errors cssClass="portlet-msg-error" path="title"/>
	 		<form:input cssClass="portlet-form-input-field" path="title" size="30" maxlength="80"/>		
		</td>
	</tr>
	<tr>
		<td valign="top">
			<spring:message code="addAnnouncement.abstract"/>
		</td>
		<td>
			<form:errors cssClass="portlet-msg-error" path="abstractText"/>
			<form:textarea cssClass="portlet-form-input-field" path="abstractText" rows="2" cols="40" cssStyle="width:80%;"/>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<spring:message code="addAnnouncement.message"/>
		</td>
		<td>
			<form:errors cssClass="portlet-msg-error" path="message"/>
			<form:textarea cssClass="portlet-form-input-field mceEditor" path="message" rows="5" cols="30" cssStyle="width:100%;"/>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<spring:message code="addAnnouncement.link"/>
		</td>
		<td>
			<form:errors cssClass="portlet-msg-error" path="link"/>
			<form:input cssClass="portlet-form-input-field" path="link" size="30" maxlength="255"/>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<spring:message code="addAnnouncement.start"/>
		</td>
		<td>
			<form:errors cssClass="portlet-msg-error" path="startDisplay"/>
			<form:input cssClass="portlet-form-input-field ${datePickerFormat} range-low-today no-transparency" path="startDisplay" size="11" maxlength="10"/>
		</td>
	</tr>
	<tr>
		<td valign="top">
			<spring:message code="addAnnouncement.end"/>
		</td>
		<td>
			<form:errors cssClass="portlet-msg-error" path="endDisplay"/>
	 		<form:input cssClass="portlet-form-input-field ${datePickerFormat} range-low-today no-transparency" path="endDisplay" size="11" maxlength="10"/>
		</td>
	</tr>
</table>
<form:hidden path="id"/>
<form:hidden path="created"/>
<form:hidden path="author"/>
<button type="submit" class="portlet-form-button"><spring:message code="addAnnouncement.save"/></button>
</form:form>
<br/>
<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topicId}"/></portlet:renderURL>">
<img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.backtotopic"/></a>
&nbsp;&nbsp;
<a style="text-decoration:none;" href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>">
<img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>

<script type="text/javascript">
<!--
tinyMCE.init({
	mode : "textareas",
	editor_selector : "mceEditor",
	theme : "advanced",
	theme_advanced_buttons1 : "bold,separator,bullist,numlist,undo,redo,link,unlink",
	theme_advanced_buttons2 : "",
	theme_advanced_buttons3 : "",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left",
	extended_valid_elements : "a[name|href|target|title|onclick],span[class|align|style]"
});
//-->
</script>

