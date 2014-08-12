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

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.0.3/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4 role="heading">
                    <spring:message code="addMembers.assigning"/>
                    <c:choose>
                        <c:when test="${groupKey eq 'admins'}"><spring:message code="general.admins"/></c:when>
                        <c:when test="${groupKey eq 'moderators'}"><spring:message code="general.moderators"/></c:when>
                        <c:when test="${groupKey eq 'authors'}"><spring:message code="general.authors"/></c:when>
                        <c:when test="${groupKey eq 'audience'}"><spring:message code="general.audience"/></c:when>
                    </c:choose>
                </h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="general.backtotopic"/></a> |
                    <a href="<portlet:renderURL />"><i class="fa fa-home"></i> <spring:message code="general.adminhome"/></a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-5">
                    <h5 role="heading"><spring:message code="addMembers.availableRoles"/></h5>
                <form:form method="post" action="${actionUrl}" commandName="selection" role="form">
                    <div class="form-group">
                        <c:forEach items="${roles}" var="roleIter" varStatus="roleCounter">
                            <c:if test="${not roleIter.person}">
                                <div class="radio">
                                    <form:checkbox path="selectedRoles" value="${roleIter.name}" />
                                    <label for="selectedRoles${roleCounter.count}"><c:out value="${roleIter.name}"/></label>
                                </div>
                            </c:if>
                        </c:forEach>
                    </div>
                    <button type="submit" class="btn btn-primary"><spring:message code="addMembers.update"/></button>
                    <a class="btn btn-link" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>"><spring:message code="addMembers.cancel"/></a>
                </form:form>
            </div>
            <div class="col-md-7">
                <h5 role="heading"><spring:message code="addMembers.users"/></h5>
                <form method="post" action="${actionUrlUser}" role="form">
                    <div class="form-group">
                        <c:forEach items="${roles}" var="roleIter">
                            <c:if test="${roleIter.person}">
                                <c:out value="${roleIter.personName}"/> <a href="<portlet:actionURL escapeXml="false"><portlet:param name="userKey" value="${roleIter.name}"/><portlet:param name="action" value="deleteUser"/><portlet:param name="groupKey" value="${groupKey}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>" title="<spring:message code="addMembers.deleteUser"/>"><i class="fa fa-trash-o"></i></a>
                            </c:if>
                        </c:forEach>
                    </div>
                    <div class="form-group">
                        <label for="add-username"><spring:message code="addMembers.addUser"/></label>
                        <input id="add-username" type="text" name="userAdd" class="form-control"/>
                    </div>
                    <button type="submit" class="btn btn-primary"><spring:message code="addMembers.addUserButton"/></button>
                </form>
            </div>
        </div>
    </div>


