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

<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<script src="<rs:resourceURL value="/rs/jquery/1.11.0/jquery-1.11.0.min.js"/>" type="text/javascript"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery-migrate/jquery-migrate-1.2.1.min.js"/>"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.10.3/jquery-ui-1.10.3.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/underscore-min.js"/>" type="text/javascript"></script>

<script type="text/template" id="${n}template-attachment-display-item">
    <div class="row">
        <div class="col-md-12">
            <div id="${n}attachment_display_${"<%="} attachment.id ${"%>"}">
                <i class="fa fa-download"></i> <a href='${"<%="} attachment.path ${"%>"}'><span>${"<%="} attachment.filename ${"%>"}</span></a>
            </div>
        </div>
    </div>
</script>

<script type="text/javascript">
    var ${n} = ${n} || {}; //create a unique variable to assign our namespace too
    ${n}.jQuery = jQuery.noConflict(true); //assign jQuery to this namespace
    ${n}._ = _.noConflict();

    ${n}.jQuery(function () {
        var $ = ${n}.jQuery; //reassign $ for normal use of jQuery
        var _ = ${n}._;
        var template = $('#${n}template-attachment-display-item').html();
        _.templateSettings.variable = "attachment";

        <c:forEach items="${announcement.attachments}" var="attachment" varStatus="status">
            var ${n}attachment = ${attachment};
            var compiled = _.template(template, ${attachment});
            $("#${n}attachment-list").append(compiled);
        </c:forEach>
    });
</script>

    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="nav-links">
                <a href="<portlet:renderURL />"><i class="fa fa-arrow-left" aria-hidden="true"></i> <spring:message code="displayFull.back"/></a>
            </div>
        </div>
        <div class="ann-display-item-full">
            <div class="row">
                <div class="col-xs-12">
                    <h2><c:out value="${announcement.title}"/></h2>
                    <c:if test="${displayPublishDate}">
                        <p><spring:message code="displayFull.displayBegin"/> <fmt:formatDate value="${announcement.startDisplay}" dateStyle="long"/></p>
                    </c:if>
                    <p>
                        <spring:message code="displayFull.displayEnd"/>
                        <c:choose>
                            <c:when test="${announcement.endDisplay == null}">
                                <spring:message code="displayFull.displayEnd.unspecified"/>
                            </c:when>
                            <c:otherwise>
                                <fmt:formatDate value="${announcement.endDisplay}" dateStyle="long"/>
                            </c:otherwise>
                        </c:choose>
                    </p>
                    <c:if test="${not empty announcement.link}">
                        <p><spring:message code="display.link.prefix"/> <a href="${announcement.link}"><c:out value="${announcement.link}"/></a></p>
                    </c:if>
                    <p><c:out value="${announcement.message}" escapeXml="false"/></p>
                </div>
            </div>
        </div>
        <c:if test="${not empty announcement.attachments}">
            <div class="row">
                <div class="col-md-12">
                    <h4><spring:message code="displayFull.attachments"/></h4>
                </div>
            </div>
            <div class="row">
                <div class="col-md-12">
                    <div id="${n}attachment-list"></div>
                </div>
            </div>
        </c:if>
    </div>
