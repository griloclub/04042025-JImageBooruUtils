package imagebooru.postparser;

import contnrutils.tree.AVLTreeExtended;
import imagebooru.ImgTag;
import imagebooru.ImgTag.TagType;
import imagebooru.ImgTags;
import java.io.IOException;
import java.util.Collection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;


/** 
 * Implementação de AbstractPostParser para scrapping de 
 * http://e-shuushuu.net .
 * Usa Jsoup para scrapping de e-shuushuu. Obtem as tags de um post segundo
 * seu tipo, e caso determinada tag não exista no cache interno, busca info.
 * detalhada da tag por seu id.<br/><br/>
 * 
 * Devido ao alto tempor levado para buscar info. tag a tag (mais de 10 seg para
 * 14 tags), o cache não deve ser atualizado com frequencia. A atualização será
 * manual.
 * 
 * @author Guilherme
 * @created 23/02/2013
 * @since 2.1
 */
public final class EshuushuuPostParserImpl extends AbstractPostParser {
    
    private static final String imgURL = "/image/";

    private AVLTreeExtended tagInfo = new AVLTreeExtended(ImgTag.CompareById);
    private int curId;//Auxiliar para não passar este id pela pilha.
    private int cur_retry = 0;//Auxiliar para controlar maximo de tentativas.
       
    private int tagIdFromLink(String link){
        return Integer.parseInt( link.replaceAll("[^0-9]*","") );
    }
    
    private int tagCountFromText(String text){
        return Integer.parseInt(text.trim().split(" ")[0]);//Pega a primeira "palavra".
    }
    
    private ImgTag getFromTagInfo(int id){
        return (ImgTag)tagInfo.get( ImgTag.getDummyWithId(id) );
    }

    private TagType TagTypeFromHtmlId(int id) {
        switch(id){
            case 1: return TagType.ttGeneral;
            case 2: return TagType.ttCopyright;
            case 3: return TagType.ttArtist;
            case 4: return TagType.ttCaracter;
            default: throw new IllegalArgumentException("Id deve ser 1,2,3 ou 4!");
        }
    }
    
    /**
     * Retorna um ImgTag apartir do link recebido.
     * 
     * @param link String com o link da pagina da Tag.
     * @return novo ImgTag com a info formatada do link.
     * @throws IOException 
     */
    private ImgTag fetchTagInfo(String link, int html_id) throws IOException{
        assert(link!=null);
        //Doc da URL.
        Document doc = Jsoup.connect(baseURL+link).get();
        //Define o id, nome, quantidade no booru e tipo da TAG.
        int id = tagIdFromLink(link);
        String nome = doc.select("div.title") .select("h2")
                .first().text().replaceAll("\"", "");
            //lista de dd's
        Elements el = doc.select("#content").select("div.display").select("dd");
        int count = tagCountFromText( el.select("a").first().text() );
        ImgTag.TagType type = TagTypeFromHtmlId( html_id );
        //retorna um ImgTag com essa info.
        return new ImgTag(id, baseURL, nome, count, type, null);
    }    
    
    /**
     * Retona a ImgTag equivalente a um elemento do HTML da pagina de um
     * post.
     * A ImgTag retornada pode tanto ser obtida do cache interno como do link
     * contido em <tt>e</tt>. <br/>
     * Se não conseguir buscar a tag no link especificado, leva RuntimeException.
     * 
     * @param e Elemento HTML com o nome e link para a tag.
     * @param html_id id Html do qual foi extraido o elemento.
     * @return ImgTag com info. detalhada desta tag.
     */
    private ImgTag elementToTag(Element e, int html_id){
        ImgTag t = getFromTagInfo( tagIdFromLink( e.attr("href") ) );
        try{
            if(t==null){
                t = fetchTagInfo(e.attr("href"),html_id);
                tagInfo.add(t);
            }            
            return t;
        } catch(IOException ex) {
            ex.printStackTrace();
            throw new RuntimeException("FALHA AO BUSCAR TAG INFO. PQP.");
        }
    }
    
    /**
     * Obtem todas as tags de um post e-shuushuu, dado seu id HTML_DOM.
     * 
     * @param doc Documento Jsoup com a pagina do post.
     * @param html_id id da tag html na qual as tags desejadas estão.
     * @return Lista com info. detalhada das tags obtidas
     */
    private ImgTags getTags(Document doc,int html_id){
        ImgTags ret = new ImgTags();
        Elements tags = doc.select(
                String.format("#quicktag%d_%d", html_id, curId)
            ).select("a");
        log_progress.doNotifyProgress("Buscando Tags... ("+html_id+")");
        int done=0;
        log_progress.doNotifyProgress(done, tags.size()-done);
        for(Element e : tags) { 
            ret.add( elementToTag(e,html_id) );
            log_progress.doNotifyProgress(++done, tags.size()-done);
        }
        return ret;
    }
    
    private ImgTags getArtist(Document doc){
        return getTags(doc, 3);
    }
    
    private ImgTags getCopyright(Document doc){
        return getTags(doc, 2);
    }
    
    private ImgTags getCaracters(Document doc){
        return getTags(doc, 4);
    }
    
    private ImgTags getGeneral(Document doc){
        return getTags(doc, 1);
    }

    public EshuushuuPostParserImpl() {
        super("http://e-shuushuu.net");
    }
    
    private int throwIfZeroOrNeg(int v){
        if(v<=0) { throw new IllegalArgumentException("Deve ser maior que 0"); }
        return v;
    }

    /**
     * Consulta um id de imagem para obter suas tags.
     *
     * @param id id do post no e-shuushuu.
     * @return Lista de tags deste post, em ordem de tipo.
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
            Document doc = Jsoup.connect(baseURL+imgURL+id).get();
            log_progress.doNotifyProgress("Conectado.");
            //Armazena id para uso geral de outros métodos.
            curId=id;
            //Busca tags em ordem.
            ret.addAll( getArtist(doc) );
            ret.addAll( getCopyright(doc) );
            ret.addAll( getCaracters(doc) );
            ret.addAll( getGeneral(doc) );
            //limpa id geral.
            curId=-1;
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
        throw new UnsupportedOperationException("e-shuushuu não suporta pesquisa por MD5.");
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
    
}
