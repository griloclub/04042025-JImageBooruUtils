package imagebooru;

import java.io.File;
import java.util.Objects;

/**
 * Modela um arquivo de imagem, com id e nome de arquivo.
 * Abstração de um arquivo de imagem pertencente a um ImageBooru, que possui
 * seu id "localizavel" no seu nome de arquivo.
 *
 * @author Guilherme
 * @created 03/03/2013
 * @since 2.3
 */
public final class ImgFile {
    private File file;
    private int id;
    
    private Object throwIfNull(Object o){
        if(o==null) { throw new NullPointerException("Nada de nulls!"); }
        return o;
    }
    
    private int throwIfZeroOrNeg(int v){
        if(v<=0) { throw new IllegalArgumentException("Maior que 0, por favor."); }
        return v;
    }
    
    private String throwIfNotAName(String n){
        if(n.matches(IllegalCaractersRegex)) { //Tambem elimina diretórios e paths.
            throw new IllegalArgumentException("O Nome contem um ou mais caracteres Ilegais"); 
        }
        return n;
    }
    
    /*
    private String throwIfNotADir(String d){
        if(!new File(d).isDirectory()) { throw new IllegalArgumentException("Dir deve ser um diretório valido e existir"); }
        return d;
    }*/
    
    //Utilidades Estáticas:
    
    /**
     * Regex para identificar caracteres ilegais em nomes de Arquivos Windows.
     * Leia como: (\ ou / ou * ou ? ou " ou &gt ou &lt ou : ou |).
     */
    public final static String IllegalCaractersRegex = "\\\\|/|\\*|\\?|\"|\\<|\\>|\\:|\\|";
    
    /**
     * Verifica se a String fornecida tem algum caracter ilegal.
     * O faz via P.O.G. comparando a própia String com sua versão "legal".
     * 
     * @param s string a ser testada.
     * @return true se ela conter algum caracter ilegal.
     */
    public static boolean hasIllegalChs(String s){
        return !s.equals(s.replaceAll(IllegalCaractersRegex, ""));
    }
    
    /**
     * Retorna a Extensão de Arquivo de f.
     * 
     * @param f Nome de Arquivo do qual se quer obter a extensão.
     * @return String com Extensão de f.
     */
    public static String getExtension(String f){
        int pos = f.lastIndexOf(".");
        return (pos>-1) ? f.substring( pos ) : "";
    }
    
    /**
     * Retorna o nome de arquivo f sem sua extensão.
     * 
     * @param f Nome de Arquivo do qual se quer "remover" a extensão.
     * @return f sem extensão.
     */
    public static String withoutExtension(String f){
        int pos = f.lastIndexOf(".");
        return (pos<=-1) ? 
                f : (f.substring(pos).length()<=5) ? 
                    f.substring(0,pos) : f;
    }
    
    //Overrides de Object:
    
    /**
     * Retorna uma representação String deste ImgFile.
     * Pode ser usada para persistir este ImgFile em Arquivo, pois contem infor_
     * mação sufiente para reconstrui-lo apartir dela;
     * Formato: [id] \t [file.getAbsolutePath()]
     * 
     * @return "id \t filename".
     */
    @Override
    public String toString(){
        return id+"\t"+file.getAbsolutePath();
    }
    
    @Override
    public boolean equals(Object o){
        ImgFile f;
        if(o instanceof ImgFile) { 
            f = (ImgFile)o; 
        }
        else {
            return false; 
        }
        return (this.id == f.id)&&(this.file.equals(f.file));
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 11 * hash + Objects.hashCode(this.file);
        hash = 11 * hash + this.id;
        return hash;
    }
    
    //Métodos Basicos:
    
    /**
     * Renomeia este arquivo.
     * Não considera o nome recebido como um caminho de arquivo, isto é, não ira
     * mover o arquivo mesmo que seja fornecido um diretório.<br/><br/>
     * 
     * Se O nome fornecido for inválido, conter um <tt>Path</tt> ou Caracteres
     * Illegais em nome de arquivo, leva IllegalArgumentException.
     * 
     * @param novoNome novo nome do arquivo.
     * @return true se renomear.
     */
    public boolean rename(String novoNome){
        throwIfNull(novoNome);
        File nf = new File( file.getParent() + File.separator 
                + withoutExtension( throwIfNotAName(novoNome) ) 
                + getExtension(file.getName())  );
        return file.renameTo(nf);
    }
    
    //Construtores Utilitários:
    
    /**
     * Construtor apartir de id e Arquivo.
     * 
     * @param id id da imagem em seu respectivo booru.
     * @param file Arquivo da Imagem.
     */
    public ImgFile(int id, File file){
        setId(id);
        setFilename(file);
    }
    
    /**
     * Construtor apartir de id e String.
     * Cria um novo arquivo com a string passada.
     * 
     * @param id id da imagem em seu respectivo booru.
     * @param filename nome do arquivo da imagem. É melhor que exista.
     */
    public ImgFile(int id, String filename){
        setId(id);
        setFilename(new File(filename));
    }
    
    /*INFERNO DE SETS E GETS!!!*/

    /**
     * @return path absoluto do arquivo fisico que este ImgFile representa.
     */
    public String getFilename() {
        return file.getAbsolutePath();
    }

    /**
     * Seta o Arquivo de Imagem deste ImgFile.
     * Não aceita nulls, e o arquivo DEVE existir e DEVE ser legivel.
     * 
     * @param file the file to set
     */
    private void setFilename(File file) {
        throwIfNull(file);
        if(!file.canRead()) { throw new IllegalArgumentException("File deve Existir e ser legivel!"); }
        this.file = file;
    }

    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * @param id the id to set
     */
    private void setId(int id) {
        this.id = throwIfZeroOrNeg(id);
    }
    
}
