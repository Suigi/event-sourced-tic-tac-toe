@import ninja.ranner.xogame.adapter.in.web.GameController.GameView
@param GameView game
<!DOCTYPE html>
<html lang="en">

<head>
    <meta charset="UTF-8">
    <title>Game</title>
    <!--suppress CssUnusedSymbol -->
    <style>
        .board {
            width: fit-content;
            display: grid;
            grid-template-columns: repeat(3, 1fr);
            border: 2px solid darkblue;
            gap: 1px;
            background-color: gray;
        }
        .board .cell {
            height: 50px;
            width: 50px;
            text-align: center;
            align-content: center;
            box-sizing: border-box;
            background: white;
            font-weight: bold;
        }
        .board .player-x {
            color: red;
            background: #ffe4e7;
        }
        .board .player-o {
            color: blue;
            background: #ebfbff;
        }

    </style>
</head>

<body>
    <h1>Game: ${game.name()}</h1>
    <div class="board">
        @for(int x=0;x<3;x++)
            @for(int y=0;y<3;y++)
                <div class="${game.cssClassFor(x,y)}">${game.cellAt(x,y)}</div>
            @endfor
        @endfor

    </div>
</body>

</html>