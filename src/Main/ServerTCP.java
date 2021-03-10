package Main;

import java.net.*;
import java.util.Scanner;
import java.io.*;
public class ServerTCP 
{

	
	public static void main(String argv[]) 
	{

		Scanner lector = new Scanner(System.in);	
		System.out.println("Digite el archivo que quiere enviar");
		String archivo = lector.nextLine() ;
		String ruta="";
		if(archivo=="1") {
			ruta="data/archivo1.txt";
		}
		else {
			ruta="data/archivo2.txt";
		}			
		System.out.println("Digite la cantidad de clientes que recibiran el archivo");
		int cantidad =Integer.parseInt(lector.nextLine());
		ServerThread newThread=new ServerThread(1,ruta);
		newThread.start();
		
		
		
	}
}