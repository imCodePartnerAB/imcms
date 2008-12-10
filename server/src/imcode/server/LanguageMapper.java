package imcode.server;

import com.imcode.db.Database;
import com.imcode.db.commands.SqlQueryCommand;
import imcode.server.user.UserDomainObject;
import imcode.util.Html;
import imcode.util.Utility;
import org.apache.commons.lang.StringUtils;
import org.apache.log4j.Logger;

import java.util.*;

/** @author kreiger */
public class LanguageMapper {
    /** {@see <a href="http://www.loc.gov/standards/iso639-2/langhome.html">http://www.loc.gov/standards/iso639-2/langhome.html</a>} **/
    public final static String ISO639_2 = "ISO 639-2";

    private static Map iso639_1to639_2map;
    private static Map iso639_2to639_1map;

    private static String[][] iso6391to6392table = new String[][] {
            { "aar", "aa" }, { "abk", "ab" }, { "ace", "" }, { "ach", "" }, { "ada", "" }, { "ady", "" }, { "afa", "" },
            { "afh", "" }, { "afr", "af" }, { "aka", "ak" }, { "akk", "" }, { "alb/sqi", "sq" }, { "ale", "" },
            { "alg", "" }, { "amh", "am" }, { "ang", "" }, { "apa", "" }, { "ara", "ar" }, { "arc", "" },
            { "arg", "an" }, { "arm/hye", "hy" }, { "arn", "" }, { "arp", "" }, { "art", "" }, { "arw", "" },
            { "asm", "as" }, { "ast", "" }, { "ath", "" }, { "aus", "" }, { "ava", "av" }, { "ave", "ae" },
            { "awa", "" }, { "aym", "ay" }, { "aze", "az" }, { "bad", "" }, { "bai", "" }, { "bak", "ba" },
            { "bal", "" }, { "bam", "bm" }, { "ban", "" }, { "baq/eus", "eu" }, { "bas", "" }, { "bat", "" },
            { "bej", "" }, { "bel", "be" }, { "bem", "" }, { "ben", "bn" }, { "ber", "" }, { "bho", "" },
            { "bih", "bh" }, { "bik", "" }, { "bin", "" }, { "bis", "bi" }, { "bla", "" }, { "bnt", "" },
            { "tib/bod", "bo" }, { "bos", "bs" }, { "bra", "" }, { "bre", "br" }, { "btk", "" }, { "bua", "" },
            { "bug", "" }, { "bul", "bg" }, { "bur/mya", "my" }, { "cad", "" }, { "cai", "" }, { "car", "" },
            { "cat", "ca" }, { "cau", "" }, { "ceb", "" }, { "cel", "" }, { "cze/ces", "cs" }, { "cha", "ch" },
            { "chb", "" }, { "che", "ce" }, { "chg", "" }, { "chi/zho", "zh" }, { "chk", "" }, { "chm", "" },
            { "chn", "" }, { "cho", "" }, { "chp", "" }, { "chr", "" }, { "chu", "cu" }, { "chv", "cv" }, { "chy", "" },
            { "cmc", "" }, { "cop", "" }, { "cor", "kw" }, { "cos", "co" }, { "cpe", "" }, { "cpf", "" }, { "cpp", "" },
            { "cre", "cr" }, { "crh", "" }, { "crp", "" }, { "csb", "" }, { "cus", "" }, { "wel/cym", "cy" },
            { "cze/ces", "cs" }, { "dak", "" }, { "dan", "da" }, { "dar", "" }, { "day", "" }, { "del", "" },
            { "den", "" }, { "ger/deu", "de" }, { "dgr", "" }, { "din", "" }, { "div", "dv" }, { "doi", "" },
            { "dra", "" }, { "dsb", "" }, { "dua", "" }, { "dum", "" }, { "dut/nld", "nl" }, { "dyu", "" },
            { "dzo", "dz" }, { "efi", "" }, { "egy", "" }, { "eka", "" }, { "gre/ell", "el" }, { "elx", "" },
            { "eng", "en" }, { "enm", "" }, { "epo", "eo" }, { "est", "et" }, { "baq/eus", "eu" }, { "ewe", "ee" },
            { "ewo", "" }, { "fan", "" }, { "fao", "fo" }, { "per/fas", "fa" }, { "fat", "" }, { "fij", "fj" },
            { "fin", "fi" }, { "fiu", "" }, { "fon", "" }, { "fre/fra", "fr" }, { "frm", "" }, { "fro", "" },
            { "fry", "fy" }, { "ful", "ff" }, { "fur", "", }, { "gaa", "Ga" }, { "gay", "" }, { "gba", "" },
            { "gem", "" }, { "geo/kat", "ka" }, { "ger/deu", "de" }, { "gez", "" }, { "gil", "" }, { "gla", "gd" },
            { "gle", "ga" }, { "glg", "gl" }, { "glv", "gv" }, { "gmh", "" }, { "goh", "" }, { "gon", "" },
            { "gor", "" }, { "got", "" }, { "grb", "" }, { "grc", "" }, { "gre/ell", "el" }, { "grn", "gn" },
            { "guj", "gu" }, { "gwi", "" }, { "hai", "" }, { "hat", "ht" }, { "hau", "ha" }, { "haw", "" },
            { "heb", "he" }, { "her", "hz" }, { "hil", "" }, { "him", "" }, { "hin", "hi" }, { "hit", "" },
            { "hmn", "" }, { "hmo", "ho" }, { "scr/hrv", "hr" }, { "hsb", "" }, { "hun", "hu" }, { "hup", "" },
            { "arm/hye", "hy" }, { "iba", "" }, { "ibo", "ig" }, { "ice/isl", "is" }, { "ido", "io" }, { "iii", "ii" },
            { "ijo", "" }, { "iku", "iu" }, { "ile", "ie" }, { "ilo", "" }, { "ina", "ia" }, { "inc", "" },
            { "ind", "id" }, { "ine", "" }, { "inh", "" }, { "ipk", "ik" }, { "ira", "" }, { "iro", "" },
            { "ice/isl", "is" }, { "ita", "it" }, { "jav", "jv" }, { "jbo", "" }, { "jpn", "ja" }, { "jpr", "" },
            { "jrb", "" }, { "kaa", "" }, { "kab", "" }, { "kac", "" }, { "kal", "kl" }, { "kam", "" }, { "kan", "kn" },
            { "kar", "" }, { "kas", "ks" }, { "geo/kat", "ka" }, { "kau", "kr" }, { "kaw", "" }, { "kaz", "kk" },
            { "kbd", "" }, { "kha", "" }, { "khi", "" }, { "khm", "km" }, { "kho", "" }, { "kik", "ki" },
            { "kin", "rw" }, { "kir", "ky" }, { "kmb", "" }, { "kok", "" }, { "kom", "kv" }, { "kon", "kg" },
            { "kor", "ko" }, { "kos", "" }, { "kpe", "" }, { "krc", "" }, { "kro", "" }, { "kru", "" }, { "kua", "kj" },
            { "kum", "" }, { "kur", "ku" }, { "kut", "" }, { "lad", "" }, { "lah", "" }, { "lam", "" }, { "lao", "lo" },
            { "lat", "la" }, { "lav", "lv" }, { "lez", "" }, { "lim", "li" }, { "lin", "ln" }, { "lit", "lt" },
            { "lol", "" }, { "loz", "" }, { "ltz", "lb" }, { "lua", "" }, { "lub", "lu" }, { "lug", "lg" },
            { "lui", "" }, { "lun", "" }, { "luo", "" }, { "lus", "" }, { "mac/mkd", "mk" }, { "mad", "" },
            { "mag", "" }, { "mah", "mh" }, { "mai", "" }, { "mak", "" }, { "mal", "ml" }, { "man", "" },
            { "mao/mri", "mi" }, { "map", "" }, { "mar", "mr" }, { "mas", "" }, { "may/msa", "ms" }, { "mdf", "" },
            { "mdr", "" }, { "men", "" }, { "mga", "" }, { "mic", "" }, { "min", "" }, { "mis", "" },
            { "mac/mkd", "mk" }, { "mkh", "" }, { "mlg", "mg" }, { "mlt", "mt" }, { "mnc", "" }, { "mni", "" },
            { "mno", "" }, { "moh", "" }, { "mol", "mo" }, { "mon", "mn" }, { "mos", "" }, { "mao/mri", "mi" },
            { "may/msa", "ms" }, { "mul", "" }, { "mun", "" }, { "mus", "" }, { "mwr", "" }, { "bur/mya", "my" },
            { "myn", "" }, { "myv", "" }, { "nah", "" }, { "nai", "" }, { "nap", "" }, { "nau", "na" }, { "nav", "nv" },
            { "nbl", "nr" }, { "nde", "nd" }, { "ndo", "ng" }, { "nds", "" }, { "nep", "ne" }, { "new", "" },
            { "nia", "" }, { "nic", "" }, { "niu", "" }, { "dut/nld", "nl" }, { "nno", "nn" }, { "nob", "nb" },
            { "nog", "" }, { "non", "" }, { "nor", "no" }, { "nso", "" }, { "nub", "" }, { "nya", "ny" }, { "nym", "" },
            { "nyn", "" }, { "nyo", "" }, { "nzi", "" }, { "oci", "oc" }, { "oji", "oj" }, { "ori", "or" },
            { "orm", "om" }, { "osa", "" }, { "oss", "os" }, { "ota", "" }, { "oto", "" }, { "paa", "" }, { "pag", "" },
            { "pal", "" }, { "pam", "" }, { "pan", "pa" }, { "pap", "" }, { "pau", "" }, { "peo", "" },
            { "per/fas", "fa" }, { "phi", "" }, { "phn", "" }, { "pli", "pi" }, { "pol", "pl" }, { "pon", "" },
            { "por", "pt" }, { "pra", "" }, { "pro", "" }, { "pus", "ps" }, { "que", "qu" }, { "raj", "" },
            { "rap", "" }, { "rar", "" }, { "roa", "", }, { "roh", "rm" }, { "rom", "" }, { "rum/ron", "ro" },
            { "run", "rn" }, { "rus", "ru" }, { "sad", "" }, { "sag", "sg" }, { "sah", "" }, { "sai", "" },
            { "sal", "" }, { "sam", "" }, { "san", "sa" }, { "sas", "" }, { "sat", "" }, { "scc/srp", "sr" },
            { "sco", "" }, { "scr/hrv", "hr" }, { "sel", "" }, { "sem", "" }, { "sga", "" }, { "sgn", "" },
            { "shn", "" }, { "sid", "" }, { "sin", "si" }, { "sio", "" }, { "sit", "" }, { "sla", "" },
            { "slo/slk", "sk" }, { "slv", "sl" }, { "sma", "" }, { "sme", "se" }, { "smi", "" }, { "smj", "" },
            { "smn", "" }, { "smo", "sm" }, { "sms", "" }, { "sna", "sn" }, { "snd", "sd" }, { "snk", "" },
            { "sog", "" }, { "som", "so" }, { "son", "" }, { "sot", "st" }, { "spa", "es" }, { "alb/sqi", "sq" },
            { "srd", "sc" }, { "scc/srp", "sr" }, { "srr", "" }, { "ssa", "" }, { "ssw", "ss" }, { "suk", "" },
            { "sun", "su" }, { "sus", "" }, { "sux", "" }, { "swa", "sw" }, { "swe", "sv" }, { "syr", "" },
            { "tah", "ty" }, { "tai", "" }, { "tam", "ta" }, { "tat", "tt" }, { "tel", "te" }, { "tem", "" },
            { "ter", "" }, { "tet", "" }, { "tgk", "tg" }, { "tgl", "tl" }, { "tha", "th" }, { "tib/bod", "bo" },
            { "tig", "" }, { "tir", "ti" }, { "tiv", "" }, { "tkl", "" }, { "tli", "" }, { "tmh", "" }, { "tog", "" },
            { "ton", "to" }, { "tpi", "" }, { "tsi", "" }, { "tsn", "tn" }, { "tso", "ts" }, { "tuk", "tk" },
            { "tum", "" }, { "tup", "" }, { "tur", "tr" }, { "tut", "" }, { "tvl", "" }, { "twi", "tw" }, { "tyv", "" },
            { "udm", "" }, { "uga", "" }, { "uig", "ug" }, { "ukr", "uk" }, { "umb", "" }, { "und", "" },
            { "urd", "ur" }, { "uzb", "uz" }, { "vai", "" }, { "ven", "ve" }, { "vie", "vi" }, { "vol", "vo" },
            { "vot", "" }, { "wak", "" }, { "wal", "" }, { "war", "" }, { "was", "" }, { "wel/cym", "cy" },
            { "wen", "" }, { "wln", "wa" }, { "wol", "wo" }, { "xal", "" }, { "xho", "xh" }, { "yao", "" },
            { "yap", "" }, { "yid", "yi" }, { "yor", "yo" }, { "ypk", "" }, { "zap", "" }, { "zen", "" },
            { "zha", "za" }, { "chi/zho", "zh" }, { "znd", "" }, { "zul", "zu" }, { "zun", "" }, };

