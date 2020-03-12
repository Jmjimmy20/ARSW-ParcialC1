package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import sun.awt.Mutex;

public class ThreadMoney  extends Thread{

    private MoneyLaundering moneyLaundering;
    private TransactionAnalyzer analyzer;
    private TransactionReader reader;
    Mutex m;
    boolean isPaused;
    List<File> files;

    public ThreadMoney(TransactionAnalyzer analyzer, MoneyLaundering moneyLaundering, TransactionReader reader){
        this.analyzer = analyzer;
        this.reader = reader;
        this.moneyLaundering = moneyLaundering;
        isPaused= false;
        m=new Mutex();
        files = new ArrayList<File>();
    }

    public void addToThread(File f){
        files.add(f);
    }

    public void pauseThread(){
        isPaused = false;
    }

    public void continueThread(){
        isPaused = true;
        synchronized (m){
            m.notify();
        }
    }

}