package application;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class PlaylistController implements Initializable {

	@FXML
	private ComboBox<String> playlistList;
	@FXML
	private VBox musicBox;
	@FXML
	private Button addToPlaylist;
	@FXML
	private Button newPlaylistButton;
	@FXML
	private Label adicionado;
	@FXML
	private Button apagarBtn;
	
	private File directory;
	private ArrayList<File> playlists;
	private File[] files;
	
	private ArrayList<CheckBox> checkboxes;
	
	private File directoryMusic;
	private ArrayList<File> songs;
	private File[] arquivos;
	
	
	public void initialize(URL arg0, ResourceBundle arg1) {
		checkboxes = new ArrayList<CheckBox>();
		playlists = new ArrayList<File>();
		directory = new File("playlists");
		files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				playlists.add(file);
			}
			for(File playlist : playlists) {
				playlistList.getItems().add(playlist.toString());
			}
		}
		songs = new ArrayList<File>();
		directoryMusic = new File("music");
		arquivos = directoryMusic.listFiles();
		if (arquivos != null) {
			for (File file : arquivos) {
				songs.add(file);
			}
			for(File musica : songs) {
				CheckBox cb = new CheckBox();
				cb.setText(musica.toString());
				cb.addEventHandler(MouseEvent.MOUSE_CLICKED, new EventHandler<MouseEvent>(){

					@Override
					public void handle(MouseEvent arg0) {
						disableOthers(cb.getText());
					}
					
				});
				checkboxes.add(cb);
				musicBox.getChildren().add(cb);
			}
		}
		
	}
	
	public void inicializar() {
		playlists = new ArrayList<File>();
		directory = new File("playlists");
		files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				playlists.add(file);
			}
			for(File playlist : playlists) {
				playlistList.getItems().add(playlist.toString());
			}
		}
		
	}
	
	public void apagarPlaylist() {
		File apagar = new File(playlistList.getValue().toString());
		apagar.delete();
		adicionado.setText("Playlist Apagada!");
	}
	
	public void disableOthers(String name) {
		for(CheckBox cb : checkboxes) {
			cb.setSelected(false);
		}
		for(CheckBox cb : checkboxes) {
			if(cb.getText() == name) {
				cb.setSelected(true);
			}
		}
	}
	
	public void addMusic() {
		File playlist = new File(playlistList.getValue());
		CheckBox selected = null;
		for(CheckBox cb : checkboxes) {
			if( cb.isSelected()) {
				selected = cb;
			}
		}
		try(
				FileWriter f = new FileWriter(playlist, true);
				BufferedWriter b = new BufferedWriter(f);
				PrintWriter p = new PrintWriter(b);){
				
				p.println(selected.getText());
				
		}catch(IOException i) {
			adicionado.setText(i.getMessage());
		}
		adicionado.setText("Música adicionada!");
	}
	
	public void newPlaylist() {
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("newPlay.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Criar nova Playlist");
			stage.setScene(new Scene(root, 323,200));
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {
				
				@Override
				public void handle(WindowEvent arg0) {
					playlistList.getItems().clear();
					inicializar();
				}
			});
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
	/*
	 * public void beginTimer() { timer = new Timer(); task = new TimerTask() {
	 * 
	 * @Override public void run() { } } timer.scheduleAtFixedRate(task, 1000,
	 * 1000); }
	 * 
	 * public void cancelTimer() { running = false; timer.cancel(); }
	 */
}
