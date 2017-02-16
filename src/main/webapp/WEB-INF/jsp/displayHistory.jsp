<%--

    Licensed to Apereo under one or more contributor license
    agreements. See the NOTICE file distributed with this work
    for additional information regarding copyright ownership.
    Apereo licenses this file to you under the Apache License,
    Version 2.0 (the "License"); you may not use this file
    except in compliance with the License.  You may obtain a
    copy of the License at the following location:

      http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing,
    software distributed under the License is distributed on an
    "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
    KIND, either express or implied.  See the License for the
    specific language governing permissions and limitations
    under the License.

--%>
<jsp:directive.include file="/WEB-INF/jsp/include.jsp"/>
<c:set var="n"><portlet:namespace/></c:set>

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.7.0/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

    <div class="container-fluid bootstrap-styles announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-12 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL />"><i class="fa fa-arrow-left"></i> <spring:message code="display.back"/></a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="ann-user-archive-table col-md-12">
                <table id="historyTable" class="table table-condensed announcements-table">
                    <thead>
                        <tr>
                            <th width="15%"><spring:message code="display.header.topic"/></th>
                            <th><spring:message code="display.header.ann"/></th>
                            <th><spring:message code="display.header.start"/></th>
                            <th><spring:message code="display.header.end"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${announcements}" var="announcement" varStatus="status">
                            <tr>
                                <td><c:out value="${announcement.parent.title}"/></td>
                                <td>
                                    <p><a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncementHistory"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a></p>
                                    <p><c:out value="${announcement.abstractText}"/></p>
                                    <c:if test="${not empty announcement.link}">
                                        <p><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></p>
                                    </c:if>
                                </td>
                                <td><p><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></p></td>
                                <td><p><fmt:formatDate value="${announcement.endDisplay}" dateStyle="medium"/></p></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>


