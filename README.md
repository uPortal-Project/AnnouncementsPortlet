# Apereo Announcements Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/Announcements/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/Announcements)
[![Linux Build Status](https://travis-ci.org/Jasig/AnnouncementsPortlet.svg?branch=master)](https://travis-ci.org/Jasig/AnnouncementsPortlet)
[![Windows Build status](https://ci.appveyor.com/api/projects/status/ckh2tqajlcxhepnr/branch/master?svg=true)](https://ci.appveyor.com/project/ChristianMurphy/announcementsportlet-wj0cf/branch/master)

This is a [Sponsored Portlet][] in the uPortal project.

## Configuration

See also documentation in the [AnnouncementsPortlet wiki on Confluence][].

### Using Encrypted Property Values

You may optionally provide sensitive configuration items -- such as database passwords -- in encrypted format.  Use the [Jasypt CLI Tools][] to encrypt the sensitive value, then include it in a `.properties` file like this:

``` ini
hibernate.connection.password=ENC(9ffpQXJi/EPih9o+Xshm5g==)
```

Specify the encryption key using the `UP_JASYPT_KEY` environment variable.

## Dependencies

These dependencies are expected to be loaded by overall uPortal:

*   [Font Awesome][] 4, last tested with [Font Awesome 4.7.0][]

[Sponsored Portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[AnnouncementsPortlet wiki on Confluence]: https://wiki.jasig.org/display/PLT/Announcements+Portlet
[Font Awesome]: http://fontawesome.io/
[Jasypt CLI Tools]: http://www.jasypt.org/cli.html
[Font Awesome 4.7.0]: https://github.com/FortAwesome/Font-Awesome/releases/tag/v4.7.0
