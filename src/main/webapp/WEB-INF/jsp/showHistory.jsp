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

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.0.3/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<div id="<portlet:namespace/>">

    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4 class="title" role="heading"><spring:message code="showHistory.history"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL portletMode="view"><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${ topic.id }"/></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="general.backtotopic"/></a> |
                    <a href="<portlet:renderURL portletMode="view"></portlet:renderURL>"><i class="fa fa-home"></i> <spring:message code="general.adminhome"/></a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <table id="historyTable" class="table table-condensed announcements-table tablesorter">
                    <thead>
                        <tr>
                            <th width="15%"><spring:message code="showHistory.header.topic"/></th>
                            <th><spring:message code="showHistory.header.ann"/></th>
                            <th><spring:message code="showHistory.header.start"/></th>
                            <th><spring:message code="showHistory.header.end"/></th>
                            <th><spring:message code="showHistory.header.repost"/></th>
                            <th><spring:message code="showHistory.header.delete"/></th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach items="${announcements}" var="ann">
                            <tr>
                                <td><c:out value="${ann.parent.title}"/></td>
                                <td><a title="<spring:message code="showHistory.preview"/>"  href="<portlet:renderURL><portlet:param name="action" value="previewAnnouncement"/><portlet:param name="annId" value="${ ann.id }"/></portlet:renderURL>"><c:out value="${ann.title}"/></a><br /><c:out value="${ann.abstractText}"/></td>
                                <td><fmt:formatDate value="${ann.startDisplay}" dateStyle="short"/></td>
                                <td><fmt:formatDate value="${ann.endDisplay}" dateStyle="short"/></td>
                                <td><a href="<portlet:renderURL><portlet:param name="action" value="addAnnouncement"/><portlet:param name="editId" value="${ann.id}"/></portlet:renderURL>" title="<spring:message code="showHistory.viewedit"/>"><span class="pull-right"><i class="fa fa-edit"></i></span></a></td>
                                <td><a href="#" onclick="<portlet:namespace/>_delete('<portlet:actionURL escapeXml="false"><portlet:param name="action" value="deleteAnnouncement"/><portlet:param name="annId" value="${ann.id}"/><portlet:param name="topicId" value="${topic.id}"/></portlet:actionURL>');" title="<spring:message code="showHistory.delete"/>"><span class="pull-right"><i class="fa fa-trash-o"></i></span></a></td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </div>
        </div>
    </div>


<script type="text/javascript" src="http://code.jquery.com/jquery-1.10.2.min.js"></script>
<script type="text/javascript" src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
<script type="text/javascript" src="<c:url value="/js/jquery.tablesorter.js"/>"></script>
<script type="text/javascript">
var <portlet:namespace/>  = jQuery.noConflict(true);
<portlet:namespace/>(document).ready(function(){
		<portlet:namespace/>("#historyTable").tablesorter( {sortList: [[1,0], [2,0]]} );
	}
);
</script>

</div>