package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

public class ThreadMoney  extends Thread{

    private MoneyLaundering moneyLaundering;
    private TransactionAnalyzer analyzer;
    private TransactionReader reader;
    boolean isPaused;
    List<File> files;

    public ThreadMoney(TransactionAnalyzer analyzer, MoneyLaundering moneyLaundering, TransactionReader reader){
        this.analyzer = analyzer;
        this.reader = reader;
        this.moneyLaundering = moneyLaundering;
        isPaused= false;
        files = new ArrayList<File>();
    }

    public void addToThread(File f){
        files.add(f);
    }

}