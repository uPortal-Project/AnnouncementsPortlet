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

<style type="text/css">
.<portlet:namespace/>-row1color { padding: 5px; background-color: #eee; }
.<portlet:namespace/>-row2color { padding: 5px; background-color: #fff; }
.<portlet:namespace/>-emerg { padding: 5px; margin-bottom:5px; color:#c00; background-color: #fff; border: 3px solid #cc3300; }
.<portlet:namespace/>-emerg a, .<portlet:namespace/>-emerg a:visited { text-decoration: none; color: #c00; }
.announcements-scrolling { overflow: auto; }
</style>

<c:set var="n"><portlet:namespace/></c:set>

<c:if test="${hideAbstract}">
    <script src="http://code.jquery.com/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js" type="text/javascript"></script>
    <script src="<c:url value="/rs/jquery-tooltip/1.3/jquery.tooltip.js"/>" type="text/javascript"></script>
    <script type="text/javascript">
        var ${n} = ${n} || {}; //create a unique variable to assign our namespace too
        ${n}.jQuery = jQuery.noConflict(true); //assign jQuery to this namespace

        /*  runs when the document is finished loading.  This prevents things like the 'div' from being fully created */
        ${n}.jQuery(function () {
            var $ = ${n}.jQuery; //reassign $ for normal use of jQuery

            $(".announcement-link-tooltip").tooltip({
                showURL: false,
                position: { offset: "15 15" }
            });
        });
    </script>

    <style>
        #tooltip {
            padding:8px;
            opacity: 0.85;
            position:absolute;
            z-index:9999;
            -o-box-shadow: 0 0 5px #aaa;
            -moz-box-shadow: 0 0 5px #aaa;
            -webkit-box-shadow: 0 0 5px #aaa;
            box-shadow: 0 0 5px #aaa;
            max-width: 400px;
            background-color: #ffffff;
            background-image: none;
            border: 1px solid #111;
            border-width:2px;
            font-size: 11px;
            font-family: inherit;
        }
        #tooltip h3, #tooltip div { margin: 0; }
    </style>
</c:if>

<c:if test="${not empty emergency}">
	<c:forEach items="${emergency}" var="announcement">
		<div class="<portlet:namespace/>-emerg">
			<strong><a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><img src="<c:url value="/icons/exclamation.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <c:out value="${announcement.title}"/></a></strong> <span class="portlet-section-text" style="font-size:0.9em;">(<fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/>)</span>
			<br/>
			<c:out value="${announcement.abstractText}"/>
		</div>
	</c:forEach>
</c:if>

<div<c:if test="${useScrollingDisplay}"> class="announcements-scrolling" style="height: ${scrollingDisplayHeightPixels}px;"</c:if>>

<c:choose>
    <c:when test="${empty announcements}">
        <p><span class=""><spring:message code="display.no.announcements"/></span></p>
    </c:when>
    <c:otherwise>
        <table width="100%" cellspacing="0" cellpadding="0" class="data">
          <thead>
            <tr>
                <th width="15%"><spring:message code="display.header.topic"/></th>
                <th><spring:message code="display.header.ann"/></th>
            </tr>
          </thead>
          <tbody>
        <c:forEach items="${announcements}" var="announcement" varStatus="status">
            <c:choose>
                <c:when test="${status.index mod 2 == 0}">
                    <c:set var="rowClass" value="${n}-row1color"/>
                </c:when>
                <c:otherwise>
                    <c:set var="rowClass" value="${n}-row2color"/>
                </c:otherwise>
            </c:choose>
            <tr>
                <td align="center" width="15%" class="<c:out value="${rowClass}"/>">
                    <c:out value="${announcement.parent.title}"/>
                    <c:if test="${displayPublishDate}">
                        <br/>
                        <span class="portlet-section-text" style="font-size:0.9em;"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span>
                    </c:if>
                </td>
                <td class="<c:out value="${rowClass}"/>">
                    <c:choose>
                        <c:when test="${hideAbstract}">
                            <c:set var="annLinkTitle" value="${announcement.abstractText}"/>
                            <c:set var="annLinkClass" value="announcement-link-tooltip"/>
                        </c:when>
                        <c:otherwise>
                            <c:set var="annLinkTitle"><spring:message code="display.title.fullannouncement"/></c:set>
                            <c:set var="annLinkClass" value="announcement-link"/>
                        </c:otherwise>
                    </c:choose>
                    <a title="<c:out value="${annLinkTitle}"/>" class="<c:out value="${annLinkClass}"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a>
                    <br/>
                    <c:if test="${not hideAbstract}">
                        <c:out value="${announcement.abstractText}"/><br/>
                    </c:if>
                    <c:if test="${not empty announcement.link}">
                        <span class="portlet-section-text" style="font-size:0.9em; padding-top:0.2em;"><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></span>
                    </c:if>
                </td>
            </tr>
        </c:forEach>
          </tbody>
        </table>
    </c:otherwise>
</c:choose>

</div>

<table border="0" width="100%">
  <tr>
	<td align="left" style="font-size:0.9em; padding-top:0.5em;">
		<c:if test="${not (from == 0)}">
			<a href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="from" value="${from - increment}"/><portlet:param name="to" value="${to - increment}"/></portlet:renderURL>"><spring:message code="display.link.prev"/> <c:out value="${increment}"/></a>
		</c:if>
		<c:if test="${(not (from == 0)) and hasMore}">&nbsp;&mdash;&nbsp;</c:if>
		<c:if test="${hasMore}">
			<a href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="from" value="${from + increment}"/><portlet:param name="to" value="${to + increment}"/></portlet:renderURL>"><spring:message code="display.link.next"/> <c:out value="${increment}"/></a>
		</c:if>
		<a href="<portlet:renderURL portletMode="view" ><portlet:param name="action" value="displayHistory"/></portlet:renderURL>"><spring:message code="display.link.history"/></a>
	</td>
	<td align="right" style="font-size:0.9em; padding-top:0.5em;">
		<c:if test="${not isGuest && not disableEdit}">
			<a style="text-decoration:none;" href="<portlet:renderURL portletMode="edit" windowState="normal"/>"><img src="<c:url value="/icons/pencil.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="display.link.edit"/></a>
		</c:if>
	</td>
  </tr>
</table>