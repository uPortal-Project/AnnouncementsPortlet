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

<c:set var="n"><portlet:namespace/></c:set>
<portlet:actionURL var="actionUrl" escapeXml="false">
	<portlet:param name="action" value="addTopic"/>
</portlet:actionURL>

<rs:aggregatedResources path="skin.xml"/>
<link href="<c:url value="/css/baseAdmin.css"/>" rel="stylesheet" type="text/css" />
<script src="<rs:resourceURL value="/rs/jquery/1.6.4/jquery-1.6.4.min.js"/>" type="text/javascript"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.8.13/jquery-ui-1.8.13.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/rs/jquery-tooltip/1.3/jquery.tooltip.js"/>" type="text/javascript"></script>
<script type="text/javascript">
    var ${n} = ${n} || {}; //create a unique variable to assign our namespace too
    ${n}.jQuery = jQuery.noConflict(true); //assign jQuery to this namespace

    /*  runs when the document is finished loading.  This prevents things like the 'div' from being fully created */
    ${n}.jQuery(function () {
        var $ = ${n}.jQuery; //reassign $ for normal use of jQuery

        $(".announcements-portlet-sub-methods label").tooltip({
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
<div class="announcements-portlet-toolbar">
    <div class="announcements-portlet-secondary">
	    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>"><img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>
	</div>
</div>
<h2 class="title" role="heading"><spring:message code="addTopic.heading"/></h2>
<form:form commandName="topic" method="post" action="${actionUrl}">
	<div class="announcements-portlet-row">
		<label for="title"><spring:message code="addTopic.title"/></label>
        <div class="announcements-portlet-col">
            <form:errors cssClass="portlet-msg-error" path="title"/>
    		<form:input cssClass="portlet-form-input-field" path="title" size="30" maxlength="80" /> 
        </div>
	</div>
	<div class="announcements-portlet-row">
		<label for="description"><spring:message code="addTopic.description"/></label>
        <div class="announcements-portlet-col"> 
		    <form:input cssClass="portlet-form-input-field" path="description" size="30" maxlength="80" />
        </div> 
	</div>
	<div class="announcements-portlet-row">
		<label for="allowRss1"><spring:message code="addTopic.publicrss"/></label>
        <div class="announcements-portlet-col">
		    <form:checkbox path="allowRss" cssClass="portlet-form-input-field"/>
        </div>
	</div>
	<div class="announcements-portlet-row">
		<label for="subscriptionMethod"><spring:message code="addTopic.submethod"/></label>
        <div class="announcements-portlet-col">
    		<form:errors cssClass="portlet-msg-error" path="subscriptionMethod"/>
    		<ul class="announcements-portlet-sub-methods">
    			<li><form:radiobutton path="subscriptionMethod" value="1"/> <label for="subscriptionMethod1" title="<spring:message code="addTopic.pushedforced.title"/>"><spring:message code="addTopic.pushedforced"/></label></li>
    			<li><form:radiobutton path="subscriptionMethod" value="2"/> <label for="subscriptionMethod2" title="<spring:message code="addTopic.pushedoptional.title"/>"><spring:message code="addTopic.pushedoptional"/></label></li>
    			<li><form:radiobutton path="subscriptionMethod" value="3"/> <label for="subscriptionMethod3" title="<spring:message code="addTopic.optional.title"/>"><spring:message code="addTopic.optional"/></label></li>
    		</ul>
        </div>
	</div>
	<form:hidden path="id"/>
	<form:hidden path="creator"/>
	<br/>
	<button type="submit" class="portlet-form-button"><spring:message code="addTopic.saveButton"/></button>
</form:form>

