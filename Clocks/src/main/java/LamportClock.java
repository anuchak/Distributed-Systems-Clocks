
public class LamportClock
{
    private long clock;

    public long getClock() {
        return clock;
    }

    public void updateForInternal()
    {
        clock++;
    }

    public void updateForSend()
    {
        clock++;
    }

    public void updateForReceive(long messageClock)
    {
        clock = Math.max(clock, messageClock) + 1;
    }
}
