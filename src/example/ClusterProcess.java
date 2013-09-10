package example;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;
import java.util.HashMap;

import migration.MigratableProcess;
import migration.TransactionalFileInputStream;
import migration.TransactionalFileOutputStream;

public class ClusterProcess implements MigratableProcess {
	
    private static final long serialVersionUID = 1L;
    private HashMap<String, Integer> map = new HashMap<String, Integer>();
    private TransactionalFileInputStream  inFile;
    private TransactionalFileOutputStream outFile;
    private volatile boolean suspending;
    
    public ClusterProcess(String args[]) throws Exception {
    	if(args.length != 2) {
    		System.out.println("usage: ClusterProcess <inputFile> <outputFile>");
            throw new Exception("Invalid Arguments");
    	}
    	
        inFile = new TransactionalFileInputStream(args[1]);
        outFile = new TransactionalFileOutputStream(args[2], false);
    }
    
    public void run()
    {
        PrintStream out = new PrintStream(outFile);
        DataInputStream in = new DataInputStream(inFile);

        try {
            while (!suspending) {
                @SuppressWarnings("deprecation")
                String line = in.readLine();
                
                if (line == null) {
                	for(String e : this.map.keySet()) {
                		out.println(e + " " + this.map.get(e));
                	}
                	break;
                }
                
                if(this.map.containsKey(line)) {
                	this.map.put(line, this.map.get(line) + 1);
                }
                else {
                	this.map.put(line, 1);
                }
                
                try {
                    Thread.sleep(40000);
                } catch (InterruptedException e) {
                    // ignore it
                }
            }
        } catch (EOFException e) {
            //End of File
        } catch (IOException e) {
            System.out.println ("ClusterProcess: Error: " + e);
        }

        suspending = false;
System.out.println("Running complete");
    }
    
    public void suspend()
    {
        suspending = true;
        while (suspending);
    }

    @Override
    public String toString() {
        StringBuilder showstring = new StringBuilder("ClusterProcess ");
        showstring.append(this.inFile.getFileName());
        showstring.append(" ");
        showstring.append(this.outFile.getFileName());
        return showstring.toString();
    }
}
