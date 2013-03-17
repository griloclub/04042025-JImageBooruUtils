package imagebooru;

import java.util.Comparator;
import java.util.Objects;

/**
 * Modela uma representação genérica de uma TAG de um ImageBooru
 * qualquer. Armazena dados que são comuns em diversos ImageBooru(s), sendo:
 * <br/><br/>
 * 
 * <ul><li>
 * <tt>int id</tt> - ID da tag no ImageBooru de Origem. necessario para 
 * busca-la novamente no seu booru de Origem, e conveniente para detectar 
 * duplicadas.
 * </li><li>
 * <tt>String booruURL</tt> - URL do ImageBooru de origem desta tag.
 * </li><li>
 * <tt>String nome</tt> - Nome da Tag. pode conter espaços e caracteres ilegais
 * para nome de arquivo no Windows, mas estes serão removidos / substituidos no 
 * ato de sua utilização.
 * </li><li>
 * <tt>int count</tt> - Quantidade de posts no ImageBooru de origem que possuem
 * esta TAG. Usado especialmente para definir tags mais comuns ou "Maiores" que
 * outras quando a necessidade de escolher entre duas TAGs para gerar um nome de
 * arquivo que respeite o limite de 260 caracteres de path do Windows.
 * </li><li>
 * <tt>ImgTag alias</tt> - Tag de Alias desta Tag. Um Alias neste caso implica 
 * em uma tag equivalente pela qual esta deve ser substituida se necessario. 
 * Esta tag substituta é a tag preferencial em relação a esta no ato de sua 
 * utilização.<br/><br/>   
 * 
 * <p>nos métodos <tt>toString()</tt> e no construtor que recebe uma String,
 * a definição de alias é feita por recursão (toString() de ImgTag inclui
 * <tt>alias.toString()</tt>, sendo que alias é um ImgTag).</p>
 * </li><li>
 * <tt>TagType type</tt> - Tipo da Tag. Esta propiedade em particular é a mais
 * dificil de se precisar.<br/><br/> 
 * 
 * <p>A Implementação desta classe usa a conveção de 6 tipos e a implementa com o 
 * enumerado <tt>TagType</tt></p>
 * </li></ul><br/><br/>
 * 
 * A Classe tambem fornece 3 Implemetanções de Comparator padrão,
 * <tt>CompareById</tt>, <tt>CompareByNome</tt> e <tt>CompareByCount</tt>,
 * a Implementação de toString(), equals(), hashCode(), e varios construtores 
 * para conveniencia.
 *
 * @author Guilherme
 * @created 23/02/2013  
 * @see ImgTags
 * @see TagType
 * @since 2.1
 */
public final class ImgTag {
    private int id = -1, count = 0;
    private String booruURL, nome;
    private ImgTag alias = null;
    private TagType type = TagType.ttGeneral;
    
    /**
     * Lista os possiveis tipos de TAG.
     * ImageBooru(s) baseados em danbooru/moebooru possuem
     * 6 tipos de tag: General, Artist, Copyright, Caracters, Circle  e Faults.
     * Outros boorus como e-shuushuu não seguem esta conveção, apesar de ser valido
     * assumir que General, Artist e Caracter vão estar presentes em todos.<br/><br/>
     * 
     * TagType implementa os 6:
     * <ul><li>
     * General - Tags genéricas com cor de olhos, roupas, etc.
     * </li><li>
     * Artist - Artista(s) responsavel(eis) pela arte. ex. Misaki Kurehito ;)
     * </li><li>
     * Copyright - Anime/Manga/LightNovel/VisualNove/Game/AOT de Origem da 
     * imagem. Inclui, na maioria dos ImageBooru(s), <tt>Original</tt>, que
     * sinaliza que não há fonte. Em geral, Original implica em não haver 
     * nenhuma TAG de Caracter, mas isto não é uma regra.
     * </li><li>
     * Caracter - Personagem(s) que aparecem na imagem. Em geral, não existem
     * quando o Copyright é Original;
     * </li><li>
     * Circle - "Circulo" de artistas aplicavel. ex. Cradle, abhar, 
     * 5 nenme no houkago.
     * </li><li>
     * Faults - "Defeitos". Variam muito de um ImageBooru para outro, a ponto
     * de TAGs consideradas General em um ImageBooru serem Faults em outro.
     * ex. jpeg artifacts, crease, scanning dust.
     * </li></ul>
     * 
     * @since 2.1
     */
    public enum TagType {
         ttGeneral,
         ttArtist,
         ttCopyright,
         ttCaracter,
         ttCircle,
         ttFaults
    };
    
