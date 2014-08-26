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

<c:set var="n"><portlet:namespace/></c:set>
<script src="http://code.jquery.com/jquery-1.10.2.min.js" type="text/javascript"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js" type="text/javascript"></script>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css" />
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.0.3/css/font-awesome.css'/>" type="text/css" />
<link href="<c:url value="/css/announcements.css"/>" rel="stylesheet" type="text/css" />

<c:if test="${portalAdmin}">
    <script type="text/javascript">
        function <portlet:namespace/>_delete(url) {
           var response = window.confirm('<spring:message code="baseAdmin.confirmDeleteTopic"/>');
           if (response) {
              window.location = url;
           }
        }
    </script>
</c:if>

<script type="text/javascript">
    var ${n} = ${n} || {}; //create a unique variable to assign our namespace too
    ${n}.jQuery = jQuery.noConflict(true); //assign jQuery to this namespace

    /*  runs when the document is finished loading.  This prevents things like the 'div' from being fully created */
    ${n}.jQuery(function () {
        var $ = ${n}.jQuery; //reassign $ for normal use of jQuery
        $(".anc-approvals .anc-approval-list-toggle").click(function(e) {
            e.preventDefault();
            $(".anc-my-approvals").toggleClass("hide");
        });
    });

    function ${n}approval(el,topicId,annId) {
        var $ = ${n}.jQuery;
        $.post("<c:url value="/ajaxApprove"/>",
        {
            annId: annId,
            approval: 'true'
        },
        function(data) {
            switch(data.status) {
                        case "0": //scheduled
                        {
                            $("#${n}scheduled_count_"+topicId).text(parseInt($("#${n}scheduled_count_"+topicId).text())+1);
                            $("#${n}pending_count_"+topicId).text(parseInt($("#${n}pending_count_"+topicId).text())-1);
                            break;
                        }
                        case "1": //expired
                        {
                            $("#${n}pending_count_"+topicId).text(parseInt($("#${n}pending_count_"+topicId).text())-1);
                            break;
                        }
                        case 2: //showing
                        {
                            $("#${n}displaying_count_"+topicId).text(parseInt($("#${n}displaying_count_"+topicId).text())+1);
                            $("#${n}pending_count_"+topicId).text(parseInt($("#${n}pending_count_"+topicId).text())-1);
                            break;
                        }
                        case "3": //pending
                        {
                            break;
                        }
                    }

                    var pendingApproval = parseInt($("#${n}approval_count").text())-1;
                    if(pendingApproval > 0) {
                        $("#${n}approval_count").text(pendingApproval);
                    } else {
                        $(".anc-approvals").hide();
                    }

                    $(el).parent('li').remove();
                },
                "json"
                );
    }
</script>

