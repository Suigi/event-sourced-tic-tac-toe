# An Event Sourcing Example

This application implements a simple Tic Tac Toe game using Event Sourcing as its persistence mechanism. 

It is a Spring Web MVC application; the Event Sourcing part is implemented from scratch without using any specific libraries or frameworks (such as Axon). Events are persisted to Postgres using the `JdbcTemplate`.  

It is structured using Hexagonal Architecture (heavily flavored by Ted M. Young's "HexADD"). 

## Playing the Game

The application is currently deployed at https://tic-tac-toe.suigi.dev. 

You can create a new game or open an existing one, that's currently in progress. Below the game board, you can see all the Events belonging to this game. You can click the Events to view the state of the board at the point in time this Event was emitted. 
