package imagebooru.persistence;

import imagebooru.ImgTag;
import imagebooru.ImgTags;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

/**
 * Persistencia de Tags em Arquivos de Texto.
 * Modelo classico e eficiente de armazenar info. em geral. Facil edição externa,
 * facil leitura e poucas restrições de versionamento.<br/><br/>
 * 
 * Recebe o nome do arquivo no construtor, e implementa os métodos básicos de
 * AbstractTagsPersistence.
 * 
 * @author Guilherme
 * @created 03/03/2013
 * @since 2.2
 */
public class TextFileTagsPersistenceImpl extends AbstractTagsPersistence {
    private File persist;   
    
    private Object throwIfNull(Object o){
        if(o==null) { throw new NullPointerException("Nada de nulls!"); }
        return o;
    }
    
    public TextFileTagsPersistenceImpl(String filename){
        throwIfNull(filename);
        persist = new File(filename);
    }

    @Override
    public void save(ImgTags tags) {
        try {
            FileWriter fw = new FileWriter(persist);
            for(ImgTag i : tags){ fw.write(i.toString()+"\n"); }
            fw.flush();
            fw.close();
        } catch (IOException ex) {
            throw new RuntimeException("Não Foi possivel salvar!\n"
                    +ex.getMessage());
        }     
    }

    @Override
    public ImgTags load() {
        ImgTags ret = new ImgTags();
        try {
            BufferedReader bf = new BufferedReader( new FileReader(persist) );
            String line;
            while( (line = bf.readLine())!=null){
                ret.add( new ImgTag(line) );
            }
            return ret;
        } catch (Exception ex) {
            throw new RuntimeException("Não Foi possivel localizar o arquivo!\n"
                    +ex.getMessage());
        }
    }
    
}
