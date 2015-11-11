Group 6
karl, keane, tim, mike, steve

To start, our submission contains the src folder. In it, we have all of our tests and source files.

The tests folder contains our tests, and the wiki folder contains the source code. The comp3350.tests folder has 5 packages. 
The first is the acceptance package, which contains the acceptance tests for our app. It also has the business package which
tests the business objects in our wiki.business folder. In the integration package, we have our integration tests for testing 
the seams in our project, and the tests.objects package tests our objects in the wiki.objects package. It also now contains our
stub DB. There's also 2 java files in the tests package which run all of our tests.

The wiki folder has 5 packages. The application package contains our main method, and the package is mainly used for getting our DB
set up. The business package is used to allow communication between the persistence layer (our database) and the presentation layer 
(our UI). It's methods mainly consist of getting and editing our domain objects. Next, we have the objects package, which contains 
the domain-level objects. Then, we have the persistence package which contains our database and it's interface. Finally, we have the 
presentation package which contains the java code for all of our UIs as well as some static helper classes used to reduce duplicate 
code.

The major files among these packages would be of course, main, which gets everything running, as well as a few others. DataAcessObject 
is pretty important because it is the interface for out DB (or stubDB). HomeActivity is also fairly important because it is the first 
activity that the users see and all the other activities used in our project are children of it.

The assets and database folders are 2 other important folders. Together, they get the real database to function correctly. The important
files in these folders would have to be the RestoreDB.bat script, which restores our DB to its original state, as well as SC.script 
which basically stores the database's initial contents as SQL commands so upon boot, the commands can be called and the DB can be
pre-populated.

The next folders we have are the lib and libs folders. They just have necessary jar files, not that interesting. Our docs folder has 
a few pdf files that kind of explain our original vision for our project.

Next up is the res folder. This folder contains the graphical elements. The layout folder is by far the most important thing here. It
contains all of our UIs and such. All of the files that start with 'activity' in this folder are very, very crucial.

Finally, we have the contents of the root folder. AndroidManifest.xml is really important, it lists all the activities in our project
and nothing would work without it. We also have our Compile.bat that does a clean->compile and RunUnitTests.bat that runs all the 
unit tests. RunIntegrationTests.bat in here unsurprisingly runs all the integration tests as well. Additionally, we have CREDITS.MD 
which is just there and contains all the group member's names. We also have the log.txt file which is where we kept track of all the 
work that we've done on the project. At the top of the log file, we have a link to our trello board where we kept track of the 
developer tasks. We also have AllTests.txt which has the results of running all the tests.


Changes to the project in Iteration 3:
•	Improved ability of automatic hyperlinking in page bodies, now it's fully functional and doesn't infinitely link the same page over and over
•	Added "most popular page/projects" so you can see a list of the most picked pages or projects (this count does not include the "home page" (the default one that opens when opening a project) 
	as this would generally be artificially higher than all others since it is automatically opened
•	Improved search functionality so it now does a search of a page's title and body and does a somewhat "fuzzy" search
•	Implemented a "Home Page" button to easily return to the home menu when browsing pages
•	Figured out error handling so our program should no longer show an error popup but then crash before the user can read it
•	Refactored code in various parts of the program so there are fewer dependencies, fewer code smells
•	Added integration tests
•	Added acceptance tests


Changes to the project in Iteration 2:
•	Changed Pages and Projects to resemble the way they are stored in the database.
•	Added a database.
•	Added automatic hyperlinking.  If a word in the body of a page is also the title of a page in the project, the word becomes a hyperlink for that page.
•	Changed the page structure.  It is more tree-like now.
•	Basic full-text search function.
•	Added a Child class for Page organization.
•	Markdown is used in the page bodies to provide formatting.  The markdown is converted to HTML when displayed.
•	Added a delete Page/Project function.
•	Added a help button in the dropdown in the top-right corner when looking at a Page.
•	Removed an activity/UI that showed a list of all pages in a project. Now, to navigate between pages, use the sliding drawer on the left (slide to the right from the left edge).


