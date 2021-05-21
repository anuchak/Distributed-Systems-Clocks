import java.util.ArrayList;
import java.util.List;

public class EventRecord
{
    private String process;

    private Event event;

    private long lamportClockValue;

    private List<Integer> vectorClockValue;

    public EventRecord(String process, Event e, long lamportClockValue, List <Integer> vectorClockValue)
    {
        this.process = process;
        this.event = e;
        this.lamportClockValue = lamportClockValue;
        this.vectorClockValue = new ArrayList<>(vectorClockValue);
    }

    public String getProcess() {
        return process;
    }

    public Event getEvent() {
        return event;
    }

    public long getLamportClockValue() {
        return lamportClockValue;
    }

    public List<Integer> getVectorClockValue() {
        return vectorClockValue;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder("{");
        builder.append(getEvent());
        builder.append(", ");
        builder.append(getLamportClockValue());
        builder.append(", ");
        builder.append(getVectorClockValue());
        builder.append("}");
        return builder.toString();
    }

}
