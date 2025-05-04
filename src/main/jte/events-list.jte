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
    <style>
        #events-container {
            display: grid;
            grid-template-columns: 1fr;
            gap: 0.5rem;
            justify-items: stretch;
        }

        .event {
            border: 1px solid green;
            border-left: 3px solid green;
            display: grid;
            grid-template-columns: 1fr;
            padding: 0.5rem;
            background: #e1ffdb;
            gap: 0.5rem;
        }

        .event.skipped {
            opacity: 0.3;
        }

        .event pre {
            margin-bottom: 0.5rem;
            margin-top: 0.5rem;
        }

        .event .event-name {
            font-weight: bold;
            font-size: 1.1rem;
            margin-bottom: 0.2rem;
        }

        .event .key {
            text-align: left;
        }

        .event .value {
            text-align: right;
            overflow: hidden;
            text-wrap: nowrap;
            font-family: monospace;
        }
    </style>
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