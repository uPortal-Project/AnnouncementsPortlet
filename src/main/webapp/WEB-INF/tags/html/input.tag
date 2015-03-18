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
	- input
	- 
	- Display an input field (default type="text") and bind it to the attribute
	- of a command or bean. If name and/or value attributes are specified,
	- they will be used instead of status.expression and/or status.value
	- respectively. A type attribute may also be used to override the
	- input tag type (the default is text).
	- Accepts dynamic attributes.
	-
	- @param path the name of the field to bind to (required)
	- @param type use this attribute to override the input type (i.e. hidden).
	- @param name use this attribute to override the input name
	- @param value use this attribute to override the input value
	--%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" body-content="empty" %>
<%@ include file="include.jsp" %>
<%@ attribute name="path" required="true" %>
<spring:bind path="${path}">
	<html:attributes var="attrString" attributeMap="${attributes}" type="text" name="${status.expression}" value="${status.value}">
		<input ${attrString} />
	</html:attributes>
	<span style="color:#A00000">${status.errorMessage}</span>
</spring:bind>