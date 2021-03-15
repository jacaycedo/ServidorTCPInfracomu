package Main;

import java.util.Scanner;

public class ClientThreadManager 
{
	private final static int serverPort = 5555;
	public static void main(String argv[]) 
	{
		Scanner lector = new Scanner(System.in);	
		System.out.println("Cuantos clientes simultaneos desea implementar");
		int cantidad =Integer.parseInt(lector.nextLine());
		lector.close();
		for (int i = 0; i < cantidad; i++) 
		{
			ClientTCP newThread=new ClientTCP((i+1),serverPort+i);
			newThread.start();
		}
	}
}
