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

<rs:aggregatedResources path="skin-mobile.xml"/>

<div class="portlet annplt-content">
    <div data-role="header" class="news-reader-back-bar titlebar portlet-titlebar">
        <a class="news-reader-back-link" href="<portlet:renderURL portletMode="view"/>" data-role="button" data-icon="back" data-inline="true"><spring:message code="edit.back"/></a>
        <h2 class="title"><spring:message code="edit.yoursubs"/></h2>
    </div>

    <div data-role="content" class="portlet-content">

        <form method="post" action="<c:out value="${actionUrl}"/>">

            <!--note the classes attached to the <input><span><a> tags-->
        <div data-role="fieldcontain">
            <fieldset data-role="controlgroup">
                <c:forEach items="${topicSubscriptions}" var="ts" varStatus="status">
                    <%-- Ignore emergency topics --%>
                    <c:if test="${ts.topic.subscriptionMethod != 4}">
                        <c:choose>
                            <c:when test="${not isGuest}">
                                    <c:choose>
                                        <c:when test="${ts.topic.subscriptionMethod == 1}">
                                            <input id="subscribed_${status.index}" type="checkbox" disabled="disabled" checked="checked" value="true" name="subscribed_${status.index}"/>   
                                        </c:when>
                                        <c:when test="${ts.topic.subscriptionMethod != 1 and ts.subscribed}">
                                            <input id="subscribed_${status.index}" type="checkbox" checked="checked" value="true" name="subscribed_${status.index}"/>   
                                        </c:when>
                                        <c:when test="${ts.topic.subscriptionMethod != 1 and not ts.subscribed}">
                                            <input id="subscribed_${status.index}" type="checkbox" value="true" name="subscribed_${status.index}"/> 
                                        </c:when>
                                    </c:choose>
                                    <input type="hidden" name="topicId_${status.index}" value="${ts.topic.id}"/>
                                    <input type="hidden" name="topicSubId_${status.index}" value="${ts.id}"/>
                                    <label for="subscribed_${status.index}"><c:out value="${ts.topic.title}"/>:
                                    <c:out value="${ts.topic.description}"/></label>
                            </c:when>
                            <c:otherwise>
                                <c:if test="${ts.topic.subscriptionMethod == 1}">
                                        <input type="checkbox" disabled="disabled" checked="checked" value="true" name="subscribed_${status.index}"/>   
                                        <input type="hidden" name="topicId_${status.index}" value="${ts.topic.id}"/>
                                        <input type="hidden" name="topicSubId_${status.index}" value="${ts.id}"/>
                                        <span class="topic"><c:out value="${ts.topic.title}"/></span>
                                        <span class="description"><c:out value="${ts.topic.description}"/></span>
                                </c:if>     
                            </c:otherwise>
                        </c:choose>
                    </c:if>
                </c:forEach>
                </fieldset>
            </div><!--note the class attached to the <a> tag-->

            <c:if test="${not isGuest}">
                <input type="hidden" name="topicsToUpdate" value="${topicsToUpdate}"/>
                <button class="annplt-form-button" type="submit"><spring:message code="edit.update"/></button>
            </c:if>
        
        </form>
    
    </div>
    
</div>
