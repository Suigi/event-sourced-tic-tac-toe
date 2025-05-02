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
        body, html {
            margin: 0;
            overflow-y: hidden;
            font-family: Helvetica, serif;
        }

        header {
            height: 5rem;
            background: lightgoldenrodyellow;
            border-bottom: 1px solid goldenrod;

            display: flex; /* Enables flexbox */
            align-items: center; /* Vertically centers content */
            padding: 0;

            max-width: 1600px;
            margin: 0 auto 1rem auto;
        }

        header .game-name {
            font-size: 2rem;
            margin-left: 1rem;
        }

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
            height: 100px;
            width: 100px;
            font-size: xxx-large;
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
            display: flex;
            flex-direction: column;
            align-items: stretch;
            padding: 0;
            height: calc(100vh - 6rem);
            overflow: hidden;

            max-width: 1600px;
            margin: 0 auto;

            background: #f5f5f5;
        }

        .game-container {
            display: flex;
            flex-direction: column;
            align-items: center;
            margin-top: 2rem;
        }

        section#events {
            margin-top: 2rem;
        }

        /* Wide screen layout - side by side */
        @media (min-width: 1024px) {
            .container {
                display: grid;
                grid-template-columns: 1fr 2fr;
                gap: 2rem;
                justify-content: stretch;
                align-items: start;
                /*padding: 2rem;*/
            }

            .game-container {
                align-items: center;
            }

            section#events {
                overflow-y: auto;
                height: 100%;
                margin-right: 2rem;
            }
        }

        /* Narrow screen layout */
        @media (max-width: 1023px) {
            body, html {
                height: 100%;
                margin: 0;
            }

            .container {
                display: flex;
                flex-direction: column;
            }

            .game-container {
                flex: 0 0 auto;
            }

            section#events {
                flex: 1 1 auto;
                overflow-y: auto;
                margin-left: 1rem;
                margin-right: 1rem;
                margin-bottom: 1rem;
            }
        }


    </style>

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
        @template.events-list(events = gameEvents, isHtmx = false)
    </section>
</div>
</body>

</html>