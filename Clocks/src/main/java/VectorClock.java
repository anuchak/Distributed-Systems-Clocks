import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class VectorClock
{
    private final int position;
    private final int totalProcess;
    private final List<Integer> clock;
    private final ReadWriteLock lock = new ReentrantReadWriteLock();

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
        try {
            lock.writeLock().lock();
            int val = clock.get(position);
            val++;
            clock.set(position, val);
        } finally {
            lock.writeLock().unlock();
        }
    }

    public void updateForSend()
    {
        try {
            lock.writeLock().lock();
            int val = clock.get(position);
            val++;
            clock.set(position, val);
        } finally {
          lock.writeLock().unlock();
        }
    }

    public void updateForReceive(List <Integer> messageClock)
    {
        try {
            lock.writeLock().lock();
            int val = clock.get(position);
            val++;
            clock.set(position, val);
            for (int i = 0; i < totalProcess; i++) {
                int currentVal = clock.get(i);
                int messageClockVal = messageClock.get(i);
                clock.set(i, Math.max(currentVal, messageClockVal));
            }
        } finally {
            lock.writeLock().unlock();
        }
    }


}
