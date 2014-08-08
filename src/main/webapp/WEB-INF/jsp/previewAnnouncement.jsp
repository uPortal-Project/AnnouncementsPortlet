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

<c:if test="${includeJQuery}">
<script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
</c:if>

<rs:aggregatedResources path="skin.xml"/>
<style type="text/css">
.preview-section-header { font-weight: bold; margin-top: 10px; margin-bottom: 5px; }
.preview-section { background-color: #f3f3f3; padding: 5px; }
</style>

<div class="portlet-section-header"><spring:message code="preview.header"/></div>

<div class="preview-section-header"><spring:message code="preview.listview"/></div>

<div class="preview-section">
    <table width="100%" cellspacing="0" cellpadding="0" class="data">
        <tr>
            <td align="center" width="15%" class="<portlet:namespace/>-row1color">
                <c:out value="${announcement.parent.title}"/>
                <br/>
                <span class="portlet-section-text" style="font-size:0.9em;"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span>
            </td>
            <td class="<portlet:namespace/>-row1color">
                <a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a>
                <br/><c:out value="${announcement.abstractText}"/>
                <br/>
                <c:if test="${not empty announcement.link}">
                    <span class="portlet-section-text" style="font-size:0.9em; padding-top:0.2em;"><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></span>
                </c:if>
            </td>
        </tr>
    </table>
</div>

<div class="preview-section-header"><spring:message code="preview.fullview"/></div>

<div class="preview-section">
    <div class="portlet-section-header"><c:out value="${announcement.title}"/></div>
    <p>
        <span class="portlet-section-text" style="font-size:0.8em;"><spring:message code="displayFull.displayEnd"/> <fmt:formatDate value="${announcement.endDisplay}" dateStyle="long"/></span>
        <c:if test="${not empty announcement.link}">
            <br/>
            <span class="portlet-section-text" style="font-size:0.8em;"><spring:message code="display.link.prefix"/> <a href="${announcement.link}"><c:out value="${announcement.link}"/></a></span>
        </c:if>
    </p>
    <c:out value="${announcement.message}" escapeXml="false"/>
</div>

<br/>
<form id="<portlet:namespace/>Form">
    <c:if test="${ user.moderator and !announcement.published }">
        <input class="portlet-form-button" type="submit" value="<spring:message code="show.publish"/>"/>
    </c:if>
    <c:if test="${ user.moderator or (user.userName == announcement.author) }">
        <a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${announcement.id}"/></portlet:renderURL>" title="<spring:message code="show.viewedit"/>"><spring:message code="show.edit"/></a>
    </c:if>
</form>

<p style="text-align:right">
    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view"><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ announcement.parent.id }"/></portlet:renderURL>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.backtotopic"/></a>
    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view"></portlet:renderURL>"><img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>
</p>

<script type="text/javascript">
var <portlet:namespace/> = <portlet:namespace/> || {};
<portlet:namespace/>.jQuery = ${ includeJQuery ? 'jQuery.noConflict(true)' : 'jQuery' };
<portlet:namespace/>.jQuery(function(){
   var $ = <portlet:namespace/>.jQuery;

   $(document).ready(function(){
       $("#<portlet:namespace/>Form").submit(function(){
           $.post("<c:url value="/ajaxApprove"/>",
               {
                   annId: "${announcement.id}",
                   approval: true
               },
               function(){
                   window.location = "<portlet:renderURL portletMode="view"><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ announcement.parent.id }"/></portlet:renderURL>";
                   return false;
               }
           );
       });
   });
});
</script>
