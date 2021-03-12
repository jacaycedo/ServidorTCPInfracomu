package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ClientTCP {
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	private static int id;
	private static int cantidadClientes;	
	private int archivo;
//cuando se pase a threads quitar el static
	
	
	public static void main(String[] args) throws IOException {
		try(Socket socket = new Socket("localhost",5555)) {
			id=1;
			cantidadClientes=2;
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			String nombre="ArchivosRecibidos/Cliente"+id+"-Prueba-"+cantidadClientes+".txt";
			receiveFile(nombre);
			dataInputStream.close();
			dataInputStream.close();
		}catch (Exception e){
			
			e.printStackTrace();
		}
	}

	private static void receiveFile(String fileName) throws Exception{
		int bytes = 0;
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);

		int size = dataInputStream.read();
		int sizeAux = size;
		dataOutputStream.write(id);
		
		byte[] buffer = new byte[4*1024];
		int cantidadPaquetes=0;
		long startTime = System.currentTimeMillis();
		while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
			fileOutputStream.write(buffer,0,bytes);
			cantidadPaquetes++;
			size -= bytes;      
		}
		
		long endTime = System.currentTimeMillis();
		long tiempoTransferencia = endTime - startTime;
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");  
		LocalDateTime now = LocalDateTime.now();  
		int archivoRecibido=dataInputStream.read();
		String nombreArchivo="archivo1.txt";
		if(archivoRecibido==2) {
			nombreArchivo="archivo2.txt";
		}
		String nombreLog="logsCliente/"+dtf.format(now)+"-log.txt";  
		PrintWriter writer = new PrintWriter(nombreLog, "UTF-8");
		writer.println("Nombre Archivo: "+nombreArchivo);
		writer.println("Tamaño Archivo: "+sizeAux+"bytes");
		writer.println("Cantidad de Paquetes Transmitidos: "+cantidadPaquetes);
		writer.println("Tiempo Transferencia: "+tiempoTransferencia+"ms");
		writer.println("Id Cliente al que se realizo transferencia: "+id);
		writer.println("Estado de transferencia: "+200);
		
		dataOutputStream.write(200);
		System.out.println("File Received");
		writer.close();
		fileOutputStream.close();
	}
}
