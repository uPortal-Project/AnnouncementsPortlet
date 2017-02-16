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
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<c:if test="${portletPreferencesValues['includeJQuery'][0] != 'false'}">
    <script type="text/javascript" src="<rs:resourceURL value="/rs/jquery/1.10.2/jquery-1.10.2.min.js"/>"></script>
</c:if>

    <div class="container-fluid bootstrap-styles announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4 role="heading"><spring:message code="preview.header"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <c:if test="${ user.moderator or (user.userName == announcement.author) }">
                        <a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${announcement.id}"/></portlet:renderURL>" title="<spring:message code="show.viewedit"/>"><i class="fa fa-edit"></i>  <spring:message code="show.edit"/></a> |
                    </c:if>
                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ announcement.parent.id }"/></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="general.backtotopic"/></a> |
                    <a href="<portlet:renderURL></portlet:renderURL>"><i class="fa fa-home"></i> <spring:message code="general.adminhome"/></a>
                </div>
            </div>
        </div>
        <div class="ann-display-item-summary">
            <div class="row">
                <div class="col-xs-12">
                    <p><spring:message code="preview.listview"/>:</p>
                </div>
            </div>
            <div class="row">
                <div class="col-xs-12">
                    <div class="ann-item-header">
                        <div class="ann-item-topic"><i class="fa fa-list-alt"></i> <c:out value="${announcement.parent.title}"/></div>
                        <div class="ann-item-date"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></div>
                    </div>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <h1><a title="<c:out value="${annLinkTitle}"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a></h1>
                    <p><c:out value="${announcement.abstractText}"/></p>
                    <c:if test="${not empty announcement.link}">
                        <p ><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></p>
                    </c:if>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-xs-12">
                <p><spring:message code="preview.fullview"/>:</p>
            </div>
        </div>
        <div class="ann-display-item-full">
            <div class="row">
                <div class="col-xs-12">
                    <h2><c:out value="${announcement.title}"/></h2>
                    <p><spring:message code="displayFull.displayEnd"/> <fmt:formatDate value="${announcement.endDisplay}" dateStyle="long"/></p>
                    <c:if test="${not empty announcement.link}">
                        <p><spring:message code="display.link.prefix"/> <a href="${announcement.link}"><c:out value="${announcement.link}"/></a></p>
                    </c:if>
                    <p><c:out value="${announcement.message}" escapeXml="false"/></p>
                </div>
            </div>
        </div>
        <c:if test="${ user.moderator and !announcement.published }">
            <div class="row">
                <div class="col-xs-12">
                    <form id="${n}Form">
                        <input class="btn btn-success" type="submit" value="<spring:message code="show.publish"/>"/>
                    </form>
                </div>
            </div>
        </c:if>
    </div>

<script type="text/javascript"><rs:compressJs>
    var ${n} = ${n} || {};

<c:choose>
    <c:when test="${portletPreferencesValues['includeJQuery'][0] != 'false'}">
        ${n}.jQuery = jQuery.noConflict(true)
    </c:when>
    <c:otherwise>
        ${n}.jQuery = up.jQuery;
    </c:otherwise>
</c:choose>

${n}.jQuery(function() {
    var $ = ${n}.jQuery;

    $(document).ready(function(){
            $("#${n}Form").submit(function(){
               $.post("<c:url value="/ajaxApprove"/>",
                   {
                       annId: "${announcement.id}",
                       approval: true
                   },
                   function(){
                       window.location = "<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ announcement.parent.id }"/></portlet:renderURL>";
                       return false;
                   }
               );
           });
       });
    });

</rs:compressJs></script>
