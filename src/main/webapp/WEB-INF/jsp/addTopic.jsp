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
<portlet:actionURL var="actionUrl" escapeXml="false">
	<portlet:param name="action" value="addTopic"/>
</portlet:actionURL>

<link rel="stylesheet" href="<rs:resourceURL value='/rs/bootstrap-namespaced/3.1.1/css/bootstrap.min.css'/>" type="text/css"/>
<link rel="stylesheet" href="<rs:resourceURL value='/rs/fontawesome/4.0.3/css/font-awesome.css'/>" type="text/css"/>
<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<script src="<rs:resourceURL value="/rs/jquery/1.10.2/jquery-1.10.2.min.js"/>" type="text/javascript"></script>
<script type="text/javascript" src="<rs:resourceURL value="/rs/jquery-migrate/jquery-migrate-1.2.1.min.js"/>"></script>
<script src="<rs:resourceURL value="/rs/jqueryui/1.10.3/jquery-ui-1.10.3.min.js"/>" type="text/javascript"></script>
<script src="<c:url value='/rs/jquery-tooltip/1.3/jquery.tooltip.js'/>" type="text/javascript"></script>

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
<div class="container-fluid bootstrap-styles announcements-container">
    <div class="row announcements-portlet-toolbar">
        <div class="col-md-8 no-col-padding">
            <h4 role="heading"><spring:message code="addTopic.heading"/></h4>
        </div>
        <div class="col-md-4 no-col-padding">
            <div class="nav-links">
                <a href="<portlet:renderURL />"><i class="fa fa-home"></i> <spring:message code="general.adminhome"/></a>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <form:form commandName="topic" method="post" action="${actionUrl}" class="form-horizontal" role="form">
                <div class="form-group">
                    <label for="title" class="col-sm-3 control-label"><spring:message code="addTopic.title"/></label>
                    <div class="col-sm-9">
                        <form:input cssClass="form-control" path="title"/>
                        <form:errors cssClass="announcements-error label label-danger" path="title"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="description" class="col-sm-3 control-label"><spring:message code="addTopic.description"/></label>
                    <div class="col-sm-9">
                        <form:textarea cssClass="form-control" path="description"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="allowRss1" class="col-sm-3 control-label"><spring:message code="addTopic.publicrss"/></label>
                    <div class="col-sm-9">
                        <div class="checkbox">
                            <form:checkbox path="allowRss" cssClass="portlet-form-input-field"/>
                        </div>
                    </div>
                </div>
                <div class="form-group">
                    <label for="subscriptionMethod" class="col-sm-3 control-label"><spring:message code="addTopic.submethod"/></label>
                    <div class="col-sm-9">
                        <div class="radio">
                            <label for="subscriptionMethod1" title="<spring:message code="addTopic.pushedforced.title"/>"><spring:message code="addTopic.pushedforced"/>
                                <form:radiobutton path="subscriptionMethod" value="1"/>
                            </label>
                        </div>
                        <div class="radio">
                            <label for="subscriptionMethod2" title="<spring:message code="addTopic.pushedoptional.title"/>"><spring:message code="addTopic.pushedoptional"/>
                                <form:radiobutton path="subscriptionMethod" value="2"/>
                            </label>
                        </div>
                        <div class="radio">
                                <label for="subscriptionMethod3" title="<spring:message code="addTopic.optional.title"/>"><spring:message code="addTopic.optional"/>
                                <form:radiobutton path="subscriptionMethod" value="3"/>
                                </label>
                        </div>
                        <form:errors cssClass="announcements-error label label-danger" path="subscriptionMethod"/>
                    </div>
                </div>
                <form:hidden path="id"/>
                <form:hidden path="creator"/>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-3">
                        <button type="submit" class="btn btn-primary"><spring:message code="addTopic.saveButton"/></button>
                        <a class="btn btn-link" href="<portlet:renderURL />"><spring:message code="addTopic.cancel"/></a>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>


