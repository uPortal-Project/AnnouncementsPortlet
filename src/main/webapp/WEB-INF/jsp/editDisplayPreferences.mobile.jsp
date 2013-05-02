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

<link type="text/css" rel="stylesheet" href="<c:url value="/css/mobile.css"/>"/>

<div class="content annplt-content">

    <form method="post" action="<c:out value="${actionUrl}"/>">

        <div class="annplt-announcement">
    
            <!--note the classes attached to the <input><span><a> tags-->
            <ul class="annplt-subscriptions">
                <c:forEach items="${topicSubscriptions}" var="ts" varStatus="status">
                    <%-- Ignore emergency topics --%>
                    <c:if test="${ts.topic.subscriptionMethod != 4}">
                        <c:choose>
                            <c:when test="${not isGuest}">
                            	<li>
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
	                                <span class="topic"><c:out value="${ts.topic.title}"/></span>
	                                <span class="description"><c:out value="${ts.topic.description}"/></span>
	                                <c:if test="${ts.topic.allowRss}">
	                                    <a class="rss" href="<c:url value="/getRssFeed?topic=${ts.topic.id}"/>" title="<spring:message code="edit.rss"/>"><img alt="<spring:message code="edit.rss"/>" src="<c:url value="/icons/feed.png"/>" height="16" width="16" border="0"/></a>
	                                </c:if>
                            	</li>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${ts.topic.subscriptionMethod == 1}">
                                	<li>
	                                    <input type="checkbox" disabled="disabled" checked="checked" value="true" name="subscribed_${status.index}"/>   
	                                    <input type="hidden" name="topicId_${status.index}" value="${ts.topic.id}"/>
	                                    <input type="hidden" name="topicSubId_${status.index}" value="${ts.id}"/>
	                                    <span class="topic"><c:out value="${ts.topic.title}"/></span>
	                                    <span class="description"><c:out value="${ts.topic.description}"/></span>
	                                    <c:if test="${ts.topic.allowRss}">
	                                        <a class="rss" href="<c:url value="/getRssFeed?topic=${ts.topic.id}"/>" title="<spring:message code="edit.rss"/>"><img alt="<spring:message code="edit.rss"/>" src="<c:url value="/icons/feed.png"/>" height="16" width="16" border="0"/></a>
	                                    </c:if>
                                    </li>
                                </c:if>     
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:forEach>
            </ul><!--note the class attached to the <a> tag-->
        
	        <div class="options">
	            <c:if test="${not isGuest}">
	                <input type="hidden" name="topicsToUpdate" value="${topicsToUpdate}"/>
	                <button class="annplt-form-button" type="submit">Update Subscriptions</button>
	                <a class="annplt-form-button" href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.cancelandreturn"/></a>
	            </c:if>
	            <c:if test="${isGuest}">
	                <a class="annplt-form-button" href="<portlet:renderURL portletMode="view"/>"><img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="edit.back"/></a>
	            </c:if>
	        </div>

        </div>

    </form>
    
</div>
