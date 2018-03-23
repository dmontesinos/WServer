import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**Clase que implementa el Filtro Ascii para ficheros .html
 *  @author Jose Antonio Cegarra Alonso
 *  @author Daniel Montesions Santos
 */
public class AsciiInputStream extends FilterInputStream {
    OutputStream outputStream;

    /**Constructor del InputStream para codificar en ASCII
     * @param in inputStream el cual se manipulara para devolverlo sin etiquetas HTML
     */
    public AsciiInputStream(InputStream in) {
        super(in);
    }

    /** Sobreescritura del metodo read para poder leer los caracteres del fichero y descartar aquellos que formen parte
     * de una etiqueta
     * @return i: byte leido que no forma parte de una etiqueta
     * @throws IOException Excepcion que se lanzara en caso de que la lectura super.reaad o in.read de error
     */
    @Override
    public int read() throws IOException {
        int i;
        i=super.read();
        if (i == 60){
            do {
                i=super.read();
            } while(i != 62);

            i=in.read();
        }
        return i;
    }
}