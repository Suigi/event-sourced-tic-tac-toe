@import ninja.ranner.xogame.adapter.in.web.EventView
@import ninja.ranner.xogame.adapter.in.web.GameController.GameView
@import java.util.List
@param GameView game
@param List<EventView> gameEvents
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <script src="https://unpkg.com/htmx.org@2.0.4"
            integrity="sha384-HGfztofotfshcF7+8n44JQL2oJmowVChPTg48S+jvZoztPfvwD79OC/LTtG6dMp+"
            crossorigin="anonymous"></script>
    <link rel="stylesheet" href="/game.css">
    <title>Game</title>
</head>

<body>
<header>
    <div class="game-name">
        Game: ${game.name()}
    </div>
</header>
<div class="container">
    <section class="game-container">
        @template.board(game = game)
        @template.game-result(game = game, isHtmx = false)
    </section>
    <section id="events">
        @template.events-list(
        events = gameEvents,
        isHtmx = false,
        skippedEvents = 0,
        baseUrl = "/games/" + game.gameId())
    </section>
</div>
</body>

</html>