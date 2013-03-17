package imagebooru.persistence;

import imagebooru.ImgTags;

/**
 * Modela um médium de persistencia de tags.
 * Feita para trabalhar com ImgTags, define o contrato para um objeto de 
 * persistencia capaz de armazenar uma lista de tags. Necessidade de 
 * versionamento e/ou compatibilidade com versões anteriores ficam a gosto da
 * implementação usada.
 *
 * @author Guilherme
 * @created 03/03/2013
 * @since 2.2
 */
public abstract class AbstractTagsPersistence {
    public abstract void save(ImgTags tags);
    public abstract ImgTags load();
}
