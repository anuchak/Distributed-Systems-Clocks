import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class VectorClock
{
    private final int position;
    private final int totalProcess;
    private final List<Integer> clock;

    public VectorClock(int position, int totalProcess)
    {
        this.position = position;
        this.totalProcess = totalProcess;
        clock = new ArrayList<>(Collections.nCopies(totalProcess, 0));
    }

    public List<Integer> getClock()
    {
        return clock;
    }

    public void updateForInternal()
    {
        int val = clock.get(position);
        val++;
        clock.set(position, val);
    }

    public void updateForSend()
    {
        int val = clock.get(position);
        val++;
        clock.set(position, val);
    }

    public void updateForReceive(List <Integer> messageClock)
    {
        int val = clock.get(position);
        val++;
        clock.set(position, val);
        for (int i = 0; i < totalProcess; i++) {
            int currentVal = clock.get(i);
            int messageClockVal = messageClock.get(i);
            clock.set(i, Math.max(currentVal, messageClockVal));
        }
    }


}
