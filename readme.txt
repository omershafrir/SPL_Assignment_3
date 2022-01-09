instructions to run our project:

1.

1.1.

1.2.

2. The "bad" words are hard-coded to the server. they are stored in a vector named "forbiddenWords"
   which is a private field of the server object (both the base-server and the reactor).
   This vector is initialized in the constructor of the server, therefore you can find them in
   both of the constructors (Reactor & TPC).