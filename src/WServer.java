/*
 * --- TODO ---
 * 1. Implementar parametro ASCII
 * 2. Aplicar filtro por paremetros ?XXX=bool&YYY...
 * 3. Implementar .Zip
 * 4. Implementar .Gzip
 * 5. Separar clases en ficheros.java
 * 6. Problema con las cabeceras al pedir Comprimidos (los interpreta con la cabecera original y no los descarga)
 *
 */

import java.io.*;
import java.lang.ClassNotFoundException;
import java.net.*;
import java.util.zip.*;


public class WServer {


    public static String ObtenerParametros(String PeticionManipulada){
        String parametros = "";
        if (PeticionManipulada.contains("?"))
        {
            int inicio = PeticionManipulada.indexOf("?");
            int corte = PeticionManipulada.length();
            parametros = PeticionManipulada.substring(inicio,corte);
        }
        //System.out.printf(parametros);
        return parametros;
    }

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

    public static String ObtenerNombre(String PeticionManipulada)
    {
        if (PeticionManipulada.contains("."))
        {
            int corte = PeticionManipulada.indexOf(".");
            PeticionManipulada=PeticionManipulada.substring(0,corte);

        }

        return PeticionManipulada;

    }

    public static void Respuesta(OutputStream os,String nFichero, String extension,String parametros)
    {
        String cabecera = creaCabecera(nFichero,extension);
        try {

            FileInputStream archivo = new FileInputStream(nFichero+extension);
            os.write(cabecera.getBytes());
            int i;

            if (parametros.isEmpty()){
                System.out.println("He entrado en FICHERO NORMAL");
                System.out.println(parametros + " "+ extension);
                while ((i = archivo.read()) != -1)
                {
                    os.write(i);
                }

            } else {
                if (parametros.contains("asc=true"))
                {
                    AsciiInputStream ascios = new AsciiInputStream(archivo);
                    System.out.println("He entrado en ASCII");
                    System.out.println(parametros + " "+ extension);
                    ascios.read();
                }
                if (parametros.contains("gzip=true")){
                    System.out.println("He entrado en GZIP");

                    os = new GZIPOutputStream(os);
                    while ((i = archivo.read()) != -1)
                    {
                        os.write(i);
                    }
                }
                if (parametros.contains("?zip=true") || parametros.contains("&zip=true")){
                    System.out.println("He entrado en ZIP!");
                }
            }

            archivo.close();
            os.flush();
            os.close();

        }catch(IOException e){
            Respuesta(os,"404",".html","");
        }
    }

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

    public static String ManipularCadena(String Cadena)
    {
        String nombreFichero = Cadena.substring(5);
        int corte = nombreFichero.indexOf(" ");
        nombreFichero=nombreFichero.substring(0,corte);


        return nombreFichero;
    }

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
        else if (extension.equals(".zip") || extension.equals(".gzip") || extension.equals(".xml"))
        {
            if (extension.equals(".gzip"))
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

    public static void main(String args[]) throws IOException, ClassNotFoundException{
        ServerSocket Servicios;
        Socket serverSocket;
        try {
            Servicios = new ServerSocket(9411);
            while(true)
            {
                new Proceso(Servicios.accept()).start();
            }
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }


}