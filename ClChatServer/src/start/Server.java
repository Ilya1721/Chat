package start;
import java.net.*;
import java.util.Scanner;
import java.io.*;
import javafx.application.*;
import javafx.stage.*;
import javafx.scene.*;
import javafx.scene.layout.*;
import javafx.scene.input.*;
import javafx.event.*;
import javafx.scene.control.*;
import javafx.geometry.Pos;
import javafx.concurrent.*;

public class Server extends Application
{	
	private ServerSocket server;
	private Socket[] client;
	private static final int PORT = 7777;
	private static final int client_count = 2;
	private final int width = 800;
	private final int height = 600;
	private String host = "localhost";
	private VBox main_layout;
	private Scene scene;
	private Thread connection_thread;
	private String line_from_client;
	private BufferedReader incoming[];
	private PrintWriter outcoming[];
	
	public void start(Stage my_stage) 
	{
		init_ui(width, height);
		open_connection();
		
		my_stage.setTitle("Server");
		my_stage.setScene(scene);
		my_stage.show();
		my_stage.setOnCloseRequest(new EventHandler<WindowEvent>() 
		{
			public void handle(WindowEvent event) 
			{
				try 
				{
					System.out.println("Setting closing event");
					close_streams();
					if(server != null) 
					{
						server.close();
					}
					if(client[0] != null) 
					{
						client[0].close();
					}
					if(client[1] != null) 
					{
						client[1].close();
					}
					if(connection_thread.isAlive()) 
					{
						connection_thread.stop();
					}
				}
				catch(Exception exc) 
				{
					System.out.println("Error at closing event");
					System.out.println(exc.toString()); 
				}
			}
		});
	}
	
	public static void main(String args[]) 
	{
		launch(args);
	}
	
	private void init_ui(int w, int h) 
	{
		System.out.println("Setting ui");
		main_layout = new VBox();
		scene = new Scene(main_layout, w, h);
	}
	
	private void open_connection() 
	{
		connection_thread = new Thread(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					System.out.println("Opening server");
					server = new ServerSocket(PORT);
					client = new Socket[client_count];
					for(int i = 0; i < client_count; ++i) 
					{
						System.out.println("Waiting for clients to connect");
						client[i] = server.accept();
					}
				}
				catch(Exception exc) 
				{
					System.out.println("Error at opening connection at server side");
					System.out.println(exc.toString());
				}
				System.out.println("Clients connected");
				set_streams();
				while(true) 
				{
					get_data(0);
					send_data(1);
					get_data(1);
					send_data(0);
				}
			}
		});
		connection_thread.start();
	}
	
	private void get_data(int i) 
	{
		try 
		{
			System.out.println("Getting data from client");
			line_from_client = incoming[i].readLine();	
		}
		catch(Exception exc) 
		{
			System.out.println("Error at getting data from client to server");
			System.out.println(exc.toString());
			connection_thread.stop();
		}
	}
	
	private void send_data(int i) 
	{
		try 
		{
			System.out.println("Sending data to client from server");
			if(line_from_client != "")
			{	
				outcoming[i].println(line_from_client);
				outcoming[i].flush();
			}
		}
		catch(Exception exc) 
		{
			System.out.println("Error at sending data to client");
			System.out.println(exc.toString());
		}
	}
	
	private void set_streams() 
	{
		try 
		{
			incoming = new BufferedReader[client_count];
			for(int i = 0; i < client_count; ++i) 
			{
				incoming[i] = new BufferedReader
						(new InputStreamReader(client[i].getInputStream()));
			}
			outcoming = new PrintWriter[client_count];
			for(int i = 0; i < client_count; ++i) 
			{
				outcoming[i] = new PrintWriter(client[i].getOutputStream());
			}
		}
		catch(Exception exc) 
		{
			System.out.println("Error at setting streams with clients");
			System.out.println(exc.toString());
		}
	}
	
	private void close_streams() 
	{
		try 
		{
			for(int i = 0; i < client_count; ++i) 
			{
				if(client[i] != null) 
				{
					outcoming[i].close();
					incoming[i].close();
				}
			}
		}
		catch(Exception exc) 
		{
			System.out.println("Error at closing streams with clients");
			System.out.println(exc.toString());
		}
	}
}

	
	


