<div class="container-fluid bootstrap-styles announcements-container">
    <div class="row announcements-portlet-toolbar">
        <div class="col-md-12 no-col-padding">
            <div class="nav-links">
                <c:if test="${portalAdmin}">
                    <a href="<portlet:renderURL><portlet:param name="action" value="addTopic"/></portlet:renderURL>">
                        <i class="fa fa-plus"></i> <spring:message code="baseAdmin.addnew"/></a> |
                </c:if>
                <a href="<portlet:renderURL portletMode='HELP' windowState='MAXIMIZED'/>">
                    <i class="fa fa-question-circle"></i>
                </a>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table class="table table-condensed announcements-table">
                <thead>
                    <th><spring:message code="baseAdmin.header.topics"/></th>
                    <th><spring:message code="baseAdmin.header.status"/></th>
                    <th><spring:message code="baseAdmin.header.subscriptionmethod"/></th>
                    <th><spring:message code="baseAdmin.header.actions"/></th>
                </thead>
                <c:choose>
                    <c:when test="${portalAdmin}">
                        <c:forEach items="${allTopics}" var="topic">
                            <tr>
                                <td>
                                    <c:out value="${topic.title}"/>
                                </td>
                                <td class="text-center">
                                    ( <span id="${n}displaying_count_${topic.id}"><c:out value="${topic.displayingAnnouncementCount}"/></span>, <span id="${n}scheduled_count_${topic.id}"><c:out value="${topic.scheduledAnnouncementCount}"/></span>, <span id="${n}pending_count_${topic.id}"><c:out value="${topic.pendingAnnouncementCount}"/></span> )
                                </td>
                                <td class="text-center">
                                    <c:choose>
                                        <c:when test="${topic.subscriptionMethod == 1}">
                                            <spring:message code="addTopic.pushedforced"/>
                                        </c:when>
                                        <c:when test="${topic.subscriptionMethod == 2}">
                                            <spring:message code="addTopic.pushedoptional"/>
                                        </c:when>
                                        <c:when test="${topic.subscriptionMethod == 3}">
                                            <spring:message code="addTopic.optional"/>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                    <table class="action-icon-table">
                                        <tr>
                                            <td>
                                                <a class="action-icon" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>"><i class="fa fa-gears"></i></a>
                                            </td>
                                            <td>
                                                <c:if test="${topic.subscriptionMethod != 4}">
                                                        <a class="action-icon" href="<portlet:renderURL><portlet:param name="action" value="addTopic"/><portlet:param name="edit" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.edit"/>"><i class="fa fa-edit"></i></a>
                                                </c:if>
                                                </td>
                                            <td>
                                                <c:if test="${topic.subscriptionMethod != 4}">
                                                    <a class="action-icon" href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL escapeXml="false"><portlet:param name="action" value="deleteTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="baseAdmin.delete"/>"><i class="fa fa-trash-o"></i></a>
                                            </c:if>
                                            </td>
                                        </tr>
                                    </table>


                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${adminTopics}" var="topic">
                            <tr>
                                <td>
                                    <c:out value="${topic.title}"/>
                                </td>
                                <td>
                                    ( <span id="${n}displaying_count_${topic.id}"><c:out value="${topic.displayingAnnouncementCount}"/></span>, <span id="${n}scheduled_count_${topic.id}"><c:out value="${topic.scheduledAnnouncementCount}"/></span>, <span id="${n}pending_count_${topic.id}"><c:out value="${topic.pendingAnnouncementCount}"/></span> )
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${topic.subscriptionMethod == 1}">
                                            <spring:message code="addTopic.pushedforced"/>
                                        </c:when>
                                        <c:when test="${topic.subscriptionMethod == 2}">
                                            <spring:message code="addTopic.pushedoptional"/>
                                        </c:when>
                                        <c:when test="${topic.subscriptionMethod == 3}">
                                            <spring:message code="addTopic.optional"/>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>"><img src="<c:url value="/icons/cog.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.manage"/>"/></a>&nbsp;&nbsp;
                                    <c:if test="${topic.subscriptionMethod != 4}">
                                        <a href="<portlet:renderURL><portlet:param name="action" value="addTopic"/><portlet:param name="edit" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.edit"/>"><img src="<c:url value="/icons/pencil.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.edit"/>"/></a>&nbsp;&nbsp;
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:forEach items="${otherTopics}" var="topic">
                            <tr>
                                <td>
                                    <c:out value="${topic.title}"/>
                                </td>
                                <td>&nbsp;</td>
                                <td>
                                    <c:choose>
                                        <c:when test="${topic.subscriptionMethod == 1}">
                                            <spring:message code="addTopic.pushedforced"/>
                                        </c:when>
                                        <c:when test="${topic.subscriptionMethod == 2}">
                                            <spring:message code="addTopic.pushedoptional"/>
                                        </c:when>
                                        <c:when test="${topic.subscriptionMethod == 3}">
                                            <spring:message code="addTopic.optional"/>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>
                                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>"><img src="<c:url value="/icons/cog.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.manage"/>"/></a>&nbsp;&nbsp;
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </table>
        </div>
    </div>
    <c:if test="${pendingAnnouncementCount > 0}">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-12 no-col-padding">
                <h4><i class="fa fa-thumbs-up"></i> <spring:message code="baseAdmin.myapprovals"/></h4>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <div class="anc-approvals">
                    <span id="${n}approval_count" class="approval-count"><c:out value="${pendingAnnouncementCount}"/></span>
                    <a class="anc-approval-list-toggle" href="#"><spring:message code="baseAdmin.waitingapproval"/></a>
                    <div class="anc-my-approvals hide">
                        <table class="anc-approval-list table table-condensed">
                            <c:forEach items="${pendingAnnouncements}" var="announcement">
                                <tr>
                                    <td>
                                        <span class="anc-title"><c:out value="${announcement.title}"/></span>
                                    </td>
                                    <td>
                                        <span class="anc-topic"><c:out value="${announcement.parent.title}"/></span>
                                    </td>
                                    <td class="text-right" width="25%">
                                        <a class="anc-approve" href="<portlet:renderURL/>" onclick="javascript:${n}approval(this,${announcement.parent.id},${announcement.id});return false;;"><i class="fa fa-check-square"></i>  <span><spring:message code="baseAdmin.approve"/></span></a> |
                                        <a class="anc-edit" href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${announcement.id}"/></portlet:renderURL>"><i class="fa fa-pencil"></i> <span><spring:message code="baseAdmin.edit"/></span></a>
                                    </td>
                                </tr>
                            </c:forEach>
                        </table>
                    </div>
                </div>
            </div>
        </div>
    </c:if>
</div>
