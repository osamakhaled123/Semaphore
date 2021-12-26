import java.util.ArrayList;
import java.util.Scanner;


class Device extends Thread{
    protected String name;
    protected String type;
    public static int size;
    static int k = 1;
    static int c = 1;
    protected Router obj;

    public Device(String name, String type, Router obj){
        this.name = name;
        this.type = type;
        this.obj = obj;
    }
    public void run(){

        k=obj.produce(this);
        System.out.println("Connection "+ String.valueOf(k)+ ": "+this.name+" Occupied");

        obj.consume();




    }
}


class Router {
    public static int size;
    private Device store[];
    private int inptr;
    private int outptr;
    Semaphore spaces;
    Semaphore elements;

    public Router(int s){
        size = s;
        store = new Device[size];
        outptr = 0;
        inptr = 0;
        spaces = new Semaphore(size);
        elements = new Semaphore(0);
    }

    public int produce(Device obj) {
        //System.out.println(this.size);
        spaces.P(obj,true);
        store[inptr] = obj;
        //System.out.println("Connection "+ String.valueOf(inptr+1)+ ": "+obj.name+" Occupied");
        int k = inptr + 1;
        inptr = (inptr + 1) % size;
        elements.V(obj,k,true);
        return k;
    }

    public int consume() {
        Device obj = store[outptr];
        elements.P(obj,false);

        int c = outptr + 1;
        outptr = (outptr + 1) % size;
        spaces.V(obj,c,false);
        return c;
    }
}

class Semaphore {
    protected int limit;

    protected Semaphore(int limit) {
        this.limit = limit;
    }

    public synchronized void P(Device obj, boolean f) {
        limit--;

        if(f) {
            if (limit < 0) {
                System.out.println("(" + obj.name + ") (" + obj.type + ") arrived and waiting");
                try {
                    wait();
                }
                catch (InterruptedException e) {
                }
            }
            else {
                System.out.println("(" + obj.name + ") (" + obj.type + ") arrived");

            }
        }
        else{

            if (limit < 0) {
                try {
                    wait();
                } catch (InterruptedException e) {
                }
            }

        }
    }
    public synchronized void V(Device obj, int c, boolean f) {
        limit++;
        if(!f) {
            System.out.println("Connection "+String.valueOf(c)+": "+obj.name+" Login");
            System.out.println("Connection "+String.valueOf(c)+": "+obj.name+" performs online activity");
            System.out.println("Connection " + String.valueOf(c) + ": " + obj.name + " Logged out");

        }
        if (limit <= 0) notify() ;
    }

}


public class Network {
    public static void main(String [] args){
        Scanner input = new Scanner(System.in);
        System.out.println("What is the number of WI-FI Connections?");
        int N = input.nextInt();
        System.out.println("What is the number of devices Clients want to connect?");
        int TC = input.nextInt();

        ArrayList<Device> devices = new ArrayList<Device>();
        int i,j=0;
        String a,b;
        Device.size = N;
        //Router.setSize(N);
        Router obj = new Router(N);
        for(i=0;i<TC;i++){
            a = input.next();
            b = input.next();
            devices.add(new Device(a,b,obj));

        }
        for(j=0;j<TC;j++){
            devices.get(j).start();
        }

    }
}
