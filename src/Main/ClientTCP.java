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

public class ClientTCP extends Thread {
	private  DataOutputStream dataOutputStream = null;
	private  DataInputStream dataInputStream = null;
	private  int id;
	private  int cantidadClientes;	
	private int archivo;
	private int puerto;
	//cuando se pase a threads quitar el static


	public ClientTCP(int id, int puerto) {
		this.puerto = puerto;
		this.id = id;
		
	}

	private  void receiveFile(String fileName) throws Exception{
		int bytes = 0;
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);
		long size = dataInputStream.readLong();
		long sizeAux = size;
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
		if(archivoRecibido==2) 
		{
			nombreArchivo="archivo2.txt";
		}
		String nombreLog="logsCliente/"+dtf.format(now)+"-c"+(id)+"-log.txt";  
		PrintWriter writer = new PrintWriter(nombreLog, "UTF-8");
		writer.println("Nombre Archivo: "+nombreArchivo);
		writer.println("Tamaño Archivo: "+sizeAux+"bytes");
		writer.println("Cantidad de Paquetes Transmitidos: "+cantidadPaquetes);
		writer.println("Tiempo Transferencia: "+tiempoTransferencia+"ms");
		writer.println("Id Cliente al que se realizo transferencia: "+id);
		writer.println("Estado de transferencia: " + 200);
		dataOutputStream.write(200);
		System.out.println("Client "+ (id)+ " Received File");
		writer.close(); 
		fileOutputStream.close();
	}


	@Override
	public void run() 
	{
		try(Socket socket = new Socket("localhost",puerto)) 
		{
			
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			cantidadClientes = dataInputStream.read();
			String nombre="ArchivosRecibidos/Cliente"+id+"-Prueba-"+cantidadClientes+".txt";
			receiveFile(nombre);
			dataInputStream.close();
			dataInputStream.close();
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}
}

