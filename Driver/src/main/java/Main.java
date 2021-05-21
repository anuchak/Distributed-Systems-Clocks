import java.util.*;
import java.util.concurrent.locks.ReentrantLock;

public class Main
{
    private static final Map<String, MockProcess> PROCESS_LIST = new HashMap<>();
    private static final ReentrantLock RETRIEVE_UPDATE_VAL_LOCK = new ReentrantLock();
    public static void main (String[] args)
    {
        setUp(3);
        List <Thread> allThreads = new ArrayList<>();
        Random rand = new Random();
        int option;
        do
        {
            print();
            Scanner sc = new Scanner(System.in);
            option = sc.nextInt();

            switch(option)
            {
                case 1:
                    System.out.println("Enter name of sender process");
                    String sender = sc.next();
                    System.out.println("Enter name of receiver process");
                    String receiver = sc.next();
                    MockProcess senderProcess = PROCESS_LIST.get(sender);
                    MockProcess receiverProcess = PROCESS_LIST.get(receiver);
                    Thread t = new Thread(() -> {
                        long messageSendLamportClock = -1;
                        List <Integer> messageSendVectorClock = null;
                        if(Objects.nonNull(senderProcess))
                        {
                            try
                            {
                                RETRIEVE_UPDATE_VAL_LOCK.lock();
                                senderProcess.updateProcessClocks(Event.SEND, -1, null);
                                messageSendLamportClock = senderProcess.getLamportClock().getClock();
                                messageSendVectorClock = new ArrayList<>(senderProcess.getVectorClock().getClock());
                            }
                            finally {
                                RETRIEVE_UPDATE_VAL_LOCK.unlock();
                            }
                        }

                        try
                        {
                            Thread.sleep(rand.nextInt(5001));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        if(Objects.nonNull(receiverProcess))
                        {
                            receiverProcess.updateProcessClocks(Event.RECEIVE, messageSendLamportClock, messageSendVectorClock);
                        }
                    });
                    t.start();
                    allThreads.add(t);
                    // func();
                    break;
                case 2:
                    System.out.println("Enter name of process");
                    String process = sc.next();
                    MockProcess p = PROCESS_LIST.get(process);
                    Thread t1 = new Thread(() -> {
                        if(Objects.nonNull(p))
                        {
                            p.updateProcessClocks(Event.INTERNAL, -1, null);
                        }
                    });
                    t1.start();
                    allThreads.add(t1);
                    // func();
                    break;
                case 3:
                    System.out.println("Enter name of sender process");
                    String broadcast = sc.next();
                    MockProcess broadcastSender = PROCESS_LIST.get(broadcast);
                    Thread t2 = new Thread(() -> {
                        long messageBroadcastLamportClock = -1;
                        List <Integer> messageBroadcastVectorClock = null;
                        if(Objects.nonNull(broadcastSender))
                        {
                            try
                            {
                                RETRIEVE_UPDATE_VAL_LOCK.lock();
                                broadcastSender.updateProcessClocks(Event.SEND, -1, null);
                                messageBroadcastLamportClock = broadcastSender.getLamportClock().getClock();
                                messageBroadcastVectorClock = new ArrayList<>(broadcastSender.getVectorClock().getClock());
                            }
                            finally {
                                RETRIEVE_UPDATE_VAL_LOCK.unlock();
                            }
                        }

                        try
                        {
                            Thread.sleep(rand.nextInt(6001));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        long finalMessageBroadcastLamportClock = messageBroadcastLamportClock;
                        List<Integer> finalMessageBroadcastVectorClock = messageBroadcastVectorClock;
                        PROCESS_LIST.forEach((name, broadcastReceiver) -> {
                            if(!name.equals(broadcast))
                            {
                                broadcastReceiver.updateProcessClocks(Event.RECEIVE, finalMessageBroadcastLamportClock, finalMessageBroadcastVectorClock);
                            }
                        });
                    });
                    t2.start();
                    allThreads.add(t2);
                    // func();
                    break;
                default:
                    System.out.println("Not valid option, -1 for exit");
            }

        }while(option != -1);

        allThreads.forEach(t ->
        {
            try
            {
                t.join();
            }
            catch(Exception e)
            {
                System.out.println("Error while waiting for thread: " + t.getName());
                e.printStackTrace();
            }
        });
        func();

    }

    private static void print()
    {
        String menu = "1) Send a message to specific process\n" +
                "2) Note down internal event\n" +
                "3) Broadcast a message";
        System.out.println(menu);
    }

    private static void setUp(int totalProcess)
    {
        String name;
        Scanner sc = new Scanner(System.in);
        for(int i = 0; i < totalProcess; i++)
        {
            System.out.println("Enter name for process");
            name = sc.next();
            MockProcess p = new MockProcess(name, i, totalProcess);
            PROCESS_LIST.put(name, p);
        }
    }

    private static void func()
    {
        PROCESS_LIST.forEach((name, p) ->
        {
            System.out.printf("Lamport clock of %s is %d \n\n", name, p.getLamportClock().getClock());
            System.out.println("Vector clock of " + name + " is " + p.getVectorClock().getClock() + "\n");
            System.out.println("All events that occurred in process " + name + " are " + p.getEventRecordList() + "\n");
        });
    }

}