    /**
     * Retorna a precedencia do TagType fornecido.
     * Um valor maior valor significa uma precedencia maior.
     * 
     * @param t TagType cuja "precedencia" se quer obter.
     * @return precedencia de t com int.
     */
    public static int tagTypeToInt(TagType t){
        if(t==null) { return -1; }
        //GENERAL
        if(t.equals(TagType.ttGeneral)){
            return 0;
        //ARTIST
        }else if(t.equals(TagType.ttArtist)){
            return 5;
        //COPYRIGHT
        }else if(t.equals(TagType.ttCopyright)){
            return 4;
        //CARACTER
        }else if(t.equals(TagType.ttCaracter)){
            return 3;
        //CIRCLE
        }else if(t.equals(TagType.ttCircle)){
            return 2;
        //FAULTS
        }else if(t.equals(TagType.ttFaults)){
            return 1;
        //NONE
        }else { throw new IllegalArgumentException(); }
    }    
    
    //Comparators e Dummys.
    
    public static final Comparator CompareById = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            return ((ImgTag)o1).getId() - ((ImgTag)o2).getId();
        }
    };    
    /**
     * Obtem um ImgTag "Dummy" com o id especificado.
     * O ImgTag retornado não referencia uma TAG "valida", tem tipo General,
     * e count <tt>Integer.MAX_VALUE</tt>.
     * 
     * @param id O id desejado
     * @return ImgTag com um Dummy de Id especificado.
     */
    public static ImgTag getDummyWithId(int id){
        return new ImgTag(id,
                "http://dummy",
                "dummy",
                Integer.MAX_VALUE,
                TagType.ttGeneral,
                null);
    }
    
    public static final Comparator CompareByNome = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            return ((ImgTag)o1).getNome().compareTo(((ImgTag)o2).getNome());
        }
    }; 
    /**
     * Obtem um ImgTag "Dummy" com o nome especificado.
     * O ImgTag retornado não referencia uma TAG "valida", tem tipo General,
     * count e id <tt>Integer.MAX_VALUE</tt>.
     *
     * @param nome nome desejado.
     * @return ImgTag com um Dummy de Nome especificado.
     */
    public static ImgTag getDummyWithNome(String nome){
        return new ImgTag(Integer.MAX_VALUE,
                "http://dummy",
                nome,
                Integer.MAX_VALUE,
                TagType.ttGeneral,
                null);
    }
    
    public static final Comparator CompareByCount = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            return ((ImgTag)o1).getCount() - ((ImgTag)o2).getCount();
        }
    };
    /**
     * Obtem um ImgTag "Dummy" com o count especificado.
     * O ImgTag retornado não referencia uma TAG "valida", tem tipo General,
     * e id <tt>Integer.MAX_VALUE</tt>.
     *
     * @param count count desejado.
     * @return ImgTag com um Dummy de Count especificado.
     */
    public static ImgTag getDummyWithCount(int count){
        return new ImgTag(Integer.MAX_VALUE,
                "http://dummy",
                "dummy",
                count,
                TagType.ttGeneral,
                null);
    }
    
    /**
     * Compara dois Objetos ImgTag.
     * Compara o conteudo dos campos individualmente, um a um.
     * 
     * @param o Objeto a ser comparado com este.
     * @return true se o foi um ImgTag e igual a este objeto.
     */
    @Override
    public boolean equals(Object o){
        if( (o==null) || !(o instanceof ImgTag) ) { return false; }
        ImgTag t = (ImgTag)o;//para conveniencia
        return (t.getId() == getId()) 
                && (t.getBooruURL().equals(getBooruURL())) 
                && (t.getNome().equals(getNome())) 
                && (t.getCount()==getCount()) 
                && ( 
                        ((t.getAlias()==null)&&(getAlias()==null)) 
                        || t.getAlias().equals(getAlias()) 
                )
                && (t.getType().equals(getType()));
    }

    @Override
    public int hashCode() {
        int hash = 5;
        hash = 79 * hash + this.getId();
        hash = 79 * hash + this.getCount();
        hash = 79 * hash + Objects.hashCode(this.getBooruURL());
        hash = 79 * hash + Objects.hashCode(this.getNome());
        hash = 79 * hash + Objects.hashCode(this.getAlias());
        hash = 79 * hash + (this.getType() != null ? this.getType().hashCode() : 0);
        return hash;
    }
    
    /**
     * Retorna a representação em String desta TAG.
     * representação ideal para ser salva em arquivo texto, mas pouco desejavel
     * para nomes de arquivo. Para nomes de Arquivo, usar getNome(). 
     * 
     * O Campo alias é um ImgTag dentro de outro e é definido recursivamente, 
     * até que o alias seja <tt>null</tt>
     * 
     * @return String com os campos do Objeto em sequencia e tabulados.
     */
    @Override
    public String toString(){
        if(this==null) { return "null"; } //Faz sentido?
        return String.format("{%d\t%s\t%s\t%d\t%s\t%s}", getId(), getBooruURL(), getNome(), getCount(), getType(), getAlias());
    }
    
    /*Construtores:*/
    
    private void LoadFrom(ImgTag source){
        assert source!=null;
        setId(source.getId());
        setBooruURL(source.getBooruURL());
        setNome(source.getNome());
        setCount(source.getCount());
        setType(source.getType());
        setAlias(source.getAlias());
    }
    
    /**
     * Constroi uma instancia de ImgTag apartir de uma representação em
     * String (obtida com toString()).
     * rebece uma string compativel com a obtida do toString() de um ImgTag e 
     * constroi o objeto apartir dela. gera exceção se não for possivel, e valida
     * todos os campos da string recebida com seus respectivos métodos seters.
     * 
     * @param reg string no formato de ImgTag.toString();
     */
    public ImgTag(String reg){
        String[] fields = reg.substring(1, reg.length()-1).split("\t",6);
        if(fields.length != 6) { throw new IllegalArgumentException(); }
        //TODO: Substituir atribuições por chamadas a setX();
        setId(fields[0]);
        setBooruURL(fields[1]);
        setNome(fields[2]);
        setCount(fields[3]);
        setType(fields[4]);
        setAlias( (fields[5].equals("null")) ? null : fields[5] );//Repete o processo acima (se diferente de "null");
    }
    
    /**
     * Constroi uma ImgTag apartir dos dados de outra.
     * Copia os valores dos campos da ImgTag passada, através de seus 
     * respectivos getters. a ImgTag resultante é uma cópia de <tt>source</tt>,
     * e alterações feitas sobre ela não afetam source em qualquer sentido.
     * Se null, leva NullPointerException.
     * 
     * @param source ImgTag a ser copiada.
     */
    public ImgTag(ImgTag source){
        throwIfNull(source);
        LoadFrom(source);
    }
    
    /**
     * Constroi uma ImgTag apartir de valores para cada um de seus 
     * campos.
     * Os valores recebidos serão validados por seus repectivos setters.
     * Garante que não será criada nenhuma ImgTag Inválida. O unico campo sem
     * nenhuma restrição é alias, que pode ser null, indicando ausencia de um
     * alias para esta TAG.
     * 
     * @param id int com o id da tag no ImageBooru de Origem.
     * @param booruURL String com a URL do ImageBooru de Origem.
     * @param nome String com o nome da Tag.
     * @param count quantidade de posts com esta tag no ImageBooru de Origem.
     * @param type Tipo da tag. veja {@link TagType TagType}
     * @param alias Outra ImgTag que serve de alias para esta. Pode ser null.
     */
    public ImgTag(int id, String booruURL, String nome, int count, 
            TagType type, ImgTag alias){
        setId(id);
        setBooruURL(booruURL);
        setNome(nome);
        setCount(count);
        setType(type);
        setAlias(alias);
    }

    /*INFERNO DE SETERS E GETTERS!!! MALDITOS, USEM PROPERTIES!!!*/
    
    private int throwIfZeroOrNeg(int v){
        if(v<=0) { throw new IllegalArgumentException("deve ser maior que 0."); }
        return v;
    }    
    private int intFromString(String s){
        return Integer.parseInt( s.replaceAll("[^0-9]*","") );
    }    
    private Object throwIfNull(Object o){
        if(o==null) { throw new NullPointerException(); }
        return o;
    }
    
    /**
     * @return the id
     */
    public int getId() {
        return id;
    }

    /**
     * Seta o Id apartir de um int. 
     * Se valor menor/igual 0, leva exceção.
     * 
     * @param id id a ser atribuido.
     */
    public void setId(int id) {
        this.id = throwIfZeroOrNeg(id);
    }
    
    /**
     * Seta o Id apartir de uma String.
     * nulls, leva <tt>NullPointerException</tt>.
     * Caracters não-numéricos serão removidos, cuidado.
     * Nenhum caracter numérico, leva <tt>NumberFormatException</tt>.
     * 
     * @param id string contendo o id.
     */
    public void setId(String id){
        this.id = throwIfZeroOrNeg(intFromString(id));
    }

    /**
     * @return the count
     */
    public int getCount() {
        return count;
    }

    /**
     * Seta o count apartir de um int.
     * Se valor menor/igual 0, leva exceção.
     * 
     * @param count count a ser atribuido.
     */
    public void setCount(int count) {
        this.count = throwIfZeroOrNeg(count);
    }
    
    /**
     * Seta o count apartir de uma String.
     * nulls, leva <tt>NullPointerException</tt>.
     * Caracters não-numéricos serão removidos, cuidado.
     * Nenhum caracter numérico, leva <tt>NumberFormatException</tt>.
     * 
     * @param count String com o novo count.
     */
    public void setCount(String count){
        this.count = throwIfZeroOrNeg(intFromString(count));
    }

    /** 
     * @return the booruURL.
     */
    public String getBooruURL() {
        return booruURL;
    }

    /**
     * Seta a URL do ImageBooru de origem.
     * se null, leva <tt>NullPointerException</tt>.
     * 
     * @param booruURL the booruURL to set
     */
    public void setBooruURL(String booruURL) {
        this.booruURL = throwIfNull(booruURL).toString();
    }

    /**
     * @return the nome
     */
    public String getNome() {
        return nome;
    }

    /**
     * Seta o nome da Tag.
     * se null, leva <tt>NullPointerException</tt>.
     * 
     * @param nome the nome to set
     */
    public void setNome(String nome) {
        this.nome = throwIfNull(nome).toString();
    }

    /**
     * @return uma cópia do alias desta ImgTag.
     */
    public ImgTag getAlias() {
        return (alias!=null) ? new ImgTag(alias) : null;
    }

    /**
     * Seta o alias desta ImgTag.
     * aceita nulls, pois é perfeitamente valido que um ImgTag não tenha alias.
     * o Alias fornecido será copiado para o conteudo do campo (se não for null).
     * 
     * @param alias the alias to set
     */
    public void setAlias(ImgTag alias) {
        this.alias = (alias!=null) ? new ImgTag(alias) : null;
    }
    
    /**
     * Seta o alias apartir de uma representação em String.
     * Aceita null na string, neste caso será atribuido null ao alias.
     * String não-nula porem inválida, leva exceção.
     * 
     * @param alias String contendo a representação do alias a ser atribuido.
     */
    public void setAlias(String alias){
        this.alias = (alias!=null) ? new ImgTag(alias) : null; //denovo? :)
    }

    /**
     * @return the type
     */
    public TagType getType() {
        return type;
    }

    /**
     * Seta o tipo de TAG deste ImgTag.
     * Se nulls, leva NullPointerException.
     * 
     * @param type o tipo de TAG a ser atribuido.
     * @see TagType
     */
    public void setType(TagType type) {        
        this.type = (TagType)throwIfNull(type);
    }
    
    /**
     * Seta o tipo de ImgTag apartir de uma String.
     * o conteudo dessa string deve ser igual ao de um dos elementos
     * do enumerado {@link TagType TagType}. se não for, leva exceção.
     * Se nulls, leva NullPointerException.
     * 
     * @param type String com o type a ser atribuido.
     * @see TagType
     */
    public void setType(String type) {
        this.type = TagType.valueOf( throwIfNull(type).toString() );               
    }    
}
