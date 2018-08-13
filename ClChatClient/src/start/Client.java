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

public class Client extends Application
{
	private static final int PORT = 7777;
	private final String host = "localhost";
	private final int width = 800;
	private final int height = 600;
	private Socket client;
	private Scene scene;
	private VBox main_layout;
	private HBox button_layout;
	private HBox registrate_layout;
	private HBox connect_layout;
	private TextField outcoming_field;
	private TextField name_field;
	private TextArea incoming_field;
	private Button close_button;
	private Button send_button;
	private Button registrate_button;
	private Button connect_button;
	private String user_name = "Please register your name";
	private String line_from_server;
	private String line_to_server;
	private Thread connection_thread;
	private Thread keep_thread;
	private BufferedReader incoming;
	private PrintWriter outcoming;
	
	public void start(Stage my_stage) 
	{
		init_ui(width, height);
		
		my_stage.setTitle("Client");
		my_stage.setScene(scene);
		my_stage.show();
		my_stage.setOnCloseRequest(new EventHandler<WindowEvent>() 
		{
			public void handle(WindowEvent event) 
			{
				try 
				{
					System.out.println("Setting closing event");
					if(client != null) 
					{
						client.close();
					}
					close_streams();
					if(connection_thread.isAlive()) 
					{
						connection_thread.stop();
					}
					if(keep_thread.isAlive()) 
					{
						keep_thread.stop();
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
		System.out.println("Init ui");
		main_layout = new VBox();
		button_layout = new HBox();
		registrate_layout = new HBox();
		connect_layout = new HBox();
		incoming_field = new TextArea();
		outcoming_field = new TextField();
		name_field = new TextField();
		send_button = new Button("Send");
		close_button = new Button("Close");
		registrate_button = new Button("Registrate");
		connect_button = new Button("Connect");
		button_layout.getChildren().addAll(send_button, close_button);
		registrate_layout.getChildren().addAll(registrate_button);
		connect_layout.getChildren().addAll(connect_button);
		main_layout.getChildren().addAll(incoming_field, outcoming_field, button_layout, 
				name_field, registrate_layout, connect_layout);
		button_layout.setAlignment(Pos.CENTER);
		registrate_layout.setAlignment(Pos.CENTER);
		connect_layout.setAlignment(Pos.CENTER);
		scene = new Scene(main_layout, w, h);
		set_buttons();
	}
	
	private void set_buttons() 
	{
		try 
		{
			System.out.println("Setting send button");
			send_button.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{
					try 
					{
						System.out.println("Sending data from client to server");
						line_to_server = outcoming_field.getText();
						if(line_to_server != "") 
						{
							incoming_field.appendText(user_name + ": " + line_to_server + "\n");
							outcoming.println(user_name + ": " + line_to_server);
							outcoming.flush();
						}
					}
					catch(Exception exc) 
					{
						System.out.println("Error at sending data to server");
						System.out.println(exc.toString());
					}
				}
			});
			System.out.println("Setting close button");
			close_button.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{
					try 
					{
						System.out.println("Closing connection with server");
						if(client != null) 
						{
							client.close();
						}
					}
					catch(Exception exc) 
					{
						System.out.println("Error at closing connection with server");
						System.out.println(exc.toString());
					}
				}
			});
			System.out.println("Setting registrate button");
			registrate_button.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{
					try 
					{
						System.out.println("Registrating name");
						String name_field_text = name_field.getText();
						if(name_field_text != "") 
						{
							user_name = name_field_text;
						}
					}
					catch(Exception exc) 
					{
						System.out.println("Error at registrating name");
						System.out.println(exc.toString());
					}
				}
			}); 
			System.out.println("Setting connect button");
			connect_button.setOnAction(new EventHandler<ActionEvent>() 
			{
				public void handle(ActionEvent event) 
				{
					open_connection();
				}
			});
		}
		catch(Exception exc) 
		{
			System.out.println("Error at setting buttons");
			System.out.println(exc.toString());
		}
	}
	
	private void open_connection() 
	{
		connection_thread = new Thread(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					System.out.println("Connecting to server");
					client = new Socket(host, PORT);
				}
				catch(Exception exc) 
				{
					System.out.println("Error at connection to server");
					System.out.println(exc.toString());
				}
				System.out.println("Connected to server");
				set_streams();
				keep_working();
				while(true) 
				{
					get_data();
				}
			}
		});
		connection_thread.start();
	}
	
	private void get_data() 
	{
		try 
		{
			System.out.println("Getting data from server");
			line_from_server = incoming.readLine();
			if(!line_from_server.isEmpty()) 
			{
				incoming_field.appendText(line_from_server + "\n");
			}
		}
		catch(Exception exc) 
		{
			System.out.println("Error at getting data from server");
			System.out.println(exc.toString());
			connection_thread.stop();
		}
 	}
	
	private void keep_working() 
	{
		keep_thread = new Thread(new Runnable() 
		{
			public void run() 
			{
				try 
				{
					while(true) 
					{
						System.out.println("Sending empty string");
						outcoming.println("");
						outcoming.flush();
						Thread.sleep(500);
					}
				}
				catch(Exception exc) 
				{
					System.out.println("Error at keeping sending empty string");
					System.out.println(exc.toString());
					keep_thread.stop();
				}
			}
		});
		keep_thread.start();
	}
	
	private void set_streams() 
	{
		try 
		{
			System.out.println("Setting streams with server");
			incoming = new BufferedReader
					(new InputStreamReader(client.getInputStream()));
			outcoming = new PrintWriter(client.getOutputStream());
		}
		catch(Exception exc) 
		{
			System.out.println("Error at setting streams with server");
			System.out.println(exc.toString());
		}
	}
	
	private void close_streams() 
	{
		try 
		{
			System.out.println("Closing streams with server");
			if(client != null) 
			{
				incoming.close();
				outcoming.close();
			}
		}
		catch(Exception exc) 
		{
			System.out.println("Error at closing streams with server");
			System.out.println(exc.toString());
		}
	}	
}




















