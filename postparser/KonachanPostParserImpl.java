package imagebooru.postparser;

import contnrutils.tree.AVLTreeExtended;
import imagebooru.ImgTag;
import imagebooru.ImgTags;
import java.util.Collection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

/**
 * Implementação de AbstractPostParser para scrapping de 
 * http://konachan.com .
 * Usa Jsoup para scrapping. Obtem as tags de um post, e caso determinada tag 
 * não exista no cache interno, sera adicionada.<br/><br/>
 * 
 * Diferentemente do e-shuushuu, a info das tags no konachan.com esta disponivel
 * na própia pagina do post, ou seja, não há necessidade de uma consulta indivi_
 * dual para cada tag. Assim, o cache sempre é atualizado quando uma tag é en_
 * contrada, e é mantido basicamente para possibilitar aliases se necessario.
 * 
 * Ainda diferentemente de e-shuushuu, as tags não possuem um id identificavel
 * externamente. Por isso, foi definido que todas as tags konachan.com terão o
 * mesmo id.
 * 
 * @author Guilherme
 * @create 10/03/2013
 * @since 2.4
 */
public class KonachanPostParserImpl extends AbstractPostParser {
    
    public static int konachanDefaultTagID = 23232;    
    
    private AVLTreeExtended tagInfo = new AVLTreeExtended(ImgTag.CompareByNome);
    private int cur_retry = 0;//contador para tentativas.
        
    private int throwIfZeroOrNeg(int v){
        if(v<=0) { throw new IllegalArgumentException("Deve ser maior que 0"); }
        return v;
    }
    
    private ImgTag addToTagInfo(ImgTag t){
        //Busca a tag que deve adicionar.
        ImgTag i = (ImgTag)tagInfo.get(t);
        //se não existe, adiciona e retorna.
        if(i==null){
            tagInfo.add(t);
            return t;
        } else { //se existe, atualiza tipo e count, e retorna.
            i.setCount( t.getCount() );
            i.setType( t.getType() );
            return i;
        }
    }
    
    private ImgTag.TagType getTypeFromTex(String t){
        if( t.equalsIgnoreCase("artist") ) { return ImgTag.TagType.ttArtist; }
        else if( t.equalsIgnoreCase("circle") ) { return ImgTag.TagType.ttCircle; }
        else if( t.equalsIgnoreCase("copyright") ) { return ImgTag.TagType.ttCopyright; }
        else if( t.equalsIgnoreCase("character") ) { return ImgTag.TagType.ttCaracter; }
        else if( t.equalsIgnoreCase("style") ) { return ImgTag.TagType.ttFaults; }
        else if( t.equalsIgnoreCase("general") ) { return ImgTag.TagType.ttGeneral; }
        else { throw new IllegalArgumentException("Texto inválido para TagType"); }
    }
    
    private ImgTag getTagFromElement(Element e){
        return addToTagInfo(
                new ImgTag(
                    konachanDefaultTagID,
                    baseURL,
                    e.attr("data-name"),
                    Integer.parseInt( e.select("span").text() ),
                    getTypeFromTex(e.attr("data-type")),
                    null)
        );
    }
    
    private ImgTags getTags(Document doc){
        ImgTags ret = new ImgTags();
        Elements ele = doc.select("#tag-sidebar").select("li");
        for(Element e : ele){ ret.add(getTagFromElement(e)); }
        return ret;
    }

    /**
     * Retorna a lista de tags de um post dado seu id.
     * Tenta a conexão até 5 vezes, com 3segs de intervalo entre elas.
     * Não realiza nenhuma consulta extra para obter info detalhada das tags.
     * 
     * @param id id do post. se menor ou igual a 0, leva exceção.
     * @return lista de tags atualizadas do post.
     */
    @Override
    public ImgTags fetchAllTagsById(int id) {
        //Pré-Condição:
        throwIfZeroOrNeg(id);
        //Prepara o retorno:
        ImgTags ret = new ImgTags();
        try {
            //Conecta.
            log_progress.doNotifyProgress("Conectando...");
            Document doc = Jsoup.connect(baseURL+"/post/show/"+id).get();
            log_progress.doNotifyProgress("Conectado.");
            //Busca tags.
            ret.addAll( getTags(doc) );
            //Fim.
            log_progress.doNotifyProgress("Completo.");
        } catch (Exception ex) {
            //Se erros, tenta novamente até 5 vezes.
            if(cur_retry++ < 5) { 
                log_progress.doNotifyProgress("Falha: "+ex.getMessage()+", Tentando denovo em 3 segs.");
                try { Thread.sleep(3000); } catch (InterruptedException ex1) { }
                return fetchAllTagsById(id); 
            }
        }
        cur_retry=0;//reseta contador de tentativas.
        return ret;
    }

    @Override
    public ImgTags fetchAllTagsByMD5(String md5) {
        throw new UnsupportedOperationException("Not supported yet.");
    }

    @Override
    public ImgTags getAllTagsInfo() {
        ImgTags ret = new ImgTags();
        for( Object o : tagInfo.toArray() ){
            if(!(o instanceof ImgTag)) { continue; }
            ret.add((ImgTag)o);
        }
        return ret;
    }

    @Override
    public void addAllToTagInfo(Collection<ImgTag> c) {
        //AvlExtended Ignora duplicadas por default.
        for(ImgTag i : c) { tagInfo.add(i); }
        log_progress.doNotifyProgress("Tag Info. Atualizada.");
    }

    public KonachanPostParserImpl() {
        super("http://konachan.com");
    }
    
}
