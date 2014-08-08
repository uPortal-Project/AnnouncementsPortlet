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

<rs:aggregatedResources path="skin.xml"/>

<style type="text/css">
.<portlet:namespace/>-row1color { padding: 5px; background-color: #eee; }
.<portlet:namespace/>-row2color { padding: 5px; background-color: #fff; }
.<portlet:namespace/>-emerg { padding: 5px; margin-bottom:5px; color:#c00; background-color: #fff; border: 3px solid #cc3300; }
.<portlet:namespace/>-emerg a, .<portlet:namespace/>-emerg a:visited { text-decoration: none; color: #c00; }
</style>

<table width="100%" cellspacing="0" cellpadding="0" class="data">
	<tr>
		<th width="15%"><spring:message code="display.header.topic"/></th>
		<th><spring:message code="display.header.ann"/></th>
		<th><spring:message code="display.header.start"/></th>
		<th><spring:message code="display.header.end"/></th>
	</tr>

	<c:forEach items="${announcements}" var="announcement" varStatus="status">
		<tr>
			<c:choose>
				<c:when test="${status.index mod 2 == 0}">
				    <c:set var="rowClass" value="${n}-row1color"/>
				</c:when>
                <c:otherwise>
                    <c:set var="rowClass" value="${n}-row2color"/>
                </c:otherwise>
            </c:choose>

            <td align="center" width="15%" class="<c:out value="${rowClass}"/>">
                <c:out value="${announcement.parent.title}"/>
            </td>
            <td align="center" width="15%" class="<c:out value="${rowClass}"/>">
                <a title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncementHistory"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a>
				<br /><c:out value="${announcement.abstractText}"/>
				<br />
				<c:if test="${not empty announcement.link}">
					<span class="portlet-section-text" style="font-size:0.9em; padding-top:0.2em;"><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></span>
				</c:if>
			</td>
			<td align="center" width="15%" class="<c:out value="${rowClass}"/>">
				<span class="portlet-section-text" style="font-size:0.9em;"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></span>
			</td>
			<td align="center" width="15%" class="<c:out value="${rowClass}"/>">
				<span class="portlet-section-text" style="font-size:0.9em;"><fmt:formatDate value="${announcement.endDisplay}" dateStyle="medium"/></span>
			</td>
		</tr>

	</c:forEach>
</table>

<p align="right" style="font-size:0.9em; padding-top:0.5em;">
<a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="display.back"/></a>
</p>

