package example;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;

import migration.MigratableProcess;
import migration.TransactionalFileInputStream;
import migration.TransactionalFileOutputStream;

public class ReplaceProcess implements MigratableProcess {
	
    private static final long serialVersionUID = 1L;

    private TransactionalFileInputStream  inFile;
    private TransactionalFileOutputStream outFile;
    private String findStr;
    private String replaceStr;
    private volatile boolean suspending;
    
    public ReplaceProcess(String args[]) throws Exception {
    	if(args.length != 4) {
    		System.out.println("usage: ReplaceProcess <findString> <replaceString> <inputFile> <outputFile>");
            throw new Exception("Invalid Arguments");
    	}
    	
    	this.findStr = args[0];
    	this.replaceStr = args[1];
        inFile = new TransactionalFileInputStream(args[2]);
        outFile = new TransactionalFileOutputStream(args[3], false);
    }
    
    public void run()
    {
        PrintStream out = new PrintStream(outFile);
        DataInputStream in = new DataInputStream(inFile);

        try {
            while (!suspending) {
                @SuppressWarnings("deprecation")
                String line = in.readLine();
                
                if (line == null) break;
                int index = 0;
                if((index = line.indexOf(this.findStr)) != -1) {
                    line = line.replaceAll(this.findStr, this.replaceStr);
                    out.println(line);
                }

                try {
                    Thread.sleep(6000);
                } catch (InterruptedException e) {
                    // ignore it
                }
            }
        } catch (EOFException e) {
            //End of File
        } catch (IOException e) {
            System.out.println ("ReplaceProcess: Error: " + e);
        }

        suspending = false;
    }
    
    public void suspend()
    {
        suspending = true;
        while (suspending);
    }

    @Override
    public String toString() {
        StringBuilder showstring = new StringBuilder("ReplaceProcess ");
        showstring.append(this.findStr);
        showstring.append(" ");
        showstring.append(this.replaceStr);
        showstring.append(" ");
        showstring.append(this.inFile.getFileName());
        showstring.append(" ");
        showstring.append(this.outFile.getFileName());
        return showstring.toString();
    }
}
