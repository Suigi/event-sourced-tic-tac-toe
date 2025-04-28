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
    <title>Game</title>
    <!--suppress CssUnusedSymbol -->
    <style>
        button, input[type="submit"], input[type="reset"] {
            background: none;
            color: inherit;
            border: none;
            padding: 0;
            font: inherit;
            cursor: pointer;
            outline: inherit;
        }

        #board {
            width: fit-content;
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            border: 2px solid darkblue;
            gap: 1px;
            background-color: gray;
            margin-bottom: 20px;
        }

        #board .cell {
            height: 50px;
            width: 50px;
            text-align: center;
            align-content: center;
            box-sizing: border-box;
            background: white;
            font-weight: bold;
        }

        #board .cell:disabled {
            cursor: not-allowed;
        }

        #board .player-x {
            color: red;
            background: #ffe4e7;
        }

        #board .player-o {
            color: blue;
            background: #ebfbff;
        }

        .container {
            display: grid;
            grid-template-columns: auto 1fr;
            grid-gap: 0.5rem;
        }

    </style>
</head>

<body>
<h1>Game: ${game.name()}</h1>
<div class="container">
    <section class="game-container">
        @template.board(game = game)
        @template.game-result(game = game, isHtmx = false)
    </section>
    <section id="events">
        @template.events-list(events = gameEvents, isHtmx = false)
    </section>
</div>
</body>

</html>