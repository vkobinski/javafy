package application;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;

public class newPlayController implements Initializable {

	@FXML
	private javafx.scene.control.TextField name;
	@FXML
	private Button newButton;
	@FXML
	private Label adicionado;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

	}

	public void newPlaylist() {
		File file = new File("playlists/" + name.getText()); // initialize File object and passing path as argument
		boolean result;
		try {
			result = file.createNewFile(); // creates a new file
			if (result){
				adicionado.setText("Playlist Criada!");; // returns the path string
			} else {
				adicionado.setText("Falha ao criar playlist");
			}
		} catch (IOException e) {
			e.printStackTrace(); // prints exception if any
		}
	}

}
