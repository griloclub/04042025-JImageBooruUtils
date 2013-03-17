package imagebooru.directory;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class KonachanImageDirectoryImpl extends AbstractImageDirectory {  
    
    /*
     * Le regex, Chuck Noris Style.
     * 
     * (?=_) : Alguns aquivos tem lixo separado por um "_" antes do header.
     * 
     * (K|k) : Alguns começão com K, outros com k.
     * 
     * (\.|_|-)? : [konachan] e [com] podem ser separados por ".", "_", "-" ou 
     *              estarem juntos.
     * 
     * [_ -]* : pode haver ou não "_", "-" ou " " separando [com] e [id].
     * 
     * [0-9] : o id sempre começa com um numero. 
     */
    private static final String nameRegex = "(?=_?)(K|k)onachan(\\.|_|-)?com[_ -]*[0-9]";
    
    /*
     * Le regex, Chuck Noris Style.
     * 
     * 20[^0-9]20 : um espaço, um caracter não numérico, um espaço.
     *  
     * [0-9]* : numeros do id. vai incluir o ultimo "20".
     * 
     * (?<=20) : garante que terminou com "20" (que será removido).
     */
    private static final String escapedIdRegex = "20[^0-9]20[0-9]*(?<=20)";    
    
    /**
     * Retorna o id de uma imagem com o nome no formato esperado.
     * Dada uma Imagem com o nome no formato detectavel por nameRegex.
     * retorna o id dessa imagem ([0-9]*).
     * 
     * @param fn nome do arquivo.
     * @return id extraido
     */
    @Override
    protected int getIdFromFileName(String fn){
        //Regex:
        Matcher mc = Pattern.compile(nameRegex).matcher(fn);
        //ignora se não satisfazer o regex.
        if( !mc.find() ) { return -1; }
        //corta tudo antes do id.
        fn = fn.substring( mc.end()-1 );
        //novo regex, para encontrar o próximo não-numero e não hifen.
        mc = Pattern.compile("[^0-9-]").matcher(fn);
        //se encontrar, corta tudo após o id, se não econtrar (improvavel), então filename é o id.
        if( mc.find() ) { fn = fn.substring(0, mc.start()); }
        //se terminar com "-", remove.
        if( fn.endsWith("-") ) { fn = fn.substring(0,fn.length()-1); }
        //se ainda contem "-", deve ser caso de " " susbtituido por "%20" e truncado. tenta tratar.
        if( fn.contains("-") ) { fn = tryToCleanEscapedSpaces(fn); }
        //por fim, reza para só restem os numeros pertencentes ao id.
        try{            
            return Integer.parseInt(fn);
        } catch (NumberFormatException ex) {
            return -1;
        }
    }
    
    /**
     * Trata id's em nomes de arquivo que tiveram os espaços em branco
     * substituidos por "%20" e foram truncados para "20".
     * Se s satisfazer o regex especifico para 20-20[12345]20, trata removendo
     * os "20" do inicio e do fim.
     * 
     * Se não, assume que o proximo valor após o "-" é parte de uma tag valida,
     * e corta o "-" e tudo após ele.
     * 
     * @param s string a ser tratada.
     * @return s com "(%)20" removidos.
     */
    private String tryToCleanEscapedSpaces(String s){
        Matcher mc = Pattern.compile(escapedIdRegex).matcher(s);
        //se identificar o padrão exato, corta o inicio e o fim.
        if( mc.matches() ) { 
            s = s.substring(5, s.length()-2); 
        } else { //se não assume, por exemplo, "[123456-2]girls
            s = s.substring(0, s.indexOf("-"));
        }
        //retorna s, tratada ou não.
        return s;
    }

    public KonachanImageDirectoryImpl(File dir) {
        super(dir);
    }
    
}
