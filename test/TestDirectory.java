/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package test;

import imagebooru.ImgFile;
import imagebooru.directory.EShuuShuuImageDirectoryImpl;
import java.io.File;
import java.util.Collection;
import java.util.Scanner;

/**
 *
 * @author Guilherme
 */
public class TestDirectory {
    
    public static void main(String args[]){
        Scanner sc = new Scanner(System.in);
        
        String dir;
        while(true){
            System.out.print("\n\nDigite um nome de Diretório (end para sair): ");
            dir = sc.next();
            if(dir.equalsIgnoreCase("end")) { break; }
            EShuuShuuImageDirectoryImpl e = new EShuuShuuImageDirectoryImpl(new File(dir));//Mude a Impl. Aqui.
            Collection<ImgFile> files = e.getAllImgFiles();
            for(ImgFile i : files) { System.out.println(i); }
            //files.iterator().next().rename("renametest");
        }
    }
    
}
