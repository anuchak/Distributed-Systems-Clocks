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

    public void updateProcessClocks(Event e, long lamportMessageClock, List<Integer> vectorMessageClock)
    {
        switch (e)
        {
            case INTERNAL:
                lock.writeLock().lock();
                try {
                    lamportClock.updateForInternal();
                    vectorClock.updateForInternal();
                    updateEventRecordList(e);
                } finally {
                    lock.writeLock().unlock();
                }
                break;
            case SEND:
                lock.writeLock().lock();
                try {
                    lamportClock.updateForSend();
                    vectorClock.updateForSend();
                    updateEventRecordList(e);
                } finally {
                    lock.writeLock().unlock();
                }
                break;
            case RECEIVE:
                lock.writeLock().lock();
                try {
                    lamportClock.updateForReceive(lamportMessageClock);
                    vectorClock.updateForReceive(vectorMessageClock);
                    updateEventRecordList(e);
                } finally {
                    lock.writeLock().unlock();
                }
        }
    }

    public List<EventRecord> getEventRecordList() {
        return eventRecordList;
    }

    private void updateEventRecordList(Event e)
    {
        EventRecord record = new EventRecord(processName, e, lamportClock.getClock(), vectorClock.getClock());
        eventRecordList.add(record);
    }

}
