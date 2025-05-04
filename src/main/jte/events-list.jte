@import gg.jte.support.ForSupport
@import ninja.ranner.xogame.adapter.in.web.EventView
@import java.util.List
@param List<EventView> events
@param boolean isHtmx
@param int skippedEvents
@param String baseUrl

<div id="events-container"
     hx-swap-oob='${isHtmx ? "events-container" : null}'
>
    @for(var event : ForSupport.of(events))
        <div class="${event.getIndex() < skippedEvents ? "event skipped" : "event"}"
             hx-get="${baseUrl}?numberOfEventsToSkip=${event.getIndex()}"
             hx-target="#board"
             hx-swap="outerHTML"
        >
            <span class="event-name">${event.get().eventName()}</span>
            @for(EventView.FieldView field : event.get().fields())
                <div>
                    <strong class="key">${field.name()}</strong>:
                    <span class="value">${field.value()}</span>
                </div>
            @endfor
        </div>
    @endfor
</div>