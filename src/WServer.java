/*
 * --- TODO ---
 * 1. Implementar .Zip
 */

import java.io.*;
import java.net.*;
import java.util.zip.*;

/** Clase principal del WServer
 *  Contiene la mayoria de los metodos necesarios para ejecutar el servidor
 *  @author Jose Antonio Cegarra Alonso
 *  @author Daniel Montesions Santos
 *
 */
public class WServer {


    /**Metodo que separa los parametros de la peticion
     * @param PeticionManipulada InputStream Manipulado para un uso mas comodo de el
     * @return parametros: unica y exclusivamente los parametros (todos juntos) a partir del ? en caso de existir
     */
    public static String ObtenerParametros(String PeticionManipulada){
        String parametros = "";
        if (PeticionManipulada.contains("?"))
        {
            int inicio = PeticionManipulada.indexOf("?");
            int corte = PeticionManipulada.length();
            parametros = PeticionManipulada.substring(inicio,corte);
        }
        return parametros;
    }


    /**Metodo que separa la extension de los parametros y del nombre de fichero
     * @param PeticionManipulada InputStream Manipulado para un uso mas comodo de el
     * @return extension: la extension del fichero incluyendo el " ."
     */
    public static String ObtenerExtension(String PeticionManipulada)
    {
        String extension = "";
        if (PeticionManipulada.contains("."))
        {
            int inicio = PeticionManipulada.indexOf(".");
            int corte;
            if(PeticionManipulada.contains("?")){
                corte = PeticionManipulada.indexOf("?");
            }
            else{
                corte = PeticionManipulada.length();
            }
            extension = PeticionManipulada.substring(inicio,corte);
        }

        return extension;

    }


    /**Metodo que obtiene el nombre del fichero solicitado a partir del InputStream
     * @param PeticionManipulada InputStream Manipulado para un uso mas comodo de el
     * @return PeticionManipulada: nombre del fichero
     */
    public static String ObtenerNombre(String PeticionManipulada)
    {
        if (PeticionManipulada.contains("."))
        {
            int corte = PeticionManipulada.indexOf(".");
            PeticionManipulada=PeticionManipulada.substring(0,corte);

        }

        return PeticionManipulada;

    }

    /**Metodo principal de la clase WServer que controla la escritura del OutputStream,
     * el fichero que se nos pide,los filtros que se nos pide y redireccionamiento en caso de error
     * @param os OutputStream como canal donde escribiremos la salida byte a byte
     * @param nFichero Nombre del fichero el cual queremos pasar por el OutputStream
     * @param extension Extension del fichero para tratar de una manera u otra la cabecera
     * @param parametros Parametros que modifican el OutputStream antes de entregarlo al cliente
     */
    public static void Respuesta(OutputStream os,String nFichero, String extension,String parametros)
    {
        boolean compression=false;
        boolean asciiread=false;
        boolean controlparametros = false;
        String cabecera = null;
        try {
            FileInputStream archivo = new FileInputStream(nFichero+extension);
            int i;
            if (parametros.isEmpty())
            {
                cabecera = creaCabecera(nFichero,extension);
                os.write(cabecera.getBytes());
                while ((i = archivo.read()) != -1)
                {
                    os.write(i);
                }

            } else {
                if ( (parametros.contains("?asc=true") || (parametros.contains("&asc=true"))) && (extension.equals(".html")) )
                {
                    controlparametros=true;
                    asciiread=true;
                }
                if (parametros.contains("?zip=true&gzip=true") || parametros.contains("&zip=true&gzip=true") || (parametros.contains("?gzip=true&zip=true")) || (parametros.contains("&gzip=true&zip=true"))){
                    nFichero=nFichero+extension;
                    extension=".gz.zip";
                    cabecera = creaCabecera(nFichero,extension);
                    os.write(cabecera.getBytes());
                    os = new GZIPOutputStream(os);
                    os = new ZipOutputStream(os);
                    ZipEntry ze = new ZipEntry(nFichero);
                    ((ZipOutputStream) os).putNextEntry(ze);
                    controlparametros=true;
                    compression=true;
                } else if (parametros.contains("?gzip=true") || (parametros.contains("&gzip=true"))){
                    nFichero=nFichero+extension;
                    extension=".gz";
                    cabecera = creaCabecera(nFichero,extension);
                    os.write(cabecera.getBytes());
                    os = new GZIPOutputStream(os);
                    controlparametros=true;
                    compression=true;
                } else if (parametros.contains("?zip=true") || parametros.contains("&zip=true")){
                    nFichero=nFichero+extension;
                    extension=".zip";
                    cabecera = creaCabecera(nFichero,extension);
                    os.write(cabecera.getBytes());
                    os = new ZipOutputStream(os);
                    ZipEntry ze = new ZipEntry(nFichero);
                    ((ZipOutputStream) os).putNextEntry(ze);
                    controlparametros=true;
                    compression=true;

                }

                if (controlparametros)
                {
                    if (asciiread){
                        if(!compression)
                        {
                            cabecera = creaCabecera(nFichero,extension);
                            os.write(cabecera.getBytes());
                        }
                        AsciiInputStream ainput = new AsciiInputStream(archivo);
                        while ((i=ainput.read())!= -1){
                            os.write(i);
                        }
                    }
                    else {
                        while ((i = archivo.read()) != -1)
                        {
                            os.write(i);
                        }
                    }
                }
                else{
                    Respuesta(os,"404",".html","");
                }
            }

            archivo.close();
            os.flush();
            os.close();

        }catch(IOException e){
            Respuesta(os,"404",".html","");
        }
    }

