import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**Clase que implementa el Filtro Ascii para ficheros .html
 *  @author Jose Antonio Cegarra Alonso
 *  @author Daniel Montesions Santos
 */
public class AsciiInputStream extends FilterInputStream {

    public AsciiInputStream(InputStream in) {

        super(in);
    }

    @Override
    public int read() throws IOException {
        int i;
        while ((i = in.read())!= -1 )
        {
            System.out.println(i);
        }
        return 0;
    }
}