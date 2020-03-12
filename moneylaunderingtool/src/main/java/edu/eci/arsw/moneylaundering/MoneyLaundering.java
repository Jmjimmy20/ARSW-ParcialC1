package edu.eci.arsw.moneylaundering;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class MoneyLaundering {
    private TransactionAnalyzer transactionAnalyzer;
    private TransactionReader transactionReader;
    private int amountOfFilesTotal;
    private AtomicInteger amountOfFilesProcessed;
    private static ThreadMoney[] threadsArray;
    private static boolean isPressed = false;

    public MoneyLaundering() {
        transactionAnalyzer = new TransactionAnalyzer();
        transactionReader = new TransactionReader();
        amountOfFilesProcessed = new AtomicInteger();
    }

    public void processTransactionData() {
        // Num de hilos
        int nThreads = 5;
        // para moverme e ir asignado archivos segun cantidad de hilos
        int cont = 0;
        // inicializar arreglo de hilos
        threadsArray = new ThreadMoney[nThreads];
        amountOfFilesProcessed.set(0);
        List<File> transactionFiles = getTransactionFileList();
        amountOfFilesTotal = transactionFiles.size();

        for (int i = 0; i < nThreads; i++) {
            threadsArray[i] = new ThreadMoney(transactionAnalyzer, this, transactionReader);
        }

        for (File transactionFile : transactionFiles) {
            // agrega el archivo, y avanza al siguiente hilo, si ya esta en la ultima,
            // vuelve al hilo 0
            threadsArray[cont].addToThread(transactionFile);
            if (cont == nThreads - 1)
                cont = 0;
            else
                cont++;
        }

        for (int i = 0; i < threadsArray.length; i++) {
            threadsArray[i].start();
        }

        for (int i = 0; i < threadsArray.length; i++) {
            try {
                threadsArray[i].join();
            } catch (InterruptedException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }

    }

    public static void pressEnter() {
        isPressed = !isPressed;
    }

    public List<String> getOffendingAccounts() {
        return transactionAnalyzer.listOffendingAccounts();
    }

    private List<File> getTransactionFileList() {
        List<File> csvFiles = new ArrayList<>();
        try (Stream<Path> csvFilePaths = Files.walk(Paths.get("src/main/resources/"))
                .filter(path -> path.getFileName().toString().endsWith(".csv"))) {
            csvFiles = csvFilePaths.map(Path::toFile).collect(Collectors.toList());
        } catch (IOException e) {
            e.printStackTrace();
        }
        return csvFiles;
    }

    public static void main(String[] args) {
        MoneyLaundering moneyLaundering = new MoneyLaundering();
        Thread processingThread = new Thread(() -> moneyLaundering.processTransactionData());
        processingThread.start();
        pressEnter();
        while(true)
        {
            Scanner scanner = new Scanner(System.in);
            String line = scanner.nextLine();
            if(line.contains("exit"))
                break;
            else if((line.equals("")||line.equals(null)) && isPressed ){
                for (int i = 0; i < threadsArray.length; i++) {threadsArray[i].pauseThread();}
                pressEnter();
            }else if(line.equals("")||line.equals(null)){
                pressEnter();
                for (int i = 0; i < threadsArray.length; i++) {threadsArray[i].continueThread();}

            }

            String message = "Processed %d out of %d files.\nFound %d suspect accounts:\n%s";
            List<String> offendingAccounts = moneyLaundering.getOffendingAccounts();
            String suspectAccounts = offendingAccounts.stream().reduce("", (s1, s2)-> s1 + "\n"+s2);
            message = String.format(message, moneyLaundering.amountOfFilesProcessed.get(), moneyLaundering.amountOfFilesTotal, offendingAccounts.size(), suspectAccounts);
            System.out.println(message);
        }

    }


}
