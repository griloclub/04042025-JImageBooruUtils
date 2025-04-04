package imagebooru.directory;

import java.io.File;


public class EShuuShuuImageDirectoryImpl extends AbstractImageDirectory {
    
    /**
     * Retorna o id de uma imagem com o nome no formato esperado.
     * Dada uma Imagem com o nome no formato "nnnn-nn-nn-[i]*.ext",
     * retorna o id dessa imagem ([i]*).
     * 
     * @param filename nome do arquivo.
     * @return id extraido
     */
    @Override
    protected int getIdFromFileName(String filename) {
        if( (filename.length()<13) || !filename.contains(".") ) { return -1; }
        filename = filename.substring(11, filename.lastIndexOf("."));
        try{
            return Integer.parseInt(filename);
        } catch(NumberFormatException ex){
            return -1;
        }
    }

    public EShuuShuuImageDirectoryImpl(File dir) {
        super(dir);    
    }
}
