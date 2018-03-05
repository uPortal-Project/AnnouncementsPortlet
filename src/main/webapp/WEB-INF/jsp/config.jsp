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

<portlet:actionURL var="formUrl" escapeXml="false">
    <portlet:param name="action" value="updateConfiguration"/>
</portlet:actionURL>

<div class="fl-widget portlet" role="section">

    <!-- Portlet Body -->
    <div class="fl-widget-content portlet-body" role="main">
        <form:form action="${ formUrl }" method="POST" commandName="config" htmlEscape="false">
            <!-- General Configuration Section -->
            <div class="portlet-section" role="region">
                <h3 class="portlet-section-header" role="heading"><spring:message code="config.title"/></h3>
                <div class="portlet-section-body">
                    <form:label path="filterType"><spring:message code="config.label.filter.type"/></form:label>:
                    <br/>
                    <form:select path="filterType">
                        <form:option value="WHITELIST" label="Whitelist" />
                        <form:option value="BLACKLIST" label="Blacklist" />
                    </form:select>
                    <br/><br/>
                    <form:label path="filterContent"><spring:message code="config.label.filter.items"/></form:label>:
                    <br/>
                    <form:textarea path="filterContent" rows="5" cols="30" />
                </div>
            </div>

            <div class="buttons">
                <input type="submit" class="button primary" name="save" value="<spring:message code='config.form.btn.save'/>"/>
                <input type="submit" class="button secondary" name="cancel" value="<spring:message code='config.form.btn.cancel'/>"/>
            </div>
        </form:form>
    </div>
</div>
