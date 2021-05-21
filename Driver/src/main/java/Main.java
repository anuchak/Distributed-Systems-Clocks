import java.util.*;

public class Main
{
    private static final Map<String, MockProcess> PROCESS_LIST = new HashMap<>();
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
                        if(Objects.nonNull(senderProcess))
                        {
                            senderProcess.updateLamportClock(Event.SEND, -1);
                            senderProcess.updateVectorClock(Event.SEND, null);
                            senderProcess.updateEventRecordList(Event.SEND);
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
                            receiverProcess.updateLamportClock(Event.RECEIVE, senderProcess.getLamportClock().getClock());
                            receiverProcess.updateVectorClock(Event.RECEIVE, senderProcess.getVectorClock().getClock());
                            receiverProcess.updateEventRecordList(Event.RECEIVE);
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
                            p.updateLamportClock(Event.INTERNAL, -1); p.updateVectorClock(Event.INTERNAL, null);
                            p.updateEventRecordList(Event.INTERNAL);
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
                        if(Objects.nonNull(broadcastSender))
                        {
                            broadcastSender.updateLamportClock(Event.SEND, -1);
                            broadcastSender.updateVectorClock(Event.SEND, null);
                            broadcastSender.updateEventRecordList(Event.SEND);
                        }

                        try
                        {
                            Thread.sleep(rand.nextInt(6001));
                        } catch (Exception e)
                        {
                            e.printStackTrace();
                        }

                        PROCESS_LIST.forEach((name, broadcastReceiver) -> {
                            if(!name.equals(broadcast))
                            {
                                broadcastReceiver.updateLamportClock(Event.RECEIVE, broadcastSender.getLamportClock().getClock());
                                broadcastReceiver.updateVectorClock(Event.RECEIVE, broadcastSender.getVectorClock().getClock());
                                broadcastReceiver.updateEventRecordList(Event.RECEIVE);
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
