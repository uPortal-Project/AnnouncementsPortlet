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
<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.0.3/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<c:if test="${includeJQuery}">
    <script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
</c:if>

    <script type="text/javascript">
        var <portlet:namespace/> = <portlet:namespace/> || {};
        <portlet:namespace/>.jQuery = ${ includeJQuery ? 'jQuery.noConflict(true)' : 'jQuery' };
        function <portlet:namespace/>_delete(url) {
            var response = window.confirm('<spring:message code="show.deleteAnn"/>');
            if (response) {
                window.location = url;
            }
        }
        function <portlet:namespace/>approval(id, newValue) {
            var $ = <portlet:namespace/>.jQuery;
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
    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4 class="title" role="heading"><spring:message code="show.annfor"/> <c:out value="${topic.title}"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>"><i class="fa fa-plus"></i> <spring:message code="show.addAnn"/></a> |
                    <a href="<portlet:renderURL></portlet:renderURL>"><i class="fa fa-home"></i> <spring:message code="general.adminhome"/></a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <table class="table table-condensed announcements-table">
                    <c:choose>
                        <c:when test="${empty announcements}">
                            <tr>
                                <td>
                                    <spring:message code="show.none"/>
                                </td>
                            </tr>
                        </c:when>
                        <c:otherwise>
                            <thead>
                                <th><spring:message code="show.head.status"/></th>
                                <th width="50%"><spring:message code="show.head.title"/></th>
                                <th><spring:message code="show.head.displaying"/></th>
                                <th></th>
                            </thead>
                            <tbody>
                                <c:forEach items="${announcements}" var="ann">
                                    <tr>
                                        <c:choose>
                                            <c:when test="${ann.published}">
                                                <c:choose>
                                                    <c:when test="${ann.startDisplay > now}">
                                                        <td>
                                                            <p id="<portlet:namespace/>annStatus-${ann.id}" class="label label-success"><spring:message code="show.scheduled"/></p>
                                                        </td>
                                                    </c:when>
                                                    <c:when test="${ann.endDisplay < now}">
                                                        <td>
                                                            <p id="<portlet:namespace/>annStatus-${ann.id}" class="label label-danger"><spring:message code="show.expired"/></p>
                                                        </td>
                                                    </c:when>
                                                        <c:otherwise>
                                                        <td>
                                                            <p id="<portlet:namespace/>annStatus-${ann.id}" class="label label-success"><spring:message code="show.showing"/></p>
                                                        </td>
                                                    </c:otherwise>
                                                </c:choose>
                                            </c:when>
                                        <c:otherwise>
                                            <td>
                                                <p id="<portlet:namespace/>annStatus-${ann.id}" class="label label-danger"><spring:message code="show.pending"/></p>
                                            </td>
                                        </c:otherwise>
                                    </c:choose>
                                    <td class="text-left">
                                        <a title="<spring:message code="show.preview"/>"  href="<portlet:renderURL><portlet:param name="action" value="previewAnnouncement"/><portlet:param name="annId" value="${ ann.id }"/></portlet:renderURL>"><c:out value="${ann.title}"/></a>
                                    </td>
                                    <td>
                                        <fmt:formatDate value="${ann.startDisplay}" dateStyle="short"/> - <fmt:formatDate value="${ann.endDisplay}" dateStyle="short"/>
                                    </td>
                                    <td class="text-right">
                                        <c:if test="${((not user.moderator) and user.userName eq ann.author) or user.moderator}">
                                            <a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${ann.id}"/></portlet:renderURL>" title="<spring:message code="show.viewedit"/>"><i class="fa fa-plus"></i></a>&nbsp;
                                        </c:if>
                                        <c:if test="${user.moderator}">
                                            <a href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL escapeXml="false"><portlet:param name="action" value="deleteAnnouncement"/><portlet:param name="annId" value="${ann.id}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="show.delete"/>"><i class="fa fa-trash-o"></i></a>&nbsp;
                                            <c:choose>
                                                <c:when test="${ann.published}">
                                                    <a id="<portlet:namespace/>annSwitch-${ann.id}" href="javascript:<portlet:namespace/>approval(${ann.id},'false');" title="<spring:message code="show.unpublish"/>"><i class="fa fa-stop"></i></a>
                                                </c:when>
                                                <c:otherwise>
                                                    <a id="<portlet:namespace/>annSwitch-${ann.id}" href="javascript:<portlet:namespace/>approval(${ann.id},'true');" title="<spring:message code="show.publish"/>"><i class="fa fa-check-square"></i></a>
                                                </c:otherwise>
                                            </c:choose>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </c:otherwise>
                </c:choose>
            </table>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <a class="btn btn-default btn-xs pull-right" href="<portlet:renderURL><portlet:param name="action" value="showHistory"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>"><spring:message code="show.history"/> <i class="fa fa-archive"></i></a>
        </div>
    </div>
    <c:if test="${user.admin}">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-12 no-col-padding">
                <h4 role="heading"><spring:message code="show.header.permissions"/> <c:out value="${topic.title}"/></h4>
            </div>
        </div>

    <table class="table table-condensed">
        <tr>
            <td class="permissions-group-container">
                <a href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="admin"/></portlet:renderURL>">
                    <h5><spring:message code="general.admins"/><span class="pull-right"><i class="fa fa-edit"></i></span></h5>
                </a>
                <c:forEach items="${topic.admins}" var="member">
                    <small>- <c:out value="${member}"/></small><br />
                </c:forEach>
            </td>
            <td class="permissions-group-container">
                <a href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="moderator"/></portlet:renderURL>">
                    <h5><spring:message code="general.moderators"/><span class="pull-right"><i class="fa fa-edit"></i></span></h5>
                </a>
                <c:forEach items="${topic.moderators}" var="member">
                    <small>- <c:out value="${member}"/></small><br />
                </c:forEach>
            </td>
            <td class="permissions-group-container">
                <a href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="author"/></portlet:renderURL>">
                    <h5><spring:message code="general.authors"/><span class="pull-right"><i class="fa fa-edit"></i></span></h5>
                </a>
                <c:forEach items="${topic.authors}" var="member">
                    <small>- <c:out value="${member}"/></small><br />
                </c:forEach>

        </td>
        <td class="permissions-group-container">
            <a href="<portlet:renderURL><portlet:param name="action" value="addMembers"/><portlet:param name="topicId" value="${topic.id}"/><portlet:param name="groupKey" value="audience"/></portlet:renderURL>">
                <h5><spring:message code="general.audience"/><span class="pull-right"><i class="fa fa-edit"></i></span></h5>
            </a>
            <c:forEach items="${topic.audience}" var="member">
                <small>- <c:out value="${member}"/></small><br />
            </c:forEach>
        </td>
        </tr>
    </table>
    <br/>
    </c:if>
    </div>
