package imagebooru.postparser;

import imagebooru.ImgTag;
import imagebooru.ImgTags;
import imagebooru.utils.ProgressListener;
import imagebooru.utils.StubProgressListener;
import java.util.Collection;

/**
 * Modela um "Parser" para Imagens postadas em um ImageBooru.
 * Um PostParser deve ser capaz de obter uma lista de tags associadas a um post,
 * dado o seu id. 
 * 
 * A URL na qual o post deve ser pesquisado deve ser fornecida no construtor do 
 * objeto, e será fixa pelo tempo de vida do mesmo. A Implementaçaõ é quem deve
 * definir como formar a URL final para o post.
 * 
 * As tags devem ser formadas pelo mesmo processo. Ao inspecionar um post, os
 * IDs das tags devem ser obtidos, e suas informações detalhadas obtidas apartir
 * deles. 
 * 
 * Para maior otimização, devido ao alto tempo levado para obter info detalhada 
 * de uma TAG, o PostParser deve guardar um cache com as TAGS já pesquisadas, 
 * fornecer métodos para obter uma cópia este cache, métodos para carregar este 
 * cache de uma fonte externa e empregar alguma lógica para que o mesmo não 
 * fique muito desatualizado em relação ao ImageBooru.
 *
 * @author Guilherme
 * @created 23/02/2013
 * @since 2.0
 */
public abstract class AbstractPostParser {
    protected String baseURL;
    protected ProgressListener log_progress = new StubProgressListener();
    
    private String throwIfInvalidURL(String u){
        if(u==null) { throw new NullPointerException(); }
        if( !(u.startsWith("http://")||u.startsWith("https://")) ){
            throw new IllegalArgumentException();
        }
        return u;
    }
    
    /**
     * Constroi um novo AbstractPostParser.
     * Construtor que recebe a URL de base do ImageBooru do qual serão 
     * buscados os posts. realiza uma validação simples na URL recebida.
     * 
     * @param baseURL String com a URL principal do ImageBooru Alvo.
     */
    public AbstractPostParser(String baseURL){        
        this.baseURL = throwIfInvalidURL(baseURL);
    }
    
    /**
     * Seta o ProgressListener deste PostParser.
     *
     * @param pl novo progressListener. Se null, usado stub por conviniencia.
     */
    public void setProgressListener(ProgressListener pl){
        this.log_progress=pl;
        if(log_progress==null) { log_progress = new StubProgressListener(); }
    }
    
    public abstract ImgTags fetchAllTagsById(int id);
    public abstract ImgTags fetchAllTagsByMD5(String md5);
    public abstract ImgTags getAllTagsInfo();
    public abstract void addAllToTagInfo(Collection<ImgTag> c);
}
