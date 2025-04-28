package ninja.ranner.xogame.adapter.in.web;

import ninja.ranner.xogame.domain.Event;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.RecordComponent;
import java.util.Arrays;
import java.util.List;

public class EventView {

    private final String eventName;
    private final List<FieldView> fields;

    private EventView(String eventName, List<FieldView> fields) {
        this.eventName = eventName;
        this.fields = fields;
    }

    public static EventView from(Event event) {
        return new EventView(
                event.getClass().getSimpleName(),
                mapFields(event)
        );
    }

    private static List<FieldView> mapFields(Event event) {
        return Arrays.stream(event.getClass().getRecordComponents())
                     .map(rc -> new FieldView(rc.getName(), extractFieldValue(event, rc)))
                     .toList();
    }

    private static String extractFieldValue(Event event, RecordComponent e) {
        try {
            return e.getAccessor().invoke(event).toString();
        } catch (IllegalAccessException | InvocationTargetException ex) {
            return ex.toString();
        }
    }

    public String eventName() {
        return eventName;
    }

    public List<FieldView> fields() {
        return fields;
    }

    public record FieldView(String name, String value) {}

    @Override
    public String toString() {
        return "EventView{" +
                "eventName='" + eventName + '\'' +
                ", fields=" + fields +
                '}';
    }

    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;

        EventView eventView = (EventView) o;
        return eventName.equals(eventView.eventName) && fields.equals(eventView.fields);
    }

    @Override
    public int hashCode() {
        int result = eventName.hashCode();
        result = 31 * result + fields.hashCode();
        return result;
    }
}
