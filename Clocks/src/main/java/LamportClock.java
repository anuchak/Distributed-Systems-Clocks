import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class LamportClock
{
    private long clock;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

    public long getClock() {
        return clock;
    }

    public void updateForInternal()
    {
        try
        {
            lock.writeLock().lock();
            clock++;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public void updateForSend()
    {
        try
        {
            lock.writeLock().lock();
            clock++;
        }
        finally {
            lock.writeLock().unlock();
        }
    }

    public void updateForReceive(long messageClock)
    {
        try
        {
            lock.writeLock().lock();
            clock = Math.max(clock, messageClock) + 1;
        }
        finally {
            lock.writeLock().unlock();
        }
    }
}
