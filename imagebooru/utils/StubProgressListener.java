package imagebooru.utils;

/**
 * Stub para ProgressListener.
 * Usado para evitar uso de métodos auxiliares ou checagem constante nulls ao
 * trabalhar com ProgressListener.
 *
 * @author Guilherme
 * @created 03/03/2013
 */
public class StubProgressListener implements ProgressListener{

    @Override
    public void doNotifyProgress(String progress_decription) { }

    @Override
    public void doNotifyProgress(double percentDone) { }

    @Override
    public void doNotifyProgress(int itensDone, int itensLeft) { }
    
}
