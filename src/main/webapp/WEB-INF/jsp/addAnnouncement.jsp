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
<script src="<rs:resourceURL value="/rs/jquery/1.10.2/jquery-1.10.2.min.js"/>" type="text/javascript"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery-migrate/jquery-migrate-1.2.1.min.js"/>"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.10.3/jquery-ui-1.10.3.min.js"/>" type="text/javascript"></script>
<script src="<c:url value="/js/underscore-min.js"/>" type="text/javascript"></script>

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.7.0/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<script type="text/javascript" src="<c:url value="/tinymce/js/tinymce/tinymce.min.js"/>"></script>
<script type="text/javascript">
    var ${n} = ${n} || {}; // create a unique variable for our JS namespace
    ${n}.jQuery = jQuery.noConflict(true); // assign jQuery to this namespace
    ${n}._ = _.noConflict(); // assign underscore to this namespace

    /*  runs when the document is finished loading.  This prevents things like the 'div' from being fully created */
    ${n}.jQuery(function () {
        var $ = ${n}.jQuery; //reassign $ for normal use of jQuery
        var _ = ${n}._;

        $("#${n}datepickerStart").datepicker({dateFormat: 'yy-mm-dd'});
        $("#${n}datepickerEnd").datepicker({dateFormat: 'yy-mm-dd'});

        $("#${n}datepickerStartIcon").click(function() {
            $("#${n}datepickerStart").datepicker("show");
        });
        $("#${n}datepickerEndIcon").click(function() {
            $("#${n}datepickerEnd").datepicker("show");
        });

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

            $("#${n}abstractTextRemaining").html(charsLeft + ' <spring:message code="addAnnouncement.charactersremaining"/>');

        });

        var tinyMceFileBrowserCallback = null;  // default

        // Display and use the attachments feature only if it's present
        if(typeof upAttachments != "undefined") {
            ${n}.addAttachmentCallback = function(result) {
                ${n}.addAttachment(result);
                upAttachments.hide();
            };
            ${n}.addAttachment = function(result) {
                _.templateSettings.variable = "attachment";
                var template = $('#${n}template-attachment-add-item').html();
                var compiled = _.template(template, result);
                $("#${n}attachments").append(compiled);
                var addedElement = $("#${n}attachments").find('.attachment-item:last');
                addedElement.find('.remove-button').click(function() {
                    addedElement.remove();
                });
            };
            <c:forEach items="${announcement.attachments}" var="attachment">
            ${n}.addAttachment(${attachment});
            </c:forEach>
            $("#${n}attachment_add_section").show();
            // TinyMCE WYSIWYG callback
            tinyMceFileBrowserCallback = function(field_name, url, type, win) {
                setTimeout(function() { window.self.focus(); }, 1);
                upAttachments.show(function(result) {
                    win.document.getElementById(field_name).value = result.path;
                    upAttachments.hide();
                    setTimeout(function() { win.focus(); }, 1);
                });
            };
        }

        var tinyMceOptions = { <c:out value="${tinyMceInitializationOptions}" escapeXml="false"/> };
        if (tinyMceFileBrowserCallback) {
            tinyMceOptions.file_browser_callback = tinyMceFileBrowserCallback;
        }
        tinyMCE.init(tinyMceOptions);
    });

</script>

<script type="text/template" id="${n}template-attachment-add-item">
    <div id="${n}attachment_add_${"<%="} attachment.id ${"%>"}" class="attachment-item">
        <a class="remove-button" href="javascript:void(0);"> <i class="fa fa-trash-o"></i></a>
        <span>${"<%="} attachment.filename ${"%>"}</span>
        <input type="hidden" name="attachments" value='${"<%="} JSON.stringify(attachment) ${"%>"}'/>
    </div>
</script>

<portlet:actionURL var="actionUrl" escapeXml="false">
    <portlet:param name="action" value="addAnnouncement"/>
    <portlet:param name="topicId" value="${announcement.parent.id}"/>
