import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

import static java.lang.Math.random;

public class Philosopher {
    int id;
    int leftFork;
    int rightFork;
    long start;
    long finish;
    List<Long> time = new ArrayList<Long>();

    public Philosopher(int id, int N) {
        this.id = id;
        this.leftFork = id;
        this.rightFork = (id + 1) % N;
    }

    public void sleep() {
        try {
            //System.out.println("ID: " + id + "sleeping");
            TimeUnit.MILLISECONDS.sleep((int) (random() * (10 - 1) + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void eat() {
        try {
            finish = System.currentTimeMillis();
            time.add(finish - start);
            System.out.println("ID: " + id + " eating after: " + (finish - start));
            TimeUnit.MILLISECONDS.sleep((int) (random() * (2 - 1) + 1));
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public boolean getTwoForks(Table table) {
        while (true) {
            if (table.semaphores[leftFork].tryAcquire()) {
                if (table.semaphores[rightFork].tryAcquire()) {
                    //System.out.println("ID: "+id +" forks: " +leftFork + " " + rightFork);
                    return true;
                } else {
                    //System.out.println("ID: "+id +" fork: " +leftFork);
                    table.semaphores[leftFork].release();
                }
            }
        }
    }

    public void releaseForks(Table table) {
        table.semaphores[leftFork].release();
        table.semaphores[rightFork].release();
    }

    public void life1(Table table) {
        while (true) {
            sleep();
            start = System.currentTimeMillis();
            if (getTwoForks(table)) {
                eat();
                releaseForks(table);
            }
        }
    }

    public boolean askWaiter(Table table){
        while(true){
            if(table.waiter.tryAcquire()){
                if(table.askForForks(id)){
                    table.waiter.release();
                    return true;
                }
                else{
                    table.waiter.release();
                }
            }
        }
    }

    public void life2(Table table) {
        while (true) {
            sleep();
            start = System.currentTimeMillis();
            if (askWaiter(table)) {
                eat();
                releaseForks(table);
            }
        }
    }

}
