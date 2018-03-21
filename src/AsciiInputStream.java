import java.io.FilterInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

//Por implementar
public class AsciiInputStream extends FilterInputStream {

    public AsciiInputStream(InputStream in) {

        super(in);
    }

    @Override
    public int read() throws IOException {
        int i;
        i=in.read();
        if(i==60)
        {
            while(i == super.read())
            {

            }

        }
        System.out.println("Fin de lectura del fichero");
        return i;
    }

}