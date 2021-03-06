Change Log
==========

### Syncany GUI Plugin 0.4.2-alpha (Date: 10 Jan 2015)
- Patch release to fix serialization issue in 'sy ls'
  when daemon/GUI is running.

### Syncany GUI Plugin 0.4.1-alpha (Date: 10 Jan 2015)
- New 'Preferences' dialog with ability to remove/add plugins, 
  enable/disable notifications, and change the proxy settings #334/#321
- New 'Remove folder' feature in tray menu #330
- New 'Copy link' feature in tray menu #336

### Syncany GUI Plugin 0.4.0-alpha (Date: 28 Dec 2014)
- Major revamp; for details see #297
  + Completely new init/connect wizard screens with dynamic screens for
    plugins, OAuth-based plugin support, file/folder selection for
    `File` fields (distiction via `fileType`)
  + User interaction: event/WS-based confirm and get-password callbacks;
    also introduces `EventResponse`s
  + Support for invisible plugin fields (also for OAuth)
  + Fixed internationalization

### Syncany GUI Plugin 0.3.0-alpha (Date: 9 Dec 2014)
- Updated for new core

### Syncany GUI Plugin 0.1.12-alpha (Date: 19 Oct 2014)
- First release
- Basic functionalities:
  + Tray icon with status
  + Notifications on changes

