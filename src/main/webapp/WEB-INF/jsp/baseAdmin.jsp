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
<c:set var="n"><portlet:namespace/></c:set>
<script src="<rs:resourceURL value="/rs/jquery/1.6.4/jquery-1.6.4.min.js"/>" type="text/javascript"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.8.13/jquery-ui-1.8.13.min.js"/>" type="text/javascript"></script>
<link href="<c:url value="/css/baseAdmin.css"/>" rel="stylesheet" type="text/css" />

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
<div class="portlet-section-header"><spring:message code="baseAdmin.topics"/></div>
<c:if test="${pendingAnnouncementCount > 0}">
<div class="anc-approvals">
    <a class="anc-approval-list-toggle" href="#"><span id="${n}approval_count"><c:out value="${pendingAnnouncementCount}"/></span> Announcements waiting for your approval</a>
    <div class="anc-my-approvals hide">
        <h3>My Approvals</h3>
        <ul class="anc-approval-list">
            <c:forEach items="${pendingAnnouncements}" var="announcement">
                <li><a class="anc-approve" href="<portlet:renderURL/>" onclick="javascript:${n}approval(this,${announcement.parent.id},${announcement.id});return false;;"><span>Approve</span></a><a class="anc-edit" href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${announcement.id}"/></portlet:renderURL>"><span>Edit</span></a><span class="anc-topic"><c:out value="${announcement.parent.title}"/></span><span class="anc-title"><c:out value="${announcement.title}"/></span></li>
            </c:forEach>
        </ul>
    </div>
</div>
</c:if>

<table cellpadding="0" cellspacing="0" class="data" width="100%">
<c:choose>
	<c:when test="${portalAdmin}">
		<c:forEach items="${allTopics}" var="topic">
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
					<a href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL><portlet:param name="action" value="deleteTopic"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="baseAdmin.delete"/>"><img src="<c:url value="/icons/bin_empty.png"/>" height="16" width="16" border="0" alt="<spring:message code="baseAdmin.delete"/>"/></a>
				</c:if>
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

<c:if test="${portalAdmin}">
	<br/>
	<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="addTopic"/></portlet:renderURL>">
	<img src="<c:url value="/icons/add.png"/>" border="0" height="16" width="16" style="vertical-align:middle;"/> <spring:message code="baseAdmin.addnew"/>
	</a>
</c:if>

<div style="float:right;">
    <a href="<portlet:renderURL portletMode='HELP' windowState='MAXIMIZED'/>" style="float:right;">
        <img src="<c:url value='/icons/exclamation.png'/>" border="0" style="vertical-align: middle;"/><spring:message code="general.help"/>
    </a>
</div>