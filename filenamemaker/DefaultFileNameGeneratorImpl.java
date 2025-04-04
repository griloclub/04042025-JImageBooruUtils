package imagebooru.filenamemaker;

import imagebooru.ImgFile;
import imagebooru.ImgTag;
import imagebooru.ImgTags;
import java.util.Comparator;
import java.util.regex.Pattern;
import sun.misc.Regexp;


/**
 * Implementação padrão para AbstractFileNameGenerator, escolhe tags
 * pelo tipo e pelo <tt>count</tt> das mesmas.
 * Implementação simples de AbstractFileNameGenerator, ordena as tags recebidas
 * pelo tipo e por <tt>count</tt>, depois elimina tags do fim da lista até que
 * seja satisfeito o limite de caracteres do nome de arquivo.<br/><br/>
 * 
 * Alem da funcionalidade acima, Substitui todas as tags por seus alias, 
 * adiciona <tt>"tagme"</tt> caso não seja passada nenhuma tag, e adiciona 
 * <tt>"see source"</tt> caso tenha que remover uma ou mais tags.<br/><br/>
 * 
 * Fornece um membro protegido prefIdSep, que pode ser alterado em descendentes 
 * que precisem customiza-lo.<br/>
 * Em um nome de arquivo, sua posição seria: <br/><tt>
 * "prefix<b>prefIdSep</b>id tag1 tag2 tag3".</tt>
 * 
 * @author Guilherme
 * @created 02/03/2013
 * @since 2.1
 */
public class DefaultFileNameGeneratorImpl extends AbstractFileNameGenerator {
    
    protected String prefIdSep = " - ";
    protected int curId;
    
    private boolean has_SeeSource;
    
    protected static final Comparator CompareTags = new Comparator() {
        @Override
        public int compare(Object o1, Object o2) {
            //Valida o tipo dos parametros.
            if( !(o1 instanceof ImgTag) || !(o2 instanceof ImgTag) ) {
                throw new IllegalArgumentException("o1 e o2 devem ser ImgTag.");
            }
            //Auxiliares:
            ImgTag t1 = (ImgTag)o1, t2 = (ImgTag)o2;
            /*Se qualquer uma for see_source, considera-as iguais.
             * isso garante que see_source nunca será removido
             */
            if(t1.equals(see_source)||t2.equals(see_source)) { return 0; }
            //Compara por tipo.
            int ret = ImgTag.tagTypeToInt( t1.getType() ) 
                    - ImgTag.tagTypeToInt( t2.getType() );
            //Se forem do mesmo tipo, compara por count.
            return (ret!=0) ? ret : t1.getCount()-t2.getCount();
        }
    };
    
    /**
     * Resolve uma ImgTag até seu ultimo alias.
     * Basicamente, substitui a tag por seu alias até achar uma tag sem alias. 
     * 
     * @param tag tag a ser resolvida.
     * @return ultimo alias de tag, ou a própia tag se não houver alias.
     */
    private ImgTag dereferenceAlias(ImgTag tag){
        while( tag.getAlias()!=null ) { tag=tag.getAlias(); }
        return tag;
    }
    
    /**
     * Resolve todas as tags de uma lista de tags.
     * Troca todos os elementos de uma lista por seus respectivos alias.
     * 
     * @param tags lista de tags a ser alterada.
     * @return a própia lista fornecida.
     */
    private ImgTags dereferenceAllAlias(ImgTags tags){
        for(int i=0; i<tags.size(); i++){
            tags.set( i, dereferenceAlias(tags.get(i)) );
        }
        return tags;
    }
    
    /**
     * Adiciona see_source a lista de tags fornecida.
     * Usa <tt>has_SeeSource</tt> para não ter que realizar uma custosa busca
     * linear sempre que for chamado.
     * 
     * @param tags lista de tags que deve conter "see_source".
     */
    private void shouldSeeSource(ImgTags tags){
        if(has_SeeSource){ return; }
        tags.add(see_source);
        has_SeeSource=true;
    }
    
    /**
     * Remove qualquer tag que tenha caracteres ilegais no nome.
     * Usa <tt>shouldSeeSource()</tt> para garantir que haja see_source caso
     * remova alguma tag.
     * 
     * @param tags lista a ser verificada.
     * @return lista sem tags ilegais.
     */
    private ImgTags removeIllegalTags(ImgTags tags){
        ImgTag aux;
        for(int i=0; i<tags.size(); i++){
            aux=tags.get(i);
            //Se não tem chs Ilegais, ignora.
            if( !ImgFile.hasIllegalChs(aux.getNome()) ) { continue; }
            //Se tem remove.
            tags.remove(i);
            shouldSeeSource(tags);
        }
        return tags;
    }
    
    /** 
     * @return numero de caracteres do nome de arquivo sem tags e com ext.
     */
    private int calcBaseFilename(){
        return (getPrefix()+prefIdSep+curId+" ").length() +5;//.jpeg
    }
    
