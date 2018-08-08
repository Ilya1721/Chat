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

public class Client extends Application
{
	private static final int PORT = 7777;
	private String host = "localhost";
	private Socket client;
	private VBox main_layout;
	private Label you;
	private Label him;
	private TextField message_out;
	private Scene scene;
	private Button close_button;
	private Button send_button;
	private HBox button_layout;
	
	public void start(Stage my_stage) 
	{
		init_ui();
		open_connection();
		hand_shake();
		send_data();
		get_data();
		close_connection();
		
		my_stage.setScene(scene);
		my_stage.setTitle("Client");
		my_stage.show();
	}
	
	public static void main(String args[]) 
	{
		launch(args);
	}
	
	private void get_data() 
	{
		while(true) 
		{
			try 
			{
				System.out.println("Start getting data");
				BufferedReader data_in = new BufferedReader(new InputStreamReader(client.getInputStream()));
				System.out.println("Get Buffered Reader");
				String line_from_server = data_in.readLine();
				if(line_from_server == "") 
				{
					System.out.println("Server has not sent data");
				}
				him.setText("Him: " + line_from_server);
				System.out.println(line_from_server);
				//data_in.close();
			}
			catch(Exception exc) 
			{
				System.out.println("Error at getting data from server");
				System.out.println(exc.toString());
			}
		}
	}
	
	private void send_data() 
	{
		send_button.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event)
			{
				try 
				{
					PrintWriter data_out = new PrintWriter(client.getOutputStream());
					you.setText("You: " + message_out.getText());
					data_out.println(message_out.getText());
					data_out.flush();
				}
				catch(Exception exc) 
				{
					System.out.println("Error at sending data to server");
					System.out.println(exc.toString());
				}
			}
		});
	}
	
	private void close_connection() 
	{
		close_button.setOnAction(new EventHandler<ActionEvent>() 
		{
			public void handle(ActionEvent event) 
			{
				try 
				{
					System.out.println("Closing connection");
					client.close();
				}
				catch(Exception exc) 
				{
					System.out.println("Error at closing connection");
					System.out.println(exc.toString());
				}
			}
		});
	}
	
	private void open_connection() 
	{
		try 
		{
			System.out.println("Opening connection");
			client = new Socket(host, PORT);
		}
		catch(Exception exc) 
		{
			System.out.println("Error at opening connection");
			System.out.println(exc.toString());
		}
	}
	
	private void init_ui() 
	{
		main_layout = new VBox();
		him = new Label("Him: ");
		you = new Label("You: ");
		message_out = new TextField();
		close_button = new Button("Close");
		send_button = new Button("Send");
		button_layout = new HBox();
		scene = new Scene(main_layout, 800, 600);
		button_layout.getChildren().addAll(close_button, send_button);
		button_layout.setAlignment(Pos.CENTER);
		main_layout.getChildren().addAll(you, him, message_out, button_layout);	
	}
	
	private void hand_shake() 
	{
		try 
		{
			PrintWriter data_out = new PrintWriter(client.getOutputStream());
			data_out.println("Connected");
			data_out.flush();
		}
		catch(Exception exc) 
		{
			System.out.println("Error at handshaking");
			System.out.println(exc.toString());
		}
	}
}




















