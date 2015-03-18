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
<%--
	- errors
	- 
	- Output a list of errors for the command or bean specified in the
	- 'path' attribute. The markup enclosing each error message can be
	- customized by editing this tag file directly.
	-
	- @param path the name of the field to bind to (required)
	- @param fields whether the individual fields should also be checked,
	-     specifically '${path}.*' (optional)
	--%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" body-content="empty" %>
<%@ include file="include.jsp" %>
<%@ attribute name="path" required="true" %>
<%@ attribute name="fields" required="false"%>
<spring:hasBindErrors name="${path}">
	<div style="color:#A00000">
		<p>Please correct the following errors:</p>
		<ul class="errors">
			<spring:bind path="${path}">
				<c:forEach items="${status.errorMessages}" var="error">
					<li><c:out value="${error}"/></li>
				</c:forEach>
			</spring:bind>
			<c:if test="${fields}">
				<spring:bind path="${path}.*">
					<c:forEach items="${status.errorMessages}" var="error">
						<li><c:out value="${error}"/></li>
					</c:forEach>
				</spring:bind>
			</c:if>
		<ul>
	</div>
</spring:hasBindErrors>