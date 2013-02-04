<%@ include file="/WEB-INF/jsp/include.jsp" %>
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
<c:set var="n"><portlet:namespace/></c:set>
<link href="<c:url value="/css/baseAdmin.css"/>" rel="stylesheet" type="text/css" />
<script src="<rs:resourceURL value="/rs/jquery/1.6.4/jquery-1.6.4.min.js"/>" type="text/javascript"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.8.13/jquery-ui-1.8.13.min.js"/>" type="text/javascript"></script>
<script type="text/javascript" src="<c:url value="/tinymce/tiny_mce.js"/>"></script>

<script type="text/javascript">
    var ${n} = ${n} || {}; //create a unique variable to assign our namespace too
    ${n}.jQuery = jQuery.noConflict(true); //assign jQuery to this namespace

    /*  runs when the document is finished loading.  This prevents things like the 'div' from being fully created */
    ${n}.jQuery(function () {
        var $ = ${n}.jQuery; //reassign $ for normal use of jQuery

    	$("#${n}datepickerstart").datepicker({dateFormat: 'yy-mm-dd'});
    	$("#${n}datepickerend").datepicker({dateFormat: 'yy-mm-dd'});

        $("#${n}abstractText").bind('keyup input paste change',function(e){
            //get the limit from maxlength attribute
            var limit = parseInt("<c:out value="${abstractMaxLength}"/>");
            //get the current text inside the textarea
            var text = $(this).val();
            //count the number of characters in the text
            var chars = text.length;
            var charsLeft = limit - text.length;

            //check if there are more characters then allowed
            if(chars > limit){
                //and if there are use substr to get the text before the limit
                var new_text = text.substr(0, limit);
                charsLeft = 0;
                //and change the current text with the new text
                $(this).val(new_text);
            }

            $("#${n}abstractTextRemaining").html(charsLeft + " characters remaining.");

        });
    });

</script>


<portlet:actionURL var="actionUrl">
	<portlet:param name="action" value="addAnnouncement"/>
	<portlet:param name="topicId" value="${announcement.parent.id}"/>
</portlet:actionURL>
<div class="announcements-portlet-toolbar">
	<a style="text-decoration:none;" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${announcement.parent.id}"/></portlet:renderURL>">
		<img src="<c:url value="/icons/arrow_left.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.backtotopic"/></a>
	<div class="announcements-portlet-secondary">
		

		<a style="text-decoration:none;" href="<portlet:renderURL portletMode="view" windowState="normal"></portlet:renderURL>">
		<img src="<c:url value="/icons/house.png"/>" border="0" height="16" width="16" style="vertical-align:middle"/> <spring:message code="general.adminhome"/></a>
	</div>
</div>
<div class="portlet-section-header"><h2 class="title=" role="heading"><spring:message code="addAnnouncement.header"/> <c:out value="${announcement.parent.title}"/></h2></div>

<form:form commandName="announcement" method="post" action="${actionUrl}">
	<div class="announcements-portlet-row">
		<label for="title"><spring:message code="addAnnouncement.title"/></label>
		<form:errors cssClass="portlet-msg-error" path="title"/>
 		<form:input cssClass="portlet-form-input-field" path="title" size="30" maxlength="80"/>
	</div>


	<div class="announcements-portlet-row">
		<label for="${n}abstractText"><spring:message code="addAnnouncement.abstract"/></label>
		<form:errors cssClass="portlet-msg-error" path="abstractText"/>
		<form:textarea cssClass="portlet-form-input-field" path="abstractText" id="${n}abstractText" rows="2" cols="40" />
        <div style="margin-left: 30%;" id="${n}abstractTextRemaining"><c:out value="${abstractMaxLength}"/> characters remaining</div>
	</div>


	<div class="announcements-portlet-row">
		<label><spring:message code="addAnnouncement.message"/></label>
		<form:errors cssClass="portlet-msg-error" path="message"/>
		<form:textarea cssClass="portlet-form-input-field mceEditor" path="message" rows="5" cols="30" cssStyle="width: 70%;" />
	</div>


	<div class="announcements-portlet-row">
		<label><spring:message code="addAnnouncement.link"/></label>
		<form:errors cssClass="portlet-msg-error" path="link"/>
		<form:input cssClass="portlet-form-input-field" path="link" size="30" maxlength="255"/>
	</div>


	<div class="announcements-portlet-row">
		<label><spring:message code="addAnnouncement.start"/></label>

		<form:errors cssClass="portlet-msg-error" path="startDisplay"/>
		<form:input path="startDisplay" id="${n}datepickerstart"></form:input>
	</div>


	<div class="announcements-portlet-row">
		<label><spring:message code="addAnnouncement.end"/></label>
		<form:errors cssClass="portlet-msg-error" path="endDisplay"/>
		<form:input path="endDisplay" id="${n}datepickerend"></form:input>
	</div>

<form:hidden path="id"/>
<form:hidden path="created"/>
<form:hidden path="author"/>
<form:hidden path="parent"/>
<button type="submit" class="portlet-form-button"><spring:message code="addAnnouncement.save"/></button>
</form:form>

<script type="text/javascript">
<!--
tinyMCE.init({
	mode : "textareas",
	editor_selector : "mceEditor",
	theme : "advanced",
	theme_advanced_buttons1 : "bold,separator,bullist,numlist,undo,redo,link,unlink",
	theme_advanced_buttons2 : "",
	theme_advanced_buttons3 : "",
	theme_advanced_toolbar_location : "top",
	theme_advanced_toolbar_align : "left",
	extended_valid_elements : "a[name|href|target|title|onclick],span[class|align|style]"
});
//-->
</script>