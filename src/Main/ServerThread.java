package Main;
import java.net.*;
import java.nio.charset.Charset;
import java.nio.charset.StandardCharsets;
import java.io.*;
import java.security.MessageDigest;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ServerThread extends Thread 
{

	private int id;
	private ServerSocket socket;
	private String ruta="";
	private DataOutputStream dataOutputStream = null;
	private DataInputStream dataInputStream = null;
	private int archivo;
	private int cantidadClientes;
	
	private Cifrador cifrador;

	public ServerThread(int identificacion,String path,int opcion, int puerto,int cantidad) 
	{
		try
		{
			cifrador=new Cifrador();
			cantidadClientes=cantidad;
			this.socket = new ServerSocket(puerto);	
			this.id=identificacion;
			ruta=path;
			archivo=opcion;
		}
		catch (Exception e) 
		{
			e.printStackTrace();
		}
	}

	private void recibir()
	{
		try 
		{
			System.out.println("Server "+id+" esperando solicitudes en puerto: " + socket.getLocalPort());
			Socket clientSocket = socket.accept();
			System.out.println(clientSocket+" connected.");
			dataInputStream = new DataInputStream(clientSocket.getInputStream());
			dataOutputStream = new DataOutputStream(clientSocket.getOutputStream());

			sendFile(ruta);


			dataInputStream.close();
			dataOutputStream.close();
			clientSocket.close();

		} 
		catch (Exception e) 
		{
			e.printStackTrace();
		}

	}

	private void sendFile(String path) throws Exception{
		int bytes = 0;
		File file = new File(path);
		String hash=cifrador.getFileChecksum(path);
		System.out.println("Servidor:" +hash);
		FileInputStream fileInputStream = new FileInputStream(file);
		dataOutputStream.write(cantidadClientes);
		dataOutputStream.write(hash.length());
		dataOutputStream.write(hash.getBytes(StandardCharsets.UTF_8));
		
		long fileSize=file.length();//Tamanio archivo en bytes
		String fileName=path.substring(5, path.length());

		dataOutputStream.writeLong(fileSize); 
		int idCliente=dataInputStream.read();
		byte[] buffer = new byte[4*1024];
		long startTime = System.currentTimeMillis();
		DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd-HH-mm-ss");  
		LocalDateTime now = LocalDateTime.now();  
		String nombreLog="logsServidor/"+dtf.format(now)+"id"+id+"-log.txt";  
		int cantidadPaquetes=0;
		while ((bytes=fileInputStream.read(buffer))!=-1){
			dataOutputStream.write(buffer,0,bytes);
			cantidadPaquetes++;
			dataOutputStream.flush();
		}

		if(archivo==1) 
		{
			dataOutputStream.write(1);
			dataOutputStream.flush();
		}
		else 
		{
			dataOutputStream.write(2);
			dataOutputStream.flush();
		}
		long endTime = System.currentTimeMillis();
		long tiempoTransferencia = endTime - startTime;
		int estadoTransferencia=dataInputStream.read();
		PrintWriter writer = new PrintWriter(nombreLog, "UTF-8");
		writer.println("Nombre Archivo: "+fileName);
		writer.println("Tamaño Archivo: "+fileSize+"bytes");
		writer.println("Cantidad de Paquetes Transmitidos: "+cantidadPaquetes);
		writer.println("Tiempo Transferencia: "+tiempoTransferencia+"ms");
		writer.println("Id Cliente al que se realizo transferencia: "+idCliente);
		writer.println("Estado de transferencia: "+estadoTransferencia);
		writer.println("Hash enviado: "+hash);

		fileInputStream.close();
		writer.close();
	}



	@Override
	public void run() 
	{
		recibir();
	}
}
