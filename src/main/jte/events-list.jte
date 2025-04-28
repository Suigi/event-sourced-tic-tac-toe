@import ninja.ranner.xogame.adapter.in.web.EventView
@import java.util.List
@param List<EventView> events
@param boolean isHtmx

<div id="events-container"
     hx-swap-oob='${isHtmx ? "events-container" : null}'
>
    <style>
        #events-container {
            display: grid;
            grid-template-columns: 1fr;
            gap: 0.5rem;
            justify-items: end;
        }

        .event {
            border: 1px solid green;
            display: grid;
            grid-template-columns: auto 1fr;
            padding: 0.5rem;
            background: #e1ffdb;
            width: 300px;
            column-gap: 0.5rem;
        }

        .event pre {
            margin-bottom: 0.5rem;
            margin-top: 0.5rem;
        }

        .event .event-name {
            grid-column-start: 0;
            grid-column-end: span 2;
            font-weight: bold;
        }

        .event .key {
            text-align: left;
        }

        .event .value {
            text-align: right;
            overflow: hidden;
        }
    </style>
    @for(EventView event : events)
        <div class="event">
            <span class="event-name">${event.eventName()}</span>
            @for(EventView.FieldView field : event.fields())
                <pre class="key">${field.name()}</pre>
                <pre class="value">${field.value()}</pre>
            @endfor
        </div>
    @endfor
</div>