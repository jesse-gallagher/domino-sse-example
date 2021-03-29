# Domino SSE Example

This project shows an example of using Server-Sent Events on Domino inside an Expeditor webapp.

## Running

Run `mvn package` on the project and then import "site.xml" from the target/repository directory into an active update site on a Domino server. Then, restart HTTP and visit "/sseexampleapp" on your server.

## License

The code in the project is licensed under the Apache License 2.0. The runtime may download software licensed under compatible licenses - see NOTICE for details.