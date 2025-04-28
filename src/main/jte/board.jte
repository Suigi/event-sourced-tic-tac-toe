@import ninja.ranner.xogame.adapter.in.web.GameController.GameView
@param GameView game

<div id="board">
    @for(int x=0;x<3;x++)
        @for(int y=0;y<3;y++)
            <button
                    class="${game.cssClassFor(x,y)}"
                    hx-post="/games/${game.gameId()}/fill"
                    hx-vals='{"x":${x},"y":${y}}'
                    hx-trigger="click"
                    hx-target="#board"
                    hx-swap="outerHTML"
            >
                ${game.cellAt(x,y)}
            </button>
        @endfor
    @endfor

</div>
