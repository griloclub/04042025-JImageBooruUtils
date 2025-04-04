package imagebooru.utils;

import java.io.PrintStream;

/**
 * Classe simples que implementa ProgressListener para uso com
 * PrintStreams.
 *
 * @author Guilherme
 * @created 03/03/2013
 * @since 2.2
 */
public class PrintStreamProgressListener implements ProgressListener{
    private PrintStream out;
    private double percent = 0;
    private int done = 0,left = 0;
    
    private void printProgress(String desc){
        out.print( String.format( "\t[Progress: %.1f (%d/%d)] %s",
                percent, done, done+left, desc) );
    }
    
    public PrintStreamProgressListener(PrintStream out){
        if(out==null) { throw new NullPointerException("out não deve ser null"); }
        this.out=out;               
    }

    @Override
    public void doNotifyProgress(String progress_decription) {
        printProgress(progress_decription+"\n");
    }

    @Override
    public void doNotifyProgress(double percentDone) {
        this.percent = percentDone;
        printProgress("\n");
    }

    @Override
    public void doNotifyProgress(int itensDone, int itensLeft) {
        this.done = itensDone;
        this.left = itensLeft;
        this.percent = done*((itensDone+itensLeft)/100);
        printProgress("\n");
    }
    
}
