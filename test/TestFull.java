/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import imagebooru.ImgFile;
import imagebooru.directory.KonachanImageDirectoryImpl;
import imagebooru.filenamemaker.AbstractFileNameGenerator;
import imagebooru.filenamemaker.DefaultFileNameGeneratorImpl;
import imagebooru.persistence.AbstractTagsPersistence;
import imagebooru.persistence.TextFileTagsPersistenceImpl;
import imagebooru.postparser.KonachanPostParserImpl;
import imagebooru.utils.PrintStreamProgressListener;
import java.io.File;
import java.util.Collection;
import java.util.Scanner;

/**
 *
 * @author Guilherme
 */
public class TestFull {
    public static void main(String args[]){
        //Scanner para entrada:
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
        
        String dir, newFilename;
        while(true){
            System.out.print("\n\nDigite o nome do Diretório (end para sair): ");
            //Le o Diretório:
            dir = sc.next();
            //Vê se deve sair:
            if(dir.equalsIgnoreCase("end")) { break; }
            KonachanImageDirectoryImpl e = new KonachanImageDirectoryImpl(new File(dir));//Mude a Impl. Aqui.
            //Define o maximo de chs, segundo o diretório:
            fg.setMaxFilenameLength(AbstractFileNameGenerator.maxFilenameLength_base - e.getPathLength());
            //Obtem a lista de arquivos
            Collection<ImgFile> files = e.getAllImgFiles();
            //Trata arquivo a arquivo
            for(ImgFile i : files) { 
                System.out.println("\nTratando o Arquivo:\n\t"+i);
                newFilename = fg.generateAsString( i.getId(), ep.fetchAllTagsById(i.getId()) );
                System.out.println("Nome gerado: "+newFilename);
                i.rename(newFilename);
                tp.save(ep.getAllTagsInfo());
            }
        }
    }
}
