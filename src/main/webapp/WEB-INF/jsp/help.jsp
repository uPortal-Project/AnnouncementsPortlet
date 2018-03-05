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

<link href="<c:url value='/css/announcements.css'/>" rel="stylesheet" type="text/css"/>

<div>
    <div>
        <div>
            <div>
                <h2>Adding a Topic</h2>

                <p>
                    Every announcement must have a topic, so the first step to getting an announcement published is
                    creating a new topic. You'll notice that there is already a topic named "EMERGENCY." This is a
                    reserved topic for posting emergency announcements only. (That will be covered later.)
                </p>

                <p>
                    First click on "Add a new Topic" and fill out the form completely.<br>
                </p>

                <p>
                    The different subscription methods describe how you want the audience of this topic to receive the
                    announcements.
                </p>

                <p>Additionally, you may choose to offer this topic as publicly-viewable RSS. Only audience members are
                    provided with the RSS link, but anybody who knows the link may gain access to the announcements.</p>

                <h2>Setting up Topic Permissions</h2>

                <p>Now that you've added a new topic, you must, at a bare minimum, define the audience of the topic.
                    From the home screen of the Announcements Admin portlet, select the cog icon ("Manage") from the
                    icons next to your new topic.<br>
                </p>

                <p>This brings up the topic management screen. The topic management screen is divided into two sections:
                    Announcements on top and Permissions below. In the Permissions section, locate the column labeled
                    "Audience Members" and click the "Edit" link.
                    <br/>
                </p>

                <p>You may select any combination of groups or individual users. Enter users by their unique portal user
                    IDs. When you are finished, click "Update." In this example, we've selected "Everyone" as the group,
                    which is mapped to the uPortal root group in the <strong>portlet.xml</strong> file.</p>

                <h2>Adding an Announcement</h2>

                <p>Before you add an announcement, be sure that you have created a topic and assigned some audience
                    members to it.</p>

                <p>Click on "Add Announcement" to bring up the new announcement screen. Here you must fill in all fields
                    except Link (URL), which is optional. When announcements are displayed, the abstract is not shown
                    when users view the full announcement. So if you have important information in the abstract, be sure
                    to repeat it in the message body. You must also set a date to begin displaying the announcement and
                    a date to stop displaying it. Dates can be typed in the MM/DD/YYYY format or selected from the
                    date-picker by clicking the calendar icon.<br>
                </p>

                <p>&nbsp;Save the announcement to place it into the queue for the topic.</p>

                <h2>Publishing an Announcement</h2>

                <div>
                    <div>
                        <div>
                            <p>Announcements must be published by a user with Editor privileges or higher. When
                                announcements are edited, they must be published again.</p>

                            <p>Announcements in the queue show a status of "Pending" with a red background in the topic
                                management screen. Click the green checkmark icon ("Publish") in order to publish the
                                announcement. If the announcement is scheduled to display today, the status changes to
                                "Showing" with a green background. However, if the announcement is post-dated, it will
                                show a status of "Scheduled" also with a green background. When an announcement has
                                expired (and before it is automatically deleted) it will show a status of "Expired" with
                                a red background.
                                <br>
                            </p>

                            <h2 >Unpublishing an Announcement</h2>

                            <p>Similar to publishing an announcement, to unpublish an announcement, click the red stop
                                sign icon ("Take Down") in order to unpublish the announcement. The announcement is
                                immediately changed to a status of "Pending" and is no longer displayed to users.<br>
                            </p>

                            <h2>Deleting an Announcement</h2>

                            <p>Announcements that have expired beyond the configured retention rate are automatically
                                deleted at 3 a.m. each morning. This includes announcements with a "Pending" status.
                                You may also delete an announcement manually by clicking the trash can icon ("Delete")
                                in the topic management screen.
                                <br>
                            </p>

                            <h2>Deleting a Topic</h2>

                            <p>If you are a Portal Administrator as defined by the "Portal_Administrators" group in
                                <strong>portlet.xml</strong>, you may delete an entire topic. Doing so will delete all
                                announcements within the topic and the action is not reversible. To delete the entire
                                topic, click the trash can icon ("Delete") in the "Admin Home" screen.
                                <br>
                            </p>

                            <h2>Delegating the Workload</h2>

                            <p>You should try to delegate your workload by allow other users to become Topic Admins,
                                Contributors or Editors of a certain topic. You assign them in the same way that you
                                assign Audience Members. Additionally, a diminished UI is provided for each type of role.
                                For example, contributors and editors cannot see the "Permissions" section of the Topic
                                management screen.</p>

                            <h2>Emergency Announcements</h2>

                            <p>The Portal Admin can manage a special topic named "EMERGENCY." This topic cannot be edit
                                nor can it be deleted. There can only be one emergency topic. It is created
                                automatically when the Announcements Portlet is loaded for the first time. Whenever an
                                announcement is added to the Emergency topic and published, a special announcement
                                appears for all users specified as audience members. Although the Portal Admin can
                                choose to assign different audience members to the Emergency Topic, it is advised to
                                leave it as is. You can add contributors, editors, and topic admins to the Emergency
                                topic just like any other topic.</p>

                            <p>To add an emergency announcement, follow the same procedure as for any other topic. Click
                                the cog icon ("Manage") from the Admin home screen.
                                <br>
                            </p>
                        </div>
                    </div>
                </div>
            </div>
        </div>
    </div>
</div>

For more details visit the AnnouncementsPortlet <a href="https://wiki.jasig.org/display/PLT/Announcements+Portlet" target="_blank">wiki page</a>.

<br/><br/>
<div class="nav-links">
    <a style="text-decoration:none;font-size:0.9em;" href="<portlet:renderURL portletMode="view" windowState="normal"/>"><i class="fa fa-arrow-left" aria-hidden="true"> </i><spring:message code="general.adminhome"/></a>
</div>
