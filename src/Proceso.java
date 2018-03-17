import java.io.IOException;
import java.io.OutputStream;
import java.net.Socket;

public class Proceso extends Thread {
    OutputStream osthread;
    String PeticionThread;


    public Proceso(Socket serverSocket) throws IOException {
        OutputStream os = serverSocket.getOutputStream();
        String Peticion=WServer.Peticion(serverSocket);
        osthread = os;
        PeticionThread = Peticion;
    }

    @Override
    public void run() {
        String PeticionManipulada = WServer.ManipularCadena(PeticionThread);
        String nombrefichero = WServer.ObtenerNombre(PeticionManipulada);
        String extension = WServer.ObtenerExtension(PeticionManipulada);
        String parametros = WServer.ObtenerParametros(PeticionManipulada);
        WServer.Respuesta(osthread,nombrefichero,extension,parametros);

    }
}