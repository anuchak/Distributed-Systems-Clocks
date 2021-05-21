import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

public class MockProcess
{
    private final String processName;
    private LamportClock lamportClock;
    private VectorClock vectorClock;
    private final int position;
    private final int totalProcess;
    private List <EventRecord> eventRecordList = new ArrayList<>();
    private ReadWriteLock lock = new ReentrantReadWriteLock();

    public MockProcess(String name, int position, int totalProcess)
    {
        this.processName = name;
        this.position = position;
        this.totalProcess = totalProcess;
        lamportClock = new LamportClock();
        vectorClock = new VectorClock(position, totalProcess);
    }

    public LamportClock getLamportClock() {
        return lamportClock;
    }

    public void setLamportClock(LamportClock lamportClock) {
        this.lamportClock = lamportClock;
    }

    public VectorClock getVectorClock() {
        return vectorClock;
    }

    public void setVectorClock(VectorClock vectorClock) {
        this.vectorClock = vectorClock;
    }

    public String getProcessName() {
        return processName;
    }

    public void updateLamportClock(Event e, long messageClock)
    {
        switch (e)
        {
            case INTERNAL:
                lamportClock.updateForInternal();
                break;
            case SEND:
                lamportClock.updateForSend();
                break;
            case RECEIVE:
                lamportClock.updateForReceive(messageClock);
        }
    }

    public void updateVectorClock(Event e, List<Integer> messageClock)
    {
        switch (e)
        {
            case INTERNAL:
                vectorClock.updateForInternal();
                break;
            case SEND:
                vectorClock.updateForSend();
                break;
            case RECEIVE:
                vectorClock.updateForReceive(messageClock);
        }
    }

    public List<EventRecord> getEventRecordList() {
        return eventRecordList;
    }

    public void updateEventRecordList(Event e)
    {
        try {
            lock.writeLock().lock();
            EventRecord record = new EventRecord(processName, e, lamportClock.getClock(), vectorClock.getClock());
            eventRecordList.add(record);
        }
        finally {
            lock.writeLock().unlock();
        }
    }

}
