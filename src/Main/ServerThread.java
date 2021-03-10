package Main;
import java.net.*;
import java.io.*;
import java.security.MessageDigest;

public class ServerThread extends Thread 
{

	private final static int serverPort = 5555;
	private int id;
	private ServerSocket socket;
	private boolean fin = false;
	private static String ruta="";
    private static DataOutputStream dataOutputStream = null;
    private static DataInputStream dataInputStream = null;

	public ServerThread(int identificacion,String path) 
	{
		try
		{
			this.socket = new ServerSocket(serverPort);	
			this.id=identificacion;
			ruta=path;
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
			System.out.println("Server "+id+" listening in port:5555");
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
	
	private static void sendFile(String path) throws Exception{
        int bytes = 0;
        byte[] textoCifrado;
        File file = new File(path);
        MessageDigest cifrador = MessageDigest.getInstance("MD5");
		
        FileInputStream fileInputStream = new FileInputStream(file); 
        int fileSize=(int) file.length();//Tamanio archivo en bytes
        String fileName=path.substring(5, path.length());
        
        dataOutputStream.writeLong(file.length());  
        byte[] buffer = new byte[4*1024];
        long startTime = System.currentTimeMillis();
        while ((bytes=fileInputStream.read(buffer))!=-1){
            dataOutputStream.write(buffer,0,bytes);
            dataOutputStream.flush();
        }
        long endTime = System.currentTimeMillis();
        long tiempoTransferencia = endTime - startTime;
        
        fileInputStream.close();
    }


	@Override
	public void run() 
	{
		recibir();
	}
}
