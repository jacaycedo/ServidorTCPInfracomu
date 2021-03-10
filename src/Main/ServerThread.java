package Main;
import java.net.*;
import java.io.*;

public class ServerThread implements Runnable
{


	private ServerSocket socket;
	private boolean fin = false;
	private File enviado;

	public ServerThread(ServerSocket socket, File enviado) 
	{
		try
		{
			this.socket = socket;		
			this.enviado = enviado;
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
			Socket cliente = socket.accept();
			System.out.println(socket + " accepted connection : " + cliente);
			byte [] mybytearray  = new byte [(int)enviado.length()];
			FileInputStream fis = new FileInputStream(enviado);
			BufferedInputStream bis = new BufferedInputStream(fis);
			bis.read(mybytearray,0,mybytearray.length);
			OutputStream  os = cliente.getOutputStream();
			System.out.println("Sending "  + "(" + mybytearray.length + " bytes)");
			os.write(mybytearray,0,mybytearray.length);
			os.flush();
			bis.close();
			System.out.println("Done.");

		} 
		catch (IOException e) 
		{
			e.printStackTrace();
		}

	}


	@Override
	public void run() 
	{
		recibir();
	}
}
