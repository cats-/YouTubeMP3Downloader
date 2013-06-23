package cats.ytmp3.util;

/**
 * User: cats
 * Date: 6/17/13
 * Time: 5:55 PM
 */
public final class FileNameValidator {

    public static final String INVALID = "<>:\"/\\|?*";

    public static char replacement = '_';

    private FileNameValidator(){}

    public static String validate(final String fileName){
        final StringBuffer buffer = new StringBuffer();
        for(final char character : fileName.toCharArray())
            buffer.append(INVALID.indexOf(character) > -1 ? replacement : character);
        return buffer.toString();
    }
}
