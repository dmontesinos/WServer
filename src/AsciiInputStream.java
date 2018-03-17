import java.io.FilterInputStream;
import java.io.InputStream;


public class AsciiInputStream extends FilterInputStream {

    public AsciiInputStream(InputStream in) {
        super(in);
    }

}