    private final static Logger log = Logger.getLogger(LanguageMapper.class.getName());

    static {
        Map iso6391to6392map = new HashMap();
        Map iso6392to6391map = new HashMap();
        for ( int i = 0; i < iso6391to6392table.length; i++ ) {
            String iso639_2 = iso6391to6392table[i][0];
            String iso639_1 = iso6391to6392table[i][1];
            if ( !"".equalsIgnoreCase(iso639_1) ) {
                int index = iso639_2.indexOf("/");
                if ( -1 != index ) {
                    iso639_2 = iso639_2.substring(0, index);
                }
                iso6391to6392map.put(iso639_1, iso639_2);
                iso6392to6391map.put(iso639_2, iso639_1);
            }
        }
        LanguageMapper.iso639_1to639_2map = Collections.unmodifiableMap(iso6391to6392map);
        LanguageMapper.iso639_2to639_1map = Collections.unmodifiableMap(iso6392to6391map);
    }

    private final Database database;
    private final String defaultLanguage;

    public LanguageMapper(Database database, String defaultLanguage) {
        this.database = database;
        this.defaultLanguage = defaultLanguage;
    }

    public String getCurrentLanguageNameInUsersLanguage(UserDomainObject user,
                                                        String documentLanguage) {
        List languageKeysAndNamesInUsersLanguage = getListOfLanguageKeysAndNamesInUsersLanguage(user);
        String result = null;
        for ( Iterator iterator = languageKeysAndNamesInUsersLanguage.iterator(); iterator.hasNext(); ) {
            String langPrefix = (String) iterator.next();
            String languageNameInUserLanguage = (String) iterator.next();
            if ( langPrefix.equalsIgnoreCase(documentLanguage) ) {
                result = languageNameInUserLanguage;
            }
        }
        return result;
    }