    /**
     * Calcula o tamanho que o nome de arquivo final teria, incluindo
     * extensão de arquivo.
     * Assume sempre extensão .jpeg, por ser a maior em numero de chs.
     * 
     * @param tags lista de tags para o nome de arquivo.
     * @return numero de caracteres do nome de arquivo resultante.
     */
    private int calcFileNameFor(ImgTags tags){
        return calcBaseFilename() + tags.toString().length();
    }
    
    /**
     * Retorna o Indice da tag de "Menor valor" entre as de tags.
     * Maior Vilão desta implementação, O(n). Melhore-o se possivel.
     * 
     * @param tags lista de tags a ser pesquisada.
     * @return indice da tag de menor precedencia.
     */
    private int indexOfMin(ImgTags tags){
        int ret = 0;
        ImgTag ret_t = tags.get(0), t;
        for(int i=1; i<tags.size(); i++){
            t=tags.get(i);
            if( CompareTags.compare(ret_t,t)>0 ){
                ret_t = t;
                ret = i;
            }
        }
        return ret;
    }
    
    /**
     * Remove tags de src até que seja satisfeito o tamanho maximo de
     * nome de arquivo.
     * Se uma ou mais tags forem removidas, adiciona a irremovivel "see_source"
     * Deve ser a forma mais ineficiente de fazer isto, mas funciona.
     * 
     * @param src lista de tags a ser filtrada
     * @return uma cópia da lista fornecida, já filtrada.
     */
    protected ImgTags filterTags(ImgTags src){
        //Trabalha com uma cópia de src.
        src = new ImgTags(src);
        //Define se tem SeeSource:
        has_SeeSource = src.indexOf(see_source) > -1;
        //Alias
        src=dereferenceAllAlias(src);
        //Tags Ilegais:
        src=removeIllegalTags(src);
        //Loop de redução:        
        while( calcFileNameFor(src) > maxFilenameLength ){
            src.remove( indexOfMin(src) );
            shouldSeeSource(src);
        }
        //retorna:
        return src;
    }
    
    /**
     * Me chame se receber uma lista de tags vazia.
     * 
     * @return um lista contendo "tagme".
     */
    protected ImgTags noTags(){
        ImgTags ret = new ImgTags();
        ret.add(tagme);
        return ret;
    }

    /**
     * Constroi um DefaultFileNameGeneratorImpl apartir de prefix.
     * Nada especial aqui, apenas repassa o prefixo para o construtor da super
     * classe. veja <tt>{@link AbstractFileNameGenerator#AbstractFileNameGenerator(java.lang.String) AbstractFileNameGenerator}</tt>.
     *
     * @param prefix prefixo a ser usado.
     */
    public DefaultFileNameGeneratorImpl(String prefix) {
        super(prefix);
    }

    private int throwIfZeroOrNeg(int id){
        if(id<=0) { throw new IllegalArgumentException("id deve ser maior ou igual a 0"); }
        return id;
    }

    private Object throwIfNull(Object tags) {
        if(tags==null) { throw new NullPointerException("tags deve ser diferente de null"); }
        return tags;
    }
    
    /**
     * Gera um nome de arquivo dados um id e uma lista de Tags.
     * Formata uma String com os nomes das tags fornecidas, ou seus alias quando
     * presentes. <p>
     * Se a lista de tags estiver vazia, adiciona a tag default "tagme".
     * </p><p>
     * Caso o nome resultante seja maior que o maximo configurado, adiciona a
     * tag default "see source" para sinalizar corte de tags, e elimina tags até
     * que seja satisfeito o numero maximo de caracteres.
     * </p><p>
     * As tags são removidas por ordem de tipo, e por menor "count":<br/>
     * Artist > Copyright > Circle > Caracter > Faults > General; <br/>
     * Maior Count > Menor Count.
     * </p>
     * @param id id da Imagem no ImageBooru de Origem.
     * @param tags Lista de Tags da imagem
     * @return String com o nome de arquivo, formato MoeBooru.
     */
    @Override
    public String generateAsString(int id, ImgTags tags) {
        curId = throwIfZeroOrNeg(id);
        throwIfNull(tags);
        tags = (tags.size()>0) ? filterTags(tags) : noTags();
        return String.format("%s%s%d %s", getPrefix(), prefIdSep, curId, tags);
    }

    /**
     * Gera um nome de arquivo dados um id e uma lista de Tags.
     * Emprega a mesma lógica de {@link DefaultFileNameGeneratorImpl#generateAsString(int, imagebooru.ImgTags) generateAsString(int imagebooru.ImgTags)}
     * mas não retorna como String, e sim como outra lista de tags.
     *
     * @param id id da Imagem no ImageBooru de Origem. necessario para contagem.
     * @param tags lista de tags da imagem.
     * @return nova Lista de Tags, filtrada para satisfazer o maximo de chs.
     * @see imagebooru.filenamemaker.DefaultFileNameGeneratorImpl.generateAsString()
     */
    @Override
    public ImgTags generateAsTags(int id, ImgTags tags) {
        curId = throwIfZeroOrNeg(id);
        throwIfNull(tags);
        return (tags.size()>0) ? filterTags(tags) : noTags();
    }
    
}
