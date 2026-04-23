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

<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<div class="container-fluid announcements-container">
    <div class="row announcements-portlet-toolbar">
        <div class="col-md-8 no-col-padding">
            <h4>
								<spring:message code="addTopic.heading"/>
						</h4>
        </div>
        <div class="col-md-4 no-col-padding">
            <div class="nav-links">
                <a href="<portlet:renderURL />">
										<i class="fa fa-home" aria-hidden="true"></i>
										<spring:message code="general.adminhome"/>
								</a>
            </div>
        </div>
    </div>
    <div class="row">
        <div class="col-md-12">
            <form:form commandName="topic" method="post" action="${actionUrl}" class="form-horizontal" role="form">
                <div class="form-group">
                    <label for="title" class="col-sm-3 control-label">
												<spring:message code="addTopic.title"/>
										</label>
                    <div class="col-sm-9">
                        <form:input cssClass="form-control" path="title"/>
                        <form:errors cssClass="announcements-error label label-danger" path="title"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="description" class="col-sm-3 control-label">
												<spring:message code="addTopic.description"/>
										</label>
                    <div class="col-sm-9">
                        <form:textarea cssClass="form-control" path="description"/>
                    </div>
                </div>
                <div class="form-group">
                    <label for="allowRss1" class="col-sm-3 control-label">
												<spring:message code="addTopic.publicrss"/>
										</label>
                    <div class="col-sm-9">
                        <div class="checkbox">
                            <form:checkbox path="allowRss" cssClass="portlet-form-input-field"/>
                        </div>
                    </div>
                </div>
                <fieldset class="form-group">
                    <legend class="col-sm-3 control-label" id="radio-label">
                        <spring:message code="addTopic.submethod"/>
                    </legend>
                    <div class="col-sm-9" aria-required="true" role="radiogroup" aria-labelledby="radio-label">
                        <div class="radio">
                            <form:radiobutton role="radio" path="subscriptionMethod" value="1"/>
                            <label for="subscriptionMethod1" title="<spring:message code="addTopic.pushedforced.title"/>">
                                <spring:message code="addTopic.pushedforced"/>
                            </label>
                        </div>
                        <div class="radio">
                            <form:radiobutton role="radio" path="subscriptionMethod" value="2"/>
                            <label for="subscriptionMethod2" title="<spring:message code="addTopic.pushedoptional.title"/>">
                                <spring:message code="addTopic.pushedoptional"/>
                            </label>
                        </div>
                        <div class="radio">
                            <form:radiobutton role="radio" path="subscriptionMethod" value="3"/>
                            <label for="subscriptionMethod3" title="<spring:message code="addTopic.optional.title"/>">
                                <spring:message code="addTopic.optional"/>
                            </label>
                        </div>
                        <form:errors cssClass="announcements-error label label-danger" path="subscriptionMethod"/>
                    </div>
                </fieldset>
                <form:hidden path="id"/>
                <form:hidden path="creator"/>
                <div class="form-group">
                    <div class="col-sm-9 col-sm-offset-3">
                        <button type="submit" class="btn btn-primary">
														<spring:message code="addTopic.saveButton"/>
												</button>
                        <a class="btn btn-link" href="<portlet:renderURL />">
														<spring:message code="addTopic.cancel"/>
												</a>
                    </div>
                </div>
            </form:form>
        </div>
    </div>
</div>