    public String createLanguagesOptionList(UserDomainObject user,
                                            String documentLanguage) {
        List languageKeysAndNamesInUsersLanguage = getListOfLanguageKeysAndNamesInUsersLanguage(user);
        return Html.createOptionList(languageKeysAndNamesInUsersLanguage, documentLanguage);
    }

    private List getListOfLanguageKeysAndNamesInUsersLanguage(UserDomainObject user) {
        final Object[] parameters = new String[] {
                user.getLanguageIso639_2() };
        String[][] languages = (String[][]) database.execute(new SqlQueryCommand("select lang_prefix, user_prefix, language from languages where user_prefix = ?", parameters, Utility.STRING_ARRAY_ARRAY_HANDLER));
        List languagesInOptionList = new ArrayList();
        for ( int i = 0; i < languages.length; i++ ) {
            String langStr = languages[i][0];
            langStr = getAsIso639_2OrDefaultLanguage(langStr, defaultLanguage);
            String userLangPrefix = languages[i][1];
            String languageNameInUserLanguage = languages[i][2];
            if ( userLangPrefix.equalsIgnoreCase(user.getLanguageIso639_2()) ) {
                languagesInOptionList.add(langStr);
                languagesInOptionList.add(languageNameInUserLanguage);
            }
        }
        return languagesInOptionList;
    }

