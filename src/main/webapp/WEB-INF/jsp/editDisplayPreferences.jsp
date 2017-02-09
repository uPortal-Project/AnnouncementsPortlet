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

<portlet:actionURL var="actionUrl" escapeXml="false">
	<portlet:param name="action" value="editDisplayPreferences"/>
</portlet:actionURL>

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.7.0/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

    <div class="container-fluid bootstrap-styles announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4 role="heading"><spring:message code="edit.yoursubs"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <c:if test="${isGuest}">
                        <a href="<portlet:renderURL portletMode="view"></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="edit.back"/></a>
                    </c:if>
                </div>
            </div>
        </div>
        <form action="${actionUrl}" method="post" role="form">
            <div class="row">
                <div class="col-md-8 col-md-offset-2">
                    <table class="table table-condensed announcements-table">
                        <c:forEach items="${topicSubscriptions}" var="ts" varStatus="status">
                            <c:choose>
                                <c:when test="${not isGuest}">
                                    <tr>
                                        <td>
                                            <c:choose>
                                                <c:when test="${ts.topic.subscriptionMethod == 1}">
                                                    <input type="checkbox" disabled="disabled" checked="checked" value="true" name="subscribed_${status.index}"/>
                                                </c:when>
                                                <c:when test="${ts.topic.subscriptionMethod != 1 and ts.subscribed}">
                                                    <input type="checkbox" checked="checked" value="true" name="subscribed_${status.index}"/>
                                                </c:when>
                                                <c:when test="${ts.topic.subscriptionMethod != 1 and not ts.subscribed}">
                                                    <input type="checkbox" value="true" name="subscribed_${status.index}"/>
                                                </c:when>
                                            </c:choose>
                                            <input type="hidden" name="topicId_${status.index}" value="${ts.topic.id}"/>
                                            <input type="hidden" name="topicSubId_${status.index}" value="${ts.id}"/>
                                        </td>
                                        <td>
                                            <c:out value="${ts.topic.title}"/>
                                        </td>
                                        <td>
                                            <c:out value="${ts.topic.description}"/>
                                        </td>
                                        <td>
                                            <c:if test="${ts.topic.allowRss}">
                                                <a href="<c:url value="/getRssFeed?topic=${ts.topic.id}"/>" title="<spring:message code="edit.rss"/>"><i class="fa fa-lg fa-rss"</a>
                                            </c:if>
                                        </td>
                                    </tr>
                                </c:when>
                                <c:otherwise>
                                    <c:if test="${ts.topic.subscriptionMethod == 1}">
                                        <tr>
                                            <td>
                                                <input type="checkbox" disabled="disabled" checked="checked" value="true" name="subscribed_${status.index}"/>
                                                <input type="hidden" name="topicId_${status.index}" value="${ts.topic.id}"/>
                                                <input type="hidden" name="topicSubId_${status.index}" value="${ts.id}"/>
                                            </td>
                                            <td>
                                                <c:out value="${ts.topic.title}"/>
                                            </td>
                                            <td>
                                                <c:out value="${ts.topic.description}"/>
                                            </td>
                                            <td>
                                                <c:if test="${ts.topic.allowRss}">
                                                    <a href="<c:url value="/getRssFeed?topic=${ts.topic.id}"/>" title="<spring:message code="edit.rss"/>"><i class="fa fa-lg fa-rss"</a>
                                                </c:if>
                                            </td>
                                        </tr>
                                    </c:if>
                                </c:otherwise>
                            </c:choose>
                        </c:forEach>
                    </table>
                </div>
            </div>
            <div class="row announcements-portlet-toolbar">
                <div class="col-md-12 no-col-padding">
                    <h4><spring:message code="edit.yourprefs"/></h4>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <c:choose>
                        <c:when test="${not prefHideAbstract}">
                            <input type="checkbox" value="true" name="hideAbstract"/>
                            <label><spring:message code="edit.pref.hideabstract"/></label>
                        </c:when>
                        <c:otherwise>
                            <input type="checkbox" value="true" name="hideAbstract" checked="checked"/>
                            <label><spring:message code="edit.pref.hideabstract"/></label>
                        </c:otherwise>
                    </c:choose>
                </div>
            </div>
            <c:if test="${not isGuest}">
                <div class="row">
                    <div class="col-md-12">
                        <input type="hidden" name="topicsToUpdate" value="${topicsToUpdate}"/>
                        <button type="submit" class ="btn btn-primary"><spring:message code="edit.update"/></button>
                        &nbsp;&nbsp;
                        <a class="btn btn-default" href="<portlet:renderURL portletMode="view" windowState="normal"/>"><spring:message code="general.cancelandreturn"/></a>
                    </div>
                </div>
            </c:if>
        </form>
    </div>
