package imagebooru;

import java.util.ArrayList;
import java.util.Collection;

/** 
 * Uma Lista de ImgTag, feita apartir de ArrayList.
 * Sobrescreve todos os métodos para adição de elementos para evitar que sejam 
 * inseridos nulls. Não valida as tags enquanto tags.
 * 
 * Sua principal função é fornecer um toString() seguro.
 * 
 * @author Guilherme
 * @see ImgTag
 * @created 24/02/2013
 * @since 2.1
 */
public class ImgTags extends ArrayList<ImgTag> {    
    
    //To String e auxiliares, unico motivo desta existencia.
    
    private String semChsIlegais(String s){
        //Leia essa @#~!* como (\ ou / ou * ou ? ou " ou < ou > ou |).
        return s.replaceAll(ImgFile.IllegalCaractersRegex,"");
    }
    
    private String safeTag(String tn){
        return semChsIlegais(tn.trim().replaceAll(" ", "_"));
    }
    
    /**
     * Retorna a representação padrão para uma lista de tags em nomes 
     * de arquivo. Os nomes das tags contidas são tratadas para substituir 
     * quaisquer caracteres não aceitos por nomes de arquivo no windows, e 
     * substitui espaços dentro do nome da tag por "_" para que possam ser 
     * diferenciadas umas das outras. Entretando, não é feita qualquer 
     * checagem do numero de caracteres da string final.
     * 
     * @return representação em string de todas as tags contidas neste objeto.  
     */
    @Override
    public String toString(){
        StringBuilder ret = new StringBuilder();
        for(ImgTag t : this){
            ret.append( safeTag(t.getNome()) ).append(" ");
        }
        return (ret.length()>0) ? ret.substring(0, ret.length()-1) : "";
    }
    
    private void throwIfNull(Object o){
        if(o==null) { throw new NullPointerException(); }
    }
    
    private void throwIfHasNull(Collection c){
        if(c.contains(null)) { 
            throw new NullPointerException("c nao deve conter nulls"); 
        }
    }
    
    //Construtores Compilantes com ArrayList<>    
    
    /**
     * Construtor vazio, Incializa o array de ArrayList com 
     * 15 elementos. Só isso mesmo. 
     */
    public ImgTags(){ super(15); }
    
    /**
     * Construtor que recebe uma colecao de ImgTag. Não aceita nulls
     * na colecao recebida.
     * 
     * @param c Collection qualquer a ser "copiada". Não deve conter nulls.
     */
    public ImgTags(Collection<? extends ImgTag> c){
        super();
        if(c==null) { return; }
        throwIfHasNull(c);
        super.addAll(c);
    }
    
    //Overrides para não permitir nulls.
    
    @Override
    public boolean add(ImgTag i){
        throwIfNull(i);
        super.add(i);
        return true;
    }
    
    @Override
    public void add(int index, ImgTag i){
        throwIfNull(i);
        super.add(index, i);
    }
    
    @Override
    public boolean addAll(Collection<? extends ImgTag> c){
        if(c==null) { return true; }
        throwIfHasNull(c);
        super.addAll(c);
        return true;
    }
    
    @Override
    public boolean addAll(int index, Collection<? extends ImgTag> c){
        if(c==null) { return true; }
        throwIfHasNull(c);
        super.addAll(index, c);
        return true;
    }
    
    @Override
    public ImgTag set(int index, ImgTag i){
        throwIfNull(i);
        return super.set(index, i);
    }
}
