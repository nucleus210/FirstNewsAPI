First News API 
==============

ABDN project.
==============

Uses The Guardian - Open Platform.

Short for project:

* main search in tool bar. When user click on search popup chips with main search endpoints;

* Data from internet is fetchet via AsyncTaskLoader. After that I store data in single tone model;

* Network fragment to encapsulate data;

* Download callback interface;

* Simple messaging with Server responds codes and current download states;

* Drawer with provided option: Favorite, Connection, Settings and Share. Last is not finish;

* Toolbar setting menu with two options. 

* Favorite Fragment - start favotrite list and second is Settings where are all Searching Preferences;

* Preferences settings are stored into three main categories:

            --> First is "Content Search". Here have a lot of option from Api. Provided sections, from-date, to-date, page size, page number and few more. 
                When user store new preference after go back to main activity data is fetchet automaticly. When app is restarted data is get from user preferences;

            --> "Tag Search": options are search, page number, page size, show preferences;

            --> "General Settings": options check default search, backup data. Data is backup into Shared preference.
            
* Apllication uses all common searches from API.

![alt text](https://image.ibb.co/ntZqLz/Firstnews.png)
