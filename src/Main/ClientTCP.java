package Main;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.Socket;

public class ClientTCP {
	private static DataOutputStream dataOutputStream = null;
	private static DataInputStream dataInputStream = null;
	private static int id;
//cuando se pase a threads quitar el static
	
	
	public static void main(String[] args) throws IOException {
		try(Socket socket = new Socket("localhost",5555)) {
			id=1;
			dataInputStream = new DataInputStream(socket.getInputStream());
			dataOutputStream = new DataOutputStream(socket.getOutputStream());
			receiveFile("NewFile1.txt");
			dataInputStream.close();
			dataInputStream.close();
		}catch (Exception e){
			dataOutputStream.write(400);
			e.printStackTrace();
		}
	}

	private static void receiveFile(String fileName) throws Exception{
		int bytes = 0;
		FileOutputStream fileOutputStream = new FileOutputStream(fileName);

		long size = dataInputStream.readLong();
		dataOutputStream.write(id);
		
		byte[] buffer = new byte[4*1024];
		while (size > 0 && (bytes = dataInputStream.read(buffer, 0, (int)Math.min(buffer.length, size))) != -1) {
			fileOutputStream.write(buffer,0,bytes);
			size -= bytes;      
		}
		dataOutputStream.write(200);
		System.out.println("File Received");
		fileOutputStream.close();
	}
}
