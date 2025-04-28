@import ninja.ranner.xogame.adapter.in.web.GameController.GameView
@param GameView game
@param boolean isHtmx

<div id="game-result"
     hx-swap-oob='${isHtmx ? "game-result" : null}'
>
    ${game.result()}
</div>
