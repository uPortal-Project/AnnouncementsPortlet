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
<portlet:actionURL var="actionUrl">
	<portlet:param name="action" value="addTopic"/>
</portlet:actionURL>

<form:form commandName="topic" method="post" action="${actionUrl}">
	
	<spring:message code="addTopic.title"/> <form:input cssClass="portlet-form-input-field" path="title" size="30" maxlength="80" /> 
		<form:errors cssClass="portlet-msg-error" path="title"/> <br/>
	<spring:message code="addTopic.description"/> <form:input cssClass="portlet-form-input-field" path="description" size="30" maxlength="80" /> <br/>
	<spring:message code="addTopic.publicrss"/> <form:checkbox path="allowRss" cssClass="portlet-form-input-field"/> <br/>
	
	<spring:message code="addTopic.submethod"/> <form:errors cssClass="portlet-msg-error" path="subscriptionMethod"/> <br/>
	&nbsp;&nbsp;&nbsp;<form:radiobutton path="subscriptionMethod" value="1"/> <spring:message code="addTopic.pushedforced"/><br/>
	&nbsp;&nbsp;&nbsp;<form:radiobutton path="subscriptionMethod" value="2"/> <spring:message code="addTopic.pushedoptional"/><br/>
	&nbsp;&nbsp;&nbsp;<form:radiobutton path="subscriptionMethod" value="3"/> <spring:message code="addTopic.optional"/><br/>
	<form:hidden path="id"/>
	<form:hidden path="creator"/>
	<br/>
	<button type="submit" class="portlet-form-button"><spring:message code="addTopic.saveButton"/></button>
	&nbsp;&nbsp;<a href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>"><spring:message code="general.cancelandreturn"/>
	</a>
</form:form>

