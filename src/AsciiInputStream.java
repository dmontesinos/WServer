import java.io.FilterInputStream;
import java.io.InputStream;

//Este comentario es para comprobar que el commit&push se ha hecho bien
//Por implementar
public class AsciiInputStream extends FilterInputStream {

    public AsciiInputStream(InputStream in) {
        super(in);
    }

}