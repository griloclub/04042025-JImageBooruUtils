package imagebooru.filenamemaker;

/**
 *
 * @author Guilherme
 */
public class YandereFileNameGeneratorImpl extends DefaultFileNameGeneratorImpl {
    
    /**
     * Constroi um novo YandereFileNameGeneratorImpl.
     * Usa "yande.re" como prefixo, e define o separador de prexifo/id como
     * um unico caracter em branco.
     * 
     * @see DefaultFileNameGeneratorImpl
     */
    public YandereFileNameGeneratorImpl(){
        super("yande.re");
        prefIdSep=" ";//sem traço.
    }
    
}