    public static String getAsIso639_2OrDefaultLanguage(String langStr, String defaultLanguage) {
        try {
            if ( StringUtils.isBlank(langStr) ) {
                return defaultLanguage;
            }
            return getAsIso639_2(langStr);
        } catch ( LanguageNotSupportedException e ) {
            log.error("Unsupported language '"
                      + langStr
                      + "' found in database. Using default.", e);
            return defaultLanguage;
        }
    }

    /**
     * @param language The iso-639-1 representation of the language, i.e. 'sv'.
     * @return the iso-639-2 representation of the language, i.e. 'swe'.
     * @throws IllegalArgumentException If an unsupported language is requested.
     */
    public static String convert639_1to639_2(String language) throws LanguageNotSupportedException {
        if ( "se".equalsIgnoreCase(language) ) {
            // FIXME Nasty workaround for our "se" which should be "sv". Hides Northern Sami language (sme).
            return (String) iso639_1to639_2map.get("sv");
        }
        String iso6392 = (String) iso639_1to639_2map.get(language.toLowerCase());
        if ( null == iso6392 ) {
            throw new LanguageNotSupportedException("Language '" + language + "' not supported.");
        }
        return iso6392;
    }

    /**
     * @param language The iso-639-2 representation of the language, i.e. 'swe'.
     * @return the iso-639-1 representation of the language, i.e. 'sv'.
     * @throws IllegalArgumentException If an unsupported language is requested.
     */
    public static String convert639_2to639_1(String language) throws LanguageNotSupportedException {
        String iso6391 = (String) iso639_2to639_1map.get(language.toLowerCase());
        if ( null == iso6391 ) {
            throw new LanguageNotSupportedException("Language '" + language + "' not supported.");
        }
        return iso6391;
    }

    public static boolean existsIsoCode639_2(String isoCode639_2) {
        return iso639_2to639_1map.containsKey(isoCode639_2);
    }

    private static String getAsIso639_2(String langStr) throws LanguageNotSupportedException {
        if ( 2 == langStr.length() ) {
            langStr = convert639_1to639_2(langStr);
        }
        return langStr;
    }

    public String getDefaultLanguage() {
        return defaultLanguage;
    }

    public static class LanguageNotSupportedException extends RuntimeException {
        private LanguageNotSupportedException(String message) {
            super(message);
        }
    }

}
