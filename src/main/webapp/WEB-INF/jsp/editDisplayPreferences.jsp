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

<portlet:actionURL var="actionUrl" escapeXml="false">
	<portlet:param name="action" value="editDisplayPreferences"/>
</portlet:actionURL>

<form action="${actionUrl}" method="post">
    <div class="portlet-section-header"><spring:message code="edit.yoursubs"/></div>
	<table width="100%">
		<c:forEach items="${topicSubscriptions}" var="ts" varStatus="status">
			<%-- Ignore emergency topics --%>
			<c:if test="${ts.topic.subscriptionMethod != 4}">
				<c:choose>
					<c:when test="${not isGuest}">
						<tr>
							<td width="25">
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
									<a href="<c:url value="/getRssFeed?topic=${ts.topic.id}"/>" title="<spring:message code="edit.rss"/>"><img alt="<spring:message code="edit.rss"/>" src="<c:url value="/icons/feed.png"/>" height="16" width="16" border="0"/></a>
								</c:if>
							</td>
						</tr>		
					</c:when>
					<c:otherwise>
						<c:if test="${ts.topic.subscriptionMethod == 1}">
							<tr>
								<td width="25">
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
										<a href="<c:url value="/getRssFeed?topic=${ts.topic.id}"/>" title="<spring:message code="edit.rss"/>"><img alt="<spring:message code="edit.rss"/>" src="<c:url value="/icons/feed.png"/>" height="16" width="16" border="0"/></a>
									</c:if>
								</td>
							</tr>		
						</c:if>		
					</c:otherwise>
				</c:choose>
			</c:if>
		</c:forEach>
	</table>
    <br/>

    <div class="portlet-section-header"><spring:message code="edit.yourprefs"/></div>
    <table width="100%">
        <tr>
            <td width="25">
                <c:choose>
                    <c:when test="${not prefHideAbstract}">
                        <input type="checkbox" value="true" name="hideAbstract"/>
                    </c:when>
                    <c:otherwise>
                        <input type="checkbox" value="true" name="hideAbstract" checked="checked"/>
                    </c:otherwise>
                </c:choose>
            </td>
            <td><spring:message code="edit.pref.hideabstract"/></td>
    </table>

    <c:if test="${not isGuest}">
        <input type="hidden" name="topicsToUpdate" value="${topicsToUpdate}"/>
        <button type="submit" class="portlet-form-button"><spring:message code="edit.update"/></button>
        &nbsp;&nbsp;
        <a href="<portlet:renderURL portletMode="view" windowState="normal"/>"><spring:message code="general.cancelandreturn"/></a>
    </c:if>

    <c:if test="${isGuest}">
	    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view"></portlet:renderURL>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="edit.back"/></a>
    </c:if>
</form>
