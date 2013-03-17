package test;

import imagebooru.ImgTags;
import imagebooru.filenamemaker.AbstractFileNameGenerator;
import imagebooru.filenamemaker.DefaultFileNameGeneratorImpl;
import imagebooru.persistence.AbstractTagsPersistence;
import imagebooru.persistence.TextFileTagsPersistenceImpl;
import imagebooru.postparser.EshuushuuPostParserImpl;
import imagebooru.postparser.KonachanPostParserImpl;
import imagebooru.utils.PrintStreamProgressListener;
import java.util.Scanner;

/**
 *
 * @author Guilherme
 */
public class TestPostParser_Console {
    public static void main(String[] args){        
        Scanner sc = new Scanner(System.in);
        //ProgressListener
        PrintStreamProgressListener pl = new PrintStreamProgressListener(System.out);
        //Post Parser e FileNameGenerator.
        KonachanPostParserImpl ep = new KonachanPostParserImpl();   
        ep.setProgressListener(pl);
        AbstractFileNameGenerator fg = new DefaultFileNameGeneratorImpl("Konachan.com");
        //Persistencia:
        AbstractTagsPersistence tp = new TextFileTagsPersistenceImpl("peristence_teste.txt");
        try{
            ep.addAllToTagInfo(tp.load());
        }catch(Exception ex){
            System.err.println("Não foi possivel carregar tags: {"+ex.getMessage()+"}");
        }
        //Numero de Chs a usar:
        System.out.print("\nMaximo de chs? ");
        fg.setMaxFilenameLength(sc.nextInt());
        //Loop de Operação.
        int id;
        while(true){
            //Recebe um id do usuário
            System.out.print("\nId da Imagem/post: ");
            id = sc.nextInt();
            if(id<=0) { break; }
            //Busca as tags no site.
            ImgTags tags = ep.fetchAllTagsById( id );
            System.out.println( tags.toString() );
            //Gera um nome de arquivo valido com elas.
            String filename = fg.generateAsString(id, tags);
            System.out.println("{"+filename.length()+"} "+filename+"\n\n");
        }
        //Persiste as tags.
        tp.save(ep.getAllTagsInfo());
    }
}
