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
	- attributes
	-
	- Expose a string of HTML attributes from the given map of attributes.
	- Dynamic attributes specified will be added to the string if they do
	- not already exist in the map. This tag exposes a variable with the
	- name specified in the 'var' attribute.
	-
	- @param var the variable in which the string attributes will be exposed.
	-     (required)
	- @param attributeMap a map of attributes to convert to a string of
	-     name/value pairs.
	--%>
<%@ tag dynamic-attributes="attributes" isELIgnored="false" %>
<%@ include file="include.jsp" %>
<%@ attribute name="var" required="true" rtexprvalue="false" %>
<%@ attribute name="attributeMap" type="java.util.Map" %>
<%@ variable name-from-attribute="var" alias="attrString" declare="false" %>
<c:forEach var="attr" items="${attributeMap}">
	<c:set var="attrString">
	    <c:out escapeXml="false" value="${attrString} ${attr.key}=\""/><c:out value="${attr.value}"/><c:out escapeXml="false" value="\""/>
	</c:set>
</c:forEach>
<c:forEach var="attr" items="${attributes}">
	<c:if test="${empty attributeMap[attr.key]}">
		<c:set var="attrString">
	    	<c:out escapeXml="false" value="${attrString} ${attr.key}=\""/><c:out value="${attr.value}"/><c:out escapeXml="false" value="\""/>
		</c:set>
	</c:if>
</c:forEach>
<jsp:doBody />