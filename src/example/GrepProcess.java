package example;

import java.io.DataInputStream;
import java.io.EOFException;
import java.io.IOException;
import java.io.PrintStream;

import migration.TransactionalFileInputStream;
import migration.TransactionalFileOutputStream;
import migration.MigratableProcess;

public class GrepProcess implements MigratableProcess
{
    private static final long serialVersionUID = 1L;

    private TransactionalFileInputStream  inFile;
    private TransactionalFileOutputStream outFile;
    private String query;
    private volatile boolean suspending;

    public GrepProcess(String args[]) throws Exception
    {
        if (args.length != 3) {
            System.out.println("usage: GrepProcess <queryString> <inputFile> <outputFile>");
            throw new Exception("Invalid Arguments");
        }
        
        query = args[0];
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
                
                if (line == null) break;
                
                if (line.contains(query)) {
                    out.println(line);
                }
            }
        } catch (EOFException e) {
            //End of File
        } catch (IOException e) {
            System.out.println ("GrepProcess: Error: " + e);
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
        StringBuilder showstring = new StringBuilder("GrepProcess ");
        showstring.append(this.query);
        showstring.append(" ");
        showstring.append(this.inFile.getFileName());
        showstring.append(" ");
        showstring.append(this.outFile.getFileName());
        return showstring.toString();
    }
 
}