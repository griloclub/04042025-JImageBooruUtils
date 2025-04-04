package imagebooru.filenamemaker;

import imagebooru.ImgFile;
import imagebooru.ImgTag;
import imagebooru.ImgTags;

/**
 * Modela um Gerador de Nomes de Arquivo no formato MoeBooru.
 * Um FileNameGenerator (Gerador de Nomes de Arquivo) deve ser capaz de dado um
 * conjunto de ImgTag gerar um nome de arquivo no formato "pref tag1 tag2", que 
 * é o formato usado pelo MoeBooru.<br/><br/>
 * 
 * Como existe um limite de caracteres para nomes de arquivo no Windows, e este
 * limite é aplicado ao caminho completo do arquivo, um FileNameGenerator tambem
 * tem a importante função de, no caso do nome gerado superar o limite, decidir
 * quais tags são menos importantes que outras e remove-las, até que o nome de
 * arquivo resultante satisfaça o limite. Nestes casos, ele deve adicionar uma
 * tag especial que indica que houve tags removidas, para que mais tarde estas
 * possam ser buscadas na fonte da Imagem.<br/><br/>
 * 
 * para conveniencia, é possivel também obter somente a lista de Tags filtrada
 * pelas tomadas de decisão do FileNameGenerator. Entretando, neste caso não há
 * como garantir que o limite de caracteres continuara sendo respeitado.<br/><br/>
 * 
 * Os critérios para estas decisões são dependentes da implementação usada.<br/><br/>
 * 
 * @author Guilherme
 * @created 23/02/2013
 */
public abstract class AbstractFileNameGenerator {
    public static final int minFilenameLength_base = 24; // " 1234567 see_source.jpeg"
    public static final int maxFilenameLength_base = 253; //(260 - length("C:\") - length(".jpeg"))    
    private String pref;
    
    protected int maxFilenameLength = maxFilenameLength_base;
    
    protected static final ImgTag tagme = new ImgTag(
            Integer.MAX_VALUE,//Com sorte, nenhum ImageBooru usara este id.
            "http://10.0.0.5:3000/",//MyImoutoBooru.
            "tagme",
            Integer.MAX_VALUE, //Para que algoritmos de remoção nunca a removam.
            ImgTag.TagType.ttGeneral,
            null
        );
    protected static final ImgTag see_source = new ImgTag(
            Integer.MAX_VALUE-1,//Com sorte, nenhum ImageBooru usara este id.
            "http://10.0.0.5:3000/",//MyImoutoBooru.
            "see source",
            Integer.MAX_VALUE,//Para que algoritmos de remoção nunca a removam.
            ImgTag.TagType.ttFaults,
            null
        );
    
    private String throwIfInvalidPrefix(String prefix){
        if(prefix==null) { throw new NullPointerException(); }
        if(prefix.equals("")) { throw new IllegalArgumentException(); }
        return prefix;
    }
    
    /**
     * Retorna uma String sem nenhum caracter "Ilegal" em nomes de
     * Arquivos Windows.
     * É realizado um <tt>replaceAll()</tt> na String fornecida para remover
     * todas as ocorrencias de caracteres considerados Illegais em nomes de
     * arquivo.
     *
     * @param s String a ser "limpa"
     * @return s sem caracteres considerados especiais em nomes de arquivo.
     */
    protected final String semChsIlegais(String s){
        return s.replaceAll(ImgFile.IllegalCaractersRegex, "");
    }
    
    /**
     * Prepara o nome de tag recebido para ser adicionado em um nome
     * de arquivo Windows.
     * O nome recebido é tratado com <tt>{@link AbstractFileNameGenerator#semChsIlegais(java.lang.String) semChsIlegais()}</tt>
     * e tem os espaços em branco das pontas removidos, e os do meio substituidos
     * por underline (_). 
     *
     * @param tagName nome da tag a ser "limpo"
     * @return o nome fornecido pronto para ser adicionado a um FileName.
     */
    protected final String cleanTagName(String tagName){
        return semChsIlegais( tagName.trim().replaceAll(" ", "_") );
    }
    
    /**
     * Constroi um FileNameGenerator apartir de um prefixo.
     * O prefixo recebido será usado nos nomes de arquivo gerados por este
     * objeto, separado da primeira tag por um espaço em branco ( ).<br/>
     * Se nulls, leva NullPointerException.<br/>
     * Se prefixo vazio, leva IllegalArgumentException.
     * 
     * @param prefix String com o prefixo usado por este FileNameGenerator.
     */
    public AbstractFileNameGenerator(String prefix){
        pref = throwIfInvalidPrefix(prefix);
    }
    
    //Opções de nome de arquivo.
    
    /**
     * Sugere um novo limite de caracteres para nomes de arquivo 
     * gerados por este objeto.
     * Se o valor passado for menor que o minimo e maximo definidos nas
     * constantes da classe, ele sera definido para o valor valido mais próximo.<br/>
     * o retorno do método serve justamente para indicar se o valor passado foi
     * ou não aceito.
     *
     * @param value novo valor do limite de caracteres do nome gerado.
     * @return o real valor do limite de caracteres após a chamada.
     */
    public int setMaxFilenameLength(int value){
        if(value<(pref.length()+minFilenameLength_base)) { value = pref.length()+minFilenameLength_base; }
        if(value>(pref.length()+maxFilenameLength_base)) { value = pref.length()+maxFilenameLength_base; }
        return (maxFilenameLength=value);
    }
    
    /**
     * Retorna o prefixo usado por este objeto. 
     *
     * @return o prefixo atribuido aos nomes de arquivo gerados.
     */
    public String getPrefix(){
        return pref;
    }
    
    public abstract String generateAsString(int id, ImgTags tags);
    public abstract ImgTags generateAsTags(int id, ImgTags tags);
}
