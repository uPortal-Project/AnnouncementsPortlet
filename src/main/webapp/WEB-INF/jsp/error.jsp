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

    <div class="container-fluid announcements-container">
        <div class="row announcements-portlet-toolbar">
            <div class="col-md-12 no-col-padding">
                <h4><spring:message code="error.header"/></h4>
            </div>
        </div>
        <div class="row">
            <div class="col-md-12">
                <div class="alert alert-danger" role="alert">
                    <p><i class="fa fa-exclamation-triangle"></i> <c:out value="${exception.message}"/></p>
                    <p><a class="alert-link" href='<portlet:renderURL portletMode="view"/>'><spring:message code="error.goback"/></a></p>
                </div>
            </div>
        </div>
    </div>


