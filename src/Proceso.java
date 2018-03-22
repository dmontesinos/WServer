import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

/**Clase Proceso que hereda de Thread para implementar la concurrencia en nuestro programa
 *  @author Jose Antonio Cegarra Alonso
 *  @author Daniel Montesions Santos
 */
public class Proceso extends Thread {
    OutputStream osthread;
    String PeticionThread;


    /** Constructor de nuestro Proceso en el cual se le pasara un Socket y este se encargara de ejecutarlo de manera concurrente
     * @param serverSocket Socket del puerto que queremos leer las peticiones
     * @throws IOException Excepcion que se lanzara en caso de que el OutputStream en el Socket sea incorrecto
     */
    public Proceso(Socket serverSocket) throws IOException {
        OutputStream os = serverSocket.getOutputStream();
        String Peticion=WServer.Peticion(serverSocket);
        osthread = os;
        PeticionThread = Peticion;
    }

    /**Sobreescritura del metodo run de Thread para poder implementarlo en nuestra clase Proceso
     * Este proceso necesitara los datos mas basicos del InputStream y lanzara una Respuesta donde se tratara
     * lo que solicite el cliente
     */
    @Override
    public void run() {
        String PeticionManipulada = WServer.ManipularCadena(PeticionThread);
        String nombrefichero = WServer.ObtenerNombre(PeticionManipulada);
        String extension = WServer.ObtenerExtension(PeticionManipulada);
        String parametros = WServer.ObtenerParametros(PeticionManipulada);
        WServer.Respuesta(osthread,nombrefichero,extension,parametros);

    }
}