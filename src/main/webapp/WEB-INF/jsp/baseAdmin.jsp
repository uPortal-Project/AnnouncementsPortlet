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

<link href="<c:url value="/css/announcements.css"/>" rel="stylesheet" type="text/css" />

<c:if test="${portalAdmin}">
    <script type="text/javascript">
        function ${n}_delete() {
           return window.confirm('<spring:message code="baseAdmin.confirmDeleteTopic"/>');
        }
    </script>
</c:if>

<script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
        var toggle = document.querySelector('.anc-approvals .anc-approval-list-toggle');
        if (toggle) {
            toggle.addEventListener('click', function(e) {
                e.preventDefault();
                document.querySelectorAll('.anc-my-approvals').forEach(function(el) {
                    el.classList.toggle('hide');
                });
            });
        }
    });

    function ${n}approval(el, topicId, annId) {
        fetch('<c:url value="/ajaxApprove"/>', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: new URLSearchParams({ annId: annId, approval: 'true' })
        })
        .then(function(r) { return r.json(); })
        .then(function(data) {
            var scheduled = document.getElementById('${n}scheduled_count_' + topicId);
            var pending = document.getElementById('${n}pending_count_' + topicId);
            var displaying = document.getElementById('${n}displaying_count_' + topicId);

            switch (String(data.status)) {
                case '0': // scheduled
                    scheduled.textContent = parseInt(scheduled.textContent) + 1;
                    pending.textContent = parseInt(pending.textContent) - 1;
                    break;
                case '1': // expired
                    pending.textContent = parseInt(pending.textContent) - 1;
                    break;
                case '2': // showing
                    displaying.textContent = parseInt(displaying.textContent) + 1;
                    pending.textContent = parseInt(pending.textContent) - 1;
                    break;
            }

            var approvalCount = document.getElementById('${n}approval_count');
            var remaining = parseInt(approvalCount.textContent) - 1;
            if (remaining > 0) {
                approvalCount.textContent = remaining;
            } else {
                document.querySelectorAll('.anc-approvals').forEach(function(el) {
                    el.style.display = 'none';
                });
            }

            el.closest('li').remove();
        });
    }
</script>

<div class="container-fluid announcements-container">
    <div class="row announcements-portlet-toolbar">
        <div class="col-md-12 no-col-padding">
            <div class="nav-links">
                <c:if test="${portalAdmin}">
                    <a href="<portlet:renderURL><portlet:param name="action" value="addTopic"/></portlet:renderURL>">
                        <i class="fa fa-plus" aria-hidden="true"></i> <spring:message code="baseAdmin.addnew"/></a> |
                </c:if>
                <a href="<portlet:renderURL portletMode='HELP' windowState='MAXIMIZED'/>">
                    <i class="fa fa-question-circle" aria-hidden="true"></i> <spring:message code="baseAdmin.help"/>
                </a>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <table class="table table-condensed announcements-table">
                <caption class="sr-only"><spring:message code="baseAdmin.table"/></caption>
                <thead>
                    <th scope="col"><spring:message code="baseAdmin.header.topics"/></th>
                    <th scope="col"><spring:message code="baseAdmin.header.status"/></th>
                    <th scope="col"><spring:message code="baseAdmin.header.subscriptionmethod"/></th>
                    <th scope="col"><spring:message code="baseAdmin.header.actions"/></th>
                </thead>
                <c:choose><%-- Needs refactoring... nor reason for 3 loops --%>
                    <c:when test="${portalAdmin}">
                        <c:forEach items="${allTopics}" var="topic">
                            <tr>
                                <td>
                                    <a class="action-icon" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>">
                                        <strong><c:out value="${topic.title}"/></strong>
                                    </a>
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
                                        <c:otherwise>
                                            <spring:message code="baseAdmin.no.subMethod"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${topic.subscriptionMethod != 4}">
                                            <table role="presentation">
                                                <tr>
                                                    <td>
                                                        <a class="action-icon" href="<portlet:renderURL><portlet:param name="action" value="addTopic"/><portlet:param name="edit" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.edit"/>"><i class="fa fa-edit" aria-hidden="true"></i> <spring:message code="baseAdmin.edit"/></a>
                                                    </td>
                                                    <td>
                                                        <form action="<portlet:actionURL escapeXml="false"></portlet:actionURL>" onSubmit="return ${n}_delete()" method="post" style="display: inline-block">
                                                            <input type="hidden" name="action" value="deleteTopic"/>
                                                            <input type="hidden" name="topicId" value="${topic.id}"/>
                                                            <button type="submit">
                                                                <i class="fa fa-trash-o" aria-hidden="true"></i> <spring:message code="baseAdmin.delete"/>
                                                            </button>
                                                        </form>
                                                    </td>
                                                </tr>
                                            </table>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message code="baseAdmin.no.actions"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${adminTopics}" var="topic">
                            <tr>
                                <td>
                                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>">
                                        <strong><c:out value="${topic.title}"/></strong>
                                    </a>
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
                                        <c:otherwise>
                                            <spring:message code="baseAdmin.no.subMethod"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <c:choose>
                                        <c:when test="${topic.subscriptionMethod != 4}">
                                            <a href="<portlet:renderURL><portlet:param name="action" value="addTopic"/><portlet:param name="edit" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.edit"/>"><i class="fa fa-edit"/></a>
                                        </c:when>
                                        <c:otherwise>
                                            <spring:message code="baseAdmin.no.actions"/>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                            </tr>
                        </c:forEach>
                        <c:forEach items="${otherTopics}" var="topic">
                            <tr>
                                <td>
                                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:renderURL>" title="<spring:message code="baseAdmin.manage"/>">
                                        <strong><c:out value="${topic.title}"/></strong>
                                    </a>
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
                                        <c:when test="${topic.subscriptionMethod == 4}">
                                            <spring:message code="baseAdmin.no.subMethod"/>
                                        </c:when>
                                    </c:choose>
                                </td>
                                <td>&nbsp;</td>
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
                <h4><i class="fa fa-thumbs-up" aria-hidden="true"></i> <spring:message code="baseAdmin.myapprovals"/></h4>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <div class="anc-approvals">
                    <span id="${n}approval_count" class="approval-count"><c:out value="${pendingAnnouncementCount}"/></span>
                    <a class="anc-approval-list-toggle" href="#"><spring:message code="baseAdmin.waitingapproval"/></a>
                    <div class="anc-my-approvals hide">
                        <table class="anc-approval-list table table-condensed" role="presentation">
                            <c:forEach items="${pendingAnnouncements}" var="announcement">
                                <tr>
                                    <td>
                                        <span class="anc-title"><c:out value="${announcement.title}"/></span>
                                    </td>
                                    <td>
                                        <span class="anc-topic"><c:out value="${announcement.parent.title}"/></span>
                                    </td>
                                    <td class="text-right" width="25%">
                                        <a class="anc-approve" href="<portlet:renderURL/>" onclick="javascript:${n}approval(this,${announcement.parent.id},${announcement.id});return false;;"><i class="fa fa-check-square" aria-hidden="true"></i>  <span><spring:message code="baseAdmin.approve"/></span></a> |
                                        <a class="anc-edit" href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${announcement.id}"/></portlet:renderURL>"><i class="fa fa-pencil" aria-hidden="true"></i> <span><spring:message code="baseAdmin.edit"/></span></a>
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