</portlet:actionURL>

    <div class="container-fluid bootstrap-styles announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4><spring:message code="addAnnouncement.header"/> <c:out value="${announcement.parent.title}"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${announcement.parent.id}"/></portlet:renderURL>"><i class="fa fa-arrow-left"></i> <spring:message code="general.backtotopic"/></a> |
                    <a href="<portlet:renderURL />"><i class="fa fa-home"></i> <spring:message code="general.adminhome"/></a>
                </div>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <form:form commandName="announcement" method="post" action="${actionUrl}" class="form-horizontal" role="form">
                    <div class="form-group">
                        <label for="title" class="col-sm-3 control-label">
                            <spring:message code="addAnnouncement.title"/>
                        </label>
                        <div class="col-sm-9">
                            <form:input cssClass="form-control" path="title"/>
                            <form:errors cssClass="announcements-error label label-danger" path="title"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label for="${n}abstractText" class="col-sm-3 control-label">
                            <spring:message code="addAnnouncement.abstract"/>
                        </label>
                        <div class="col-sm-9">
                            <form:textarea cssClass="form-control" path="abstractText" id="${n}abstractText"/>
                            <form:errors cssClass="announcements-error label label-danger" path="abstractText"/>
                            <div id="${n}abstractTextRemaining"><c:out value="${abstractMaxLength}"/> <spring:message code="addAnnouncement.charactersremaining"/></div>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label">
                            <spring:message code="addAnnouncement.message"/>
                        </label>
                        <div class="col-sm-9">
                            <%-- Removed textarea and replaced with a div that TinyMCE 4.x uses with inline editing.
                                 Must set id on div because TinyMCE will use that as the added hidden form field name.
                            <form:textarea cssClass="form-control mceEditor editable" path="message"/>--%>
                            <div class="mceEditor" id="message"><c:out value="${announcement.message}" escapeXml="false"/></div>
                            <form:errors cssClass="announcements-error label label-danger" path="message"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label" for="link">
                            <spring:message code="addAnnouncement.link"/>
                        </label>
                        <div class="col-sm-9">
                            <form:errors cssClass="announcements-error label label-danger" path="link"/>
                            <form:input cssClass="form-control" path="link"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label" for="${n}datepickerStart">
                            <spring:message code="addAnnouncement.start"/>
                        </label>
                        <div class="col-sm-9">
                            <div class="input-group">
                                <form:input path="startDisplay" cssClass="form-control" id="${n}datepickerStart"/>
                                <span id="${n}datepickerStartIcon" class="input-group-addon"><i class="fa fa-calendar"></i></span>
                            </div>
                            <form:errors cssClass="announcements-error label label-danger" path="startDisplay"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label" for="${n}datepickerEnd">
                            <spring:message code="addAnnouncement.end"/>
                        </label>
                        <div class="col-sm-9">
                            <div class="input-group">
                                <form:input path="endDisplay" cssClass="form-control" id="${n}datepickerEnd"/>
                                <span id="${n}datepickerEndIcon" class="input-group-addon"><i class="fa fa-calendar"></i></span>
                            </div>
                            <form:errors cssClass="announcements-error label label-danger" path="endDisplay"/>
                        </div>
                    </div>
                    <c:if test="${useAttachments}">
                        <div class="form-group" id="${n}attachment_add_section" style="display:none;">
                            <label class="col-sm-3 control-label"><spring:message code="addAnnouncement.attachments"/>:</label>
                            <div class="col-sm-9">
                                <a class="btn btn-default btn-sm" href="javascript:upAttachments.show(${n}.addAttachmentCallback);"><i class="fa fa-folder-open-o"></i> Browse...</a>
                                <div class="row">
                                    <div class="col-sm-12">
                                        <div class="attachments-container" id="${n}attachments"></div>
                                    </div>
                                </div>
                            </div>
                        </div>
                    </c:if>
                    <div class="form-group">
                        <div class="col-sm-9 col-sm-offset-3">
                            <button type="submit" class="btn btn-primary"><spring:message code="addAnnouncement.save"/></button>
                            <a class="btn btn-link" href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${announcement.parent.id}"/></portlet:renderURL>"><spring:message code="addAnnouncement.cancel"/></a>
                        </div>
                    </div>
                    <form:hidden path="id"/>
                    <form:hidden path="created"/>
                    <form:hidden path="author"/>
                    <form:hidden path="parent"/>
                </form:form>
            </div>
        </div>
    </div>

<script type="text/javascript">
</script>
