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

<c:set var="n"><portlet:namespace/></c:set>

<c:if test="${hideAbstract}">
    <script src="http://code.jquery.com/jquery-1.10.2.min.js" type="text/javascript"></script>
    <script type="text/javascript" src="http://code.jquery.com/jquery-migrate-1.2.1.min.js"></script>
    <script src="http://code.jquery.com/ui/1.10.3/jquery-ui.min.js" type="text/javascript"></script>
    <script src="<c:url value="/rs/jquery-tooltip/1.3/jquery.tooltip.js"/>" type="text/javascript"></script>
    <script type="text/javascript">
        var ${n} = ${n} || {}; //create a unique variable to assign our namespace too
        ${n}.jQuery = jQuery.noConflict(true); //assign jQuery to this namespace

        /*  runs when the document is finished loading.  This prevents things like the 'div' from being fully created */
        ${n}.jQuery(function () {
            var $ = ${n}.jQuery; //reassign $ for normal use of jQuery

            $(".announcement-link-tooltip").tooltip({
                showURL: false,
                position: { offset: "15 15" }
            });
        });
    </script>

    <style>
        #tooltip {
            padding:8px;
            opacity: 0.85;
            position:absolute;
            z-index:9999;
            -o-box-shadow: 0 0 5px #aaa;
            -moz-box-shadow: 0 0 5px #aaa;
            -webkit-box-shadow: 0 0 5px #aaa;
            box-shadow: 0 0 5px #aaa;
            max-width: 400px;
            background-color: #ffffff;
            background-image: none;
            border: 1px solid #111;
            border-width:2px;
            font-size: 11px;
            font-family: inherit;
        }
        #tooltip h3, #tooltip div { margin: 0; }
    </style>
</c:if>

    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-12 no-col-padding">
                <div class="nav-links">
                    <c:if test="${not isGuest && not disableEdit}">
                        <a href="<portlet:renderURL portletMode="edit" windowState="normal"/>"><i class="fa fa-edit"></i> <spring:message code="display.link.edit"/></a>
                    </c:if> |
                    <a href="<portlet:renderURL portletMode="view" ><portlet:param name="action" value="displayHistory"/></portlet:renderURL>"><i class="fa fa-archive"></i> <spring:message code="display.link.history"/></a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <c:if test="${not empty emergency}">
                    <c:forEach items="${emergency}" var="announcement">
                        <div class="alert alert-danger" role="alert">
                            <strong><a class="alert-link" title="<spring:message code="display.title.fullannouncement"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><i class="fa fa-exclamation-triangle"></i> <c:out value="${announcement.title}"/></a></strong>
                            <span class="pull-right"><small><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></small></span>
                        <p><c:out value="${announcement.abstractText}"/></p>
                        </div>
                    </c:forEach>
                </c:if>
            </div>
        </div>
        <div class="row announcements-summary-row">
            <div class="col-lg-12">
                <c:choose>
                    <c:when test="${useScrollingDisplay}">
                        <div class="announcements-scrolling">
                    </c:when>
                    <c:otherwise>
                        <div></div>
                    </c:otherwise>
                </c:choose>
                <c:choose>
                    <c:when test="${empty announcements}">
                        <div class="alert alert-warning"><spring:message code="display.no.announcements"/></div>
                    </c:when>
                    <c:otherwise>
                        <c:forEach items="${announcements}" var="announcement" varStatus="status">
                            <div class="ann-display-item-summary">
                                <div class="row">
                                    <div class="col-xs-12">
                                        <div class="ann-item-header">
                                            <div class="ann-item-topic">
                                                <i class="fa fa-list-alt"></i> <c:out value="${announcement.parent.title}"/>
                                            </div>
                                            <c:if test="${displayPublishDate}">
                                                <div class="ann-item-date"><fmt:formatDate value="${announcement.startDisplay}" dateStyle="medium"/></div>
                                            </c:if>
                                        </div>
                                    </div>
                                </div>
                                <div class="row">
                                    <div class="col-md-12">
                                        <h1><a title="<c:out value="${annLinkTitle}"/>" href="<portlet:renderURL><portlet:param name="action" value="displayFullAnnouncement"/><portlet:param name="announcementId" value="${announcement.id}"/></portlet:renderURL>"><c:out value="${announcement.title}"/></a></h1>
                                        <c:choose>
                                            <c:when test="${hideAbstract}">
                                                <c:set var="annLinkTitle" value="${announcement.abstractText}"/>
                                                <c:set var="annLinkClass" value="announcement-link-tooltip"/>
                                            </c:when>
                                            <c:otherwise>
                                                <c:set var="annLinkTitle"><spring:message code="display.title.fullannouncement"/></c:set>
                                                <c:set var="annLinkClass" value="announcement-link"/>
                                            </c:otherwise>
                                        </c:choose>
                                        <c:if test="${not hideAbstract}">
                                            <p><c:out value="${announcement.abstractText}"/></p>
                                        </c:if>
                                        <c:if test="${not empty announcement.link}">
                                            <p ><spring:message code="display.link.prefix"/> <a href="<c:out value="${announcement.link}"/>"><c:out value="${announcement.link}"/></a></p>
                                        </c:if>
                                    </div>
                                </div>
                            </div>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </div>
        </div>
        <div class="row announcement-list-nav">
            <div class="col-md-12">
                <c:if test="${not (from == 0)}">
                    <a class="btn btn-default" href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="from" value="${from - increment}"/><portlet:param name="to" value="${to - increment}"/></portlet:renderURL>"><i class="fa fa-hand-o-left"></i> <spring:message code="display.link.prev"/> <c:out value="${increment}"/></a>
                </c:if>
                <c:if test="${(not (from == 0)) and hasMore}">&nbsp;&mdash;&nbsp;</c:if>
                <c:if test="${hasMore}">
                    <span class="pull-right"><a class="btn btn-default" href="<portlet:renderURL portletMode="view" windowState="normal"><portlet:param name="from" value="${from + increment}"/><portlet:param name="to" value="${to + increment}"/></portlet:renderURL>"><spring:message code="display.link.next"/> <c:out value="${increment}"/> <i class="fa fa-hand-o-right"></i></a></span>
                </c:if>
            </div>
        </div>
    </div>

    <script type="text/javascript">
        // For announcement display, the following code watches the div size of the
        // announcements container and readjust the size of the announcements div
        // to match the width of uPortal customize drawer layout width
        var watchingDiv = $(".announcements-container");
        var changingDiv = $(".announcements-summary-row > div");
        var classToRemove = "col-lg-6";
        var classToAdd = "col-lg-12";

        // Watch a div and return the width of it
        function watchDivSize (div) {
            var divSize = div.width();

            return divSize;
        }

        // If the width is less than 970 pixels, swap out Bootstrap classes
        function adjustAnnDisplay(divWidth) {
            if(divWidth < 970) {
                changingDiv.removeClass(classToRemove);
                changingDiv.addClass(classToAdd);
            }
        }

        // Make the initial watch and adjustment
        adjustAnnDisplay(watchDivSize(watchingDiv));

        $(document).ready(function() {
            $(window).resize(function() {
                //adjustAnnDisplay(watchDivSize(watchingDiv));
            });
        });
    </script>
