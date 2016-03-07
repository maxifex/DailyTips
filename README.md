## Daily Tip Android Application
An android app that displays Daily information from a preloaded SQLite DB and from web service. This app is simple but has solid implementation of Syc
It also allows you to share such information to friends and contacts.
The app is composed of one main screen that implements fragment pager adapter. The pager allows the user to scroll pages of daily tips horizontally forward and backward.
The user has various options in the "settings menu" to change language, turn off notification and change the frequency of notification.



## Overview
The app does the following:

1. Load a list of nicely formatted daily tips from a preloades SQLite DB
2. Display Daily pages with the implementation of fragment state pager adapter which is efficient for large number of pages
3. Scroll/swipe left or right to go back and forth in day 
4. Use a share intent to send recommended messages to friend/contact
5. Use settings to determine frequency of notification and languages to read in.

To achieve this, there are five different components in this app:


1. `CustomPagerAdapter` - Responsible for extending `FragmentStatePagerAdapter` that users Fragment to manage each page.
2. `Provider` - Encapsulates the data and manages access to the structured daily tip data.
3. `DbHelper` - Manages a local database for tip data.
4. `Contract` - Defines table and column names for the database.
5. `MainActivity` - Responsible for providing Daily information view and share intent.
6. `SyncAdapter` - Responsible for executing the API requests and retrieving the JSON from thirdpart application


## Libraries

This app leverages two third-party libraries:

 * [Android AsyncHTTPClient](http://loopj.com/android-async-http/) - For asynchronous network requests
 * [Picasso](http://square.github.io/picasso/) - For remote image loading

