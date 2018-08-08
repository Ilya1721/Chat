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

public class Server extends Application
{	
	private ServerSocket server;
	private Socket[] client;
	private static final int PORT = 7777;
	private static final int client_count = 2;
	private String host = "localhost";
	private VBox main_layout;
	private HBox button_layout;
	private Button close_button;
	private Scene scene;
	
	public void start(Stage my_stage) 
	{
		init_ui();
		open_connection();
		send_data();
		close_connection();
		
		my_stage.setScene(scene);
		my_stage.setTitle("Server");
		my_stage.show();
	}
	
	public static void main(String args[]) 
	{
		launch(args);
	}
	
	private void send_data() 
	{
		try 
		{
			System.out.println("Server sending data");
			PrintWriter data_out = new PrintWriter(client[1].getOutputStream());
			String server_string = get_data(client[0]);
			System.out.println(server_string);
			data_out.println(server_string);
			data_out.flush();
			//data_out.close();
			data_out = new PrintWriter(client[0].getOutputStream());
			server_string = get_data(client[1]);
			System.out.println(server_string);
			data_out.println(server_string);
			data_out.flush();
			//data_out.close();
			System.out.println("Server has sent data");
		}
		catch(Exception exc) 
		{
			System.out.println("Error at sending data to client");
			System.out.println(exc.toString());
		}
	}
	
	private void open_connection() 
	{
		try 
		{
			System.out.println("Print test 1");
			client = new Socket[client_count];
			System.out.println("Print test 2");
			server = new ServerSocket(PORT);
			System.out.println("Print test 3");
			for(int i = 0; i < client_count; ++i) 
			{
				client[i] = server.accept();
				System.out.println(client[i].getInetAddress().toString());
				System.out.println("Get connection");
			}
			System.out.println("Server got connection");
		}
		catch(Exception exc) 
		{
			System.out.println("Error at opening connection");
			System.out.println(exc.toString());
		}
	}
	
	private void close_connection() 
	{
		close_button.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				try 
				{
					server.close();
				}
				catch(Exception exc) 
				{
					System.out.println("Error at closing connection");
					System.out.println(exc.toString());
				}
			}
		});
	}
	
	private String get_data(Socket client) 
	{
		try 
		{
			System.out.println("Server getting data");
			System.out.println(client.getInetAddress().toString());
			String message_in;
			BufferedReader data_in;
			data_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			message_in = data_in.readLine();
			//data_in.close();
			if(message_in == "") 
			{
				message_in = "Null Error";
			}
			System.out.println("Server got data");
			return message_in;
		}
		catch(Exception exc) 
		{
			String error = "Error at server getting data";
			System.out.println(exc.toString());
			return error; 
		}
	}
	
	private void init_ui() 
	{
		System.out.println("Init ui");
		main_layout = new VBox();
		button_layout = new HBox();
		close_button = new Button("Close");
		button_layout.getChildren().addAll(close_button);
		button_layout.setAlignment(Pos.CENTER);
		main_layout.getChildren().addAll(button_layout);
		scene = new Scene(main_layout, 800, 600);
	}
}




































