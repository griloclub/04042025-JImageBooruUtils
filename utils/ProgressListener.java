package imagebooru.utils;

/**
 * Interface para um ouvinte de progresso em geral.
 * Define um método simples, <tt>doNotifyProgress()</tt>, para que objetos que 
 * realizam operações demoradas possam notificar seu progresso sem interrompelas.
 *
 * @author Guilherme
 * @created 03/03/2013
 * @since 2.2
 */
public interface ProgressListener {
    public void doNotifyProgress(String progress_decription);
    public void doNotifyProgress(double percentDone);    
    public void doNotifyProgress(int itensDone, int itensLeft);            
}
