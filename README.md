# Apereo Announcements Portlet

[![Maven Central](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/Announcements/badge.svg)](https://maven-badges.herokuapp.com/maven-central/org.jasig.portlet/Announcements)
[![Linux Build Status](https://travis-ci.org/Jasig/AnnouncementsPortlet.svg?branch=master)](https://travis-ci.org/Jasig/AnnouncementsPortlet)
[![Windows Build status](https://ci.appveyor.com/api/projects/status/ckh2tqajlcxhepnr/branch/master?svg=true)](https://ci.appveyor.com/project/ChristianMurphy/announcementsportlet-wj0cf/branch/master)

This is a [Sponsored Portlet][] in the uPortal project.

## Configuration

See also documentation in the [AnnouncementsPortlet wiki on Confluence][].

### Attachments
See [docs/attachments.md](https://github.com/Jasig/SimpleContentPortlet/blob/HEAD/docs/attachments.md) of the [Simple Content Portlet](https://github.com/Jasig/SimpleContentPortlet) for information regarding attachments.

### Using Encrypted Property Values

You may optionally provide sensitive configuration items -- such as database passwords -- in encrypted format. Use the [Jasypt CLI Tools][] to encrypt the sensitive value, then include it in a `.properties` file like this:

```ini
hibernate.connection.password=ENC(9ffpQXJi/EPih9o+Xshm5g==)
```

Specify the encryption key using the `UP_JASYPT_KEY` environment variable.

## Dependencies

These dependencies are expected to be loaded by overall uPortal:

* [Font Awesome][] 4, last tested with [Font Awesome 4.7.0][]
* [Bootstrap][] 3, last tested with [Bootstrap 3.3.7][]

[sponsored portlet]: https://wiki.jasig.org/display/PLT/Jasig+Sponsored+Portlets
[announcementsportlet wiki on confluence]: https://wiki.jasig.org/display/PLT/Announcements+Portlet
[font awesome]: http://fontawesome.io/
[bootstrap]: https://getbootstrap.com
[bootstrap 3.3.7]: https://getbootstrap.com/docs/3.3/
[jasypt cli tools]: http://www.jasypt.org/cli.html
[font awesome 4.7.0]: https://github.com/FortAwesome/Font-Awesome/releases/tag/v4.7.0
