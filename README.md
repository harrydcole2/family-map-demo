# family-map-demo

This project comes from a BYU class I took previously that taught me a bit about Java and the client-server model. Unlike a production project, in this app almost everything was programmed manually to teach us what a full stack application entails. Since then we've learned more advanced design patterns but overall, it is a project that I am very proud of beccause it was sort of a rite of passage into the CS program.

**Synopsis**

The application is an Android app that uses Google Maps to show migration patterns of your ancestors and descendants, inspired by FamilySearch but with fake data. When you login or register you are taken to a map that has markers of different colors representing different kinds of life events so you can see the diaspora over time. When you click on a marker, a red line is drawn to connect that marker to its spouse, blue lines are drawn in a tree to show family tree lines (e.g. children to parents to grandparents), and green lines to connect people to their events (e.g. birth, marriage, or death). You can filter any of the map data by lines, sides of your family, etc and change other settings. You can also search for people and navigate between profiles.

You can find the spec for the application at this youtube link: [https://www.youtube.com/watch?v=erjZcIDL9ek&t=1s](url)

**Notes about this project**

- The FamilyMapApp folder contains a directory that is meant to be run in Android studio locally. Would need a shared jar file from the Server with common req/res objects and to download correct dependdencies/images. MainActivity could be considered a UI starting point, and features some tasks, proxies, and a cache.
- The FamilyMapServer contains a directory to run in InteliJ, but any IDE works (it is just an API). The starting point is the Server, which has handlers that call services for various backend operations. DAOs, DTOs, and other classes also are available.
- The database is SQLite and a DB browser tool is very helpful. In our app, the DB is a literal file that I didn't include in this repo called familymap.db which was in the FamilyMapServer folder.
- There are unit tests in each part of the tech stack, but they are not comprehensive; just exposure for writing tests with JUnit and AndroidJUnit.

> Since I have taken the class, the content has been updated and the family map app is no longer the core project, which is why I decided to put it on GitHub and no longer host the code on my personal machine.