    /**Metodo que controla el Socket de entrada para leer la peticion del InputStream
     * @param serverSocket Socket del puerto que queremos leer las peticiones
     * @return pRecibida: String con la peticion ya manipulada para que la intrepretacion de esta sea mas sencilla
     */
    public static String Peticion(Socket serverSocket)
    {
        BufferedReader in = null;
        String pRecibida = "";
        try {
            in = new BufferedReader(new InputStreamReader(serverSocket.getInputStream()));
            pRecibida = in.readLine();


        } catch (IOException e) {
            e.printStackTrace();
        }
        return pRecibida;

    }

    /**Metodo que trocea la primera cadena del InputStream para que mas tarde pueda ser troceada en partes mas pequeñas
     * @param Cadena InputStream traducido a String para poder trocearlo
     * @return nombreFichero: Peticion de la cadena sin separar (fichero.extension?parametros)
     */
    public static String ManipularCadena(String Cadena)
    {
        String nombreFichero = Cadena.substring(5);
        int corte = nombreFichero.indexOf(" ");
        nombreFichero=nombreFichero.substring(0,corte);


        return nombreFichero;
    }

    /**Metodo que crea la cabecera que se pasara al navegador del cliente
     * @param nombreFichero nombre del fichero solicitado
     * @param extension extension del fichero solicitado
     * @return retorno: cabecera ya lista para escribir byte a byte en el OutputStream
     */
    public static String creaCabecera(String nombreFichero, String extension)
    {
        String ok= "HTTP/1.1 200 OK\n";
        String contenttype;
        String cierre = "\n\n";
        String contentdisposition = "Content-Disposition: attachment; filename=\"" + nombreFichero + extension + "\n";
        if (extension.equals(".html") || extension.equals(".txt"))
        {
            contentdisposition = "";
            if (extension.equals(".html"))
            {
                contenttype = "Content-Type: text/html";
            }
            else {
                contenttype = "Content-Type: text/plain";
            }
        }
        else if (extension.equals(".jpeg") || extension.equals(".png") || extension.equals(".gif") || extension.equals(".jpg"))
        {
            contenttype = "Content-Type: image/"+extension.substring(1) ;
        }
        else if (extension.equals(".zip") || extension.equals(".gz") || extension.equals(".xml") || extension.equals(".gz.zip"))
        {
            if (extension.equals(".gz"))
            {
                contenttype = "Content-Type: application/x-gzip";
            }
            else {
                contenttype = "Content-Type: application/"+extension.substring(1) ;
            }
        }
        else
        {
            contenttype = "Content-Type: application/octet-stream";
        }

        String retorno = ok + contentdisposition /*+ nombrefichero + extension +"\n"*/ + contenttype + cierre;
        return retorno;
    }

    /**Metodo principal en el cual se crean los threads para la concurrencia del programa y la creacion del socket
     * @param args no se usan en este programa
     */
    public static void main(String args[]) {
        ServerSocket Servicios;
        String prefijo = args[0];
        int puerto = Integer.parseInt(args[1]);

        try {
            prefijo = args[0];
            if (args[0] != prefijo){
                System.out.println("Debes especificar los parámetros correctamente.");
            } else{
                Servicios = new ServerSocket(puerto);
                while(true)
                {
                    new Proceso(Servicios.accept()).start();
                }
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }


}