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

<script type="text/javascript" src="<c:url value="/tinymce/js/tinymce/tinymce.min.js"/>"></script>
<script type="text/javascript">
    document.addEventListener('DOMContentLoaded', function() {
        // Abstract text character counter
        var abstractText = document.getElementById('${n}abstractText');
        var abstractRemaining = document.getElementById('${n}abstractTextRemaining');
        var limit = parseInt('<c:out value="${abstractMaxLength}"/>');
        if (abstractText) {
            abstractText.addEventListener('input', function() {
                var text = abstractText.value;
                var charsLeft = limit - text.length;
                if (text.length > limit) {
                    abstractText.value = text.substring(0, limit);
                    charsLeft = 0;
                }
                abstractRemaining.textContent = charsLeft + ' <spring:message code="addAnnouncement.charactersremaining"/>';
            });
        }

        // Attachment handling
        var tinyMceFileBrowserCallback = null;
        if (typeof upAttachments !== 'undefined') {
            window.${n} = window.${n} || {};
            ${n}.addAttachmentCallback = function(result) {
                ${n}.addAttachment(result);
                upAttachments.hide();
            };
            ${n}.addAttachment = function(result) {
                var template = document.getElementById('${n}template-attachment-add-item');
                var clone = template.content.cloneNode(true);
                clone.querySelector('.remove-button').addEventListener('click', function() {
                    this.closest('.attachment-item').remove();
                });
                clone.querySelector('.attachment-filename').textContent = result.filename;
                clone.querySelector('.attachment-value').value = JSON.stringify(result);
                document.getElementById('${n}attachments').appendChild(clone);
            };
            <c:forEach items="${announcement.attachments}" var="attachment">
            ${n}.addAttachment(${attachment});
            </c:forEach>
            document.getElementById('${n}attachment_add_section').style.display = '';

            tinyMceFileBrowserCallback = function(field_name, url, type, win) {
                setTimeout(function() { window.self.focus(); }, 1);
                upAttachments.show(function(result) {
                    win.document.getElementById(field_name).value = result.path;
                    upAttachments.hide();
                    setTimeout(function() { win.focus(); }, 1);
                });
            };
        }

        // TinyMCE init
        var tinyMceOptions = { <c:out value="${tinyMceInitializationOptions}" escapeXml="false"/> };
        if (tinyMceFileBrowserCallback) {
            tinyMceOptions.file_browser_callback = tinyMceFileBrowserCallback;
        }
        tinyMCE.init(tinyMceOptions);
    });
</script>

<template id="${n}template-attachment-add-item">
    <div class="attachment-item">
        <a class="remove-button" href="javascript:void(0);"> <i class="fa fa-trash-o"></i></a>
        <span class="attachment-filename"></span>
        <input type="hidden" class="attachment-value" name="attachments" value=""/>
    </div>
</template>

<portlet:actionURL var="actionUrl" escapeXml="false">
    <portlet:param name="action" value="addAnnouncement"/>
    <portlet:param name="topicId" value="${announcement.parent.id}"/>
</portlet:actionURL>

    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-6 no-col-padding">
                <h4><spring:message code="addAnnouncement.header"/> <c:out value="${announcement.parent.title}"/></h4>
            </div>
            <div class="col-md-6 no-col-padding">
                <div class="nav-links">
                    <a href="<portlet:renderURL><portlet:param name="action" value="showTopic"/><portlet:param name="topicId" value="${announcement.parent.id}"/></portlet:renderURL>"><i class="fa fa-arrow-left" aria-hidden="true"></i> <spring:message code="general.backtotopic"/></a> |
                    <a href="<portlet:renderURL />"><i class="fa fa-home" aria-hidden="true"></i> <spring:message code="general.adminhome"/></a>
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
                            <form:input type="date" path="startDisplay" cssClass="form-control" id="${n}datepickerStart"/>
                            <form:errors cssClass="announcements-error label label-danger" path="startDisplay"/>
                        </div>
                    </div>
                    <div class="form-group">
                        <label class="col-sm-3 control-label" for="${n}datepickerEnd">
                            <spring:message code="addAnnouncement.end"/>
                        </label>
                        <div class="col-sm-9">
                            <form:input type="date" path="endDisplay" cssClass="form-control" id="${n}datepickerEnd"/>
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
