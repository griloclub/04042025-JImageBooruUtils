package imagebooru.directory;

import imagebooru.ImgFile;
import java.io.File;
import java.io.FileFilter;
import java.util.Collection;
import java.util.LinkedList;

/**
 * Modela o medium de acesso a um diretório com imagens.
 * As "Imagens" devem ser pertencentes a um ImageBooru, e ter seu id do mesmo
 * em algum lugar do nome de arquivo, de modo regular o bastante para que possa
 * ser extraido.
 * 
 * Um ImageDirectory deve fornecer uma lista de ImgFiles com os arquivos do
 * diretório que ele conseguiu interpretar, e métodos para renomear as imagens,
 * ou copia-las para outro diretório de saida.
 *
 * @author Guilherme
 * @created 03/03/2013
 * @since 2.3
 */
public abstract class AbstractImageDirectory {
    protected File dir;
        
    protected FileFilter filter =  new FileFilter() {
        @Override
        public boolean accept(File pathname) {
            if((pathname==null)||!pathname.isFile()) { return false; }
                return (getIdFromFileName(pathname.getName())>0)
                    && isImgExt(ImgFile.getExtension(pathname.getName()));
       }
    };
    
    /** 
     * @param ext String com uma extensão de arquivo.
     * @return true se ext = (png OR jpg OR jpeg OR gif).
     */
    protected boolean isImgExt(String ext) {
        return (ext!=null) && ( 
                (ext.equalsIgnoreCase(".png")) 
                || (ext.equalsIgnoreCase(".jpg")) 
                || (ext.equalsIgnoreCase(".jpeg")) 
                || (ext.equalsIgnoreCase(".gif")) );
    }
    
    private Object throwIfNull(Object o){
        if(o==null) { throw new NullPointerException("nada de nulls"); }
        return o;
    }
    
    public AbstractImageDirectory(File dir){
        throwIfNull(dir);
        if(!dir.isDirectory()) { throw new IllegalArgumentException("dir deve ser um diretório."); }
        this.dir = dir;
    }
    
    public int getPathLength(){
        return dir.getAbsolutePath().length();
    }
    
    public Collection<ImgFile> getAllImgFiles(){
        LinkedList<ImgFile> ret = new LinkedList<>();
        for(File f : dir.listFiles(filter)){
            ret.add( new ImgFile(
                    getIdFromFileName(f.getName()),
                    f));
        }
        return ret;
    };

    protected abstract int getIdFromFileName(String filename);
}
