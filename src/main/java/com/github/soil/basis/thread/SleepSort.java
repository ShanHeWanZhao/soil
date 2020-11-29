package com.github.soil.basis.thread;

/**
 * 这排序算法，我笑了
 * @author tanruidong
 * @date 2020/08/18 15:47
 */
public class SleepSort {
    public static void main(String[] args) {
        int[] ints = {1,4,13,6,6,3,9,1212};
        SortThread[] threads = new SortThread[ints.length];
        for (int i = 0;i < ints.length;i++){
            threads[i] = new SortThread(ints[i]);
        }
        for (SortThread thread : threads) {
            thread.start();
        }
    }
}

class SortThread extends Thread{
    private int ms = 0;

    public SortThread(int ms) {
        this.ms = ms;
    }

    @Override
    public void run(){
        try{
            Thread.sleep(ms * 10 + 10);
        }catch(Exception e){

        }
        System.out.println(ms);
    }
}