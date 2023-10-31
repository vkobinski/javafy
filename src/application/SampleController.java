package application;

import java.awt.Cursor;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collections;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import org.json.JSONArray;
import org.json.JSONObject;

import animatefx.animation.FadeIn;
import animatefx.animation.FadeOut;
import animatefx.animation.SlideInLeft;
import animatefx.animation.SlideOutRight;
import javafx.application.Platform;
import javafx.concurrent.Task;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.VBox;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;
import javafx.util.Duration;
import utils.ImageDownloader;
import utils.JavaRunCommand;

public class SampleController implements Initializable {

	@FXML
	private AnchorPane pane;
	@FXML
	private Label songLabel;
	@FXML
	private Button playlistBtn;
	@FXML
	private Label musicTime;
	@FXML
	private Button playButton;
	@FXML
	private Button previousButton;
	@FXML
	private Button pauseButton;
	@FXML
	private Button nextButton;
	@FXML
	private Slider volumeSlider;
	@FXML
	private Slider progressSlider;
	@FXML
	private ImageView playPauseImage;
	@FXML
	private ImageView albumCover;
	@FXML
	private ImageView backgroundImage;
	@FXML
	private TextArea lyricArea;
	@FXML
	private Label songLabelName;
	@FXML
	private Label artistLabelName;
	@FXML
	private Label songYearLabel;
	@FXML
	private ImageView muteButton;
	@FXML
	private ComboBox<String> playlistList;
	@FXML
	private VBox playlistAtualTocando;
	@FXML
	private Button shuffle;

	private Media media;
	private MediaPlayer mediaPlayer;

	private File directory;
	private File[] files;

	private Image playImage;
	private Image pauseImage;
	private Image coverImage;
	private Image backImage;

	private ArrayList<File> songs;

	private int songNumber;

	private Timer timer;
	private TimerTask task;
	private boolean running = false;

	private File diretorio;
	private ArrayList<File> playlists;
	private File[] arquivos;

	private File directoryMusic;
	private ArrayList<File> songsPlaylist;
	private File[] arquivosPlaylist;

	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {

		playlists = new ArrayList<File>();
		diretorio = new File("playlists");
		arquivos = diretorio.listFiles();
		playlistList.getItems().add("Todas");
		if (arquivos != null) {
			for (File file : arquivos) {
				playlists.add(file);
			}
			for (File playlist : playlists) {
				playlistList.getItems().add(playlist.toString());
			}
		}

		lyricArea.setDisable(false);
		lyricArea.setEditable(false);
		backgroundImage.setPreserveRatio(false);
		songs = new ArrayList<File>();
		directory = new File("music");
		files = directory.listFiles();
		if (files != null) {
			for (File file : files) {
				songs.add(file);
			}
		}

		URL playPng = getClass().getResource("/images/play.png");
		URL pausePng = getClass().getResource("/images/pause.png");

		try {
			playImage = new Image(playPng.toURI().toString());
			pauseImage = new Image(pausePng.toURI().toString());
		} catch (Exception ex) {
			System.out.println(ex);
		}

		songName();

//		volumeSlider.valueProperty().addListener(new ChangeListener<Number>() {
//
//			@Override
//			public void changed(ObservableValue<? extends Number> arg0, Number arg1, Number arg2) {
//				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
//			}
//
//		});

		volumeSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, new javafx.event.EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
			}

		});
		progressSlider.addEventFilter(MouseEvent.MOUSE_RELEASED, new javafx.event.EventHandler<MouseEvent>() {

			@Override
			public void handle(MouseEvent event) {
				changeTime(progressSlider.getValue());
			}

		});

		atualizarVisor();

	}

	public void inicializar() {

		playlistList.getItems().clear();

		playlists = new ArrayList<File>();
		diretorio = new File("playlists");
		arquivos = diretorio.listFiles();
		playlistList.getItems().add("Todas");
		if (arquivos != null) {
			for (File file : arquivos) {
				playlists.add(file);
			}
			for (File playlist : playlists) {
				playlistList.getItems().add(playlist.toString());
			}
		}

	}

	public void songName() {
		media = new Media(songs.get(songNumber).toURI().toString());
		mediaPlayer = new MediaPlayer(media);
		if (songs.get(songNumber).getName().length() > 26) {
			new SlideOutRight(songLabel).play();
			String songName = songs.get(songNumber).getName().substring(0, 23);
			songName = songName + "...";
			songLabel.setText(songName);
			new SlideInLeft(songLabel).play();
		} else {
			new SlideOutRight(songLabel).play();
			songLabel.setText(songs.get(songNumber).getName());
			new SlideInLeft(songLabel).play();
		}
		return;
	}

	public void songNameShazam(String song, String artist, String songYear) {
		artistLabelName.setText("Artista: " + artist);
		songLabelName.setText("Música: " + song);
		songYearLabel.setText("Ano: " + songYear);
		new FadeIn(artistLabelName).play();
		new FadeIn(songLabelName).play();
		new FadeIn(songYearLabel).play();
	}

	public void mudarPlaylist() {
		if (playlistList.getValue().toString() == "Todas") {
			songs = new ArrayList<File>();
			directory = new File("music");
			files = directory.listFiles();
			if (files != null) {
				for (File file : files) {
					songs.add(file);
				}
			}
			if (running) {
				songNumber = 0;
				mediaPlayer.stop();
				songName();
				mediaPlayer.play();
				getMetadata();
			} else {
				songNumber = 0;
				songName();
				playMedia();
				getMetadata();
			}
			playlistAtualTocando.getChildren().clear();
			atualizarVisor();
			return;
		}

		File playlist = new File(playlistList.getValue().toString());
		ArrayList<File> musicasDaPlaylist = new ArrayList<File>();
		String st;
		try (BufferedReader br = new BufferedReader(new FileReader(playlist));) {
			while ((st = br.readLine()) != null) {
				musicasDaPlaylist.add(new File(st));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		if (running) {
			songs = musicasDaPlaylist;
			songNumber = 0;
			mediaPlayer.stop();
			songName();
			mediaPlayer.play();
			getMetadata();
		} else {
			songs = musicasDaPlaylist;
			songNumber = 0;
			songName();
			playMedia();
			getMetadata();
		}
		playlistAtualTocando.getChildren().clear();
		atualizarVisor();

	}

	public void changeTime(double time) {
		double user = progressSlider.getValue();
		double end = media.getDuration().toSeconds();
		double porcentagem = (end * (user / 100));
		mediaPlayer.seek(Duration.seconds(porcentagem));
	}
	
	public void atualizarVisor() {
		for (File file : songs) {
			Label nova = new Label(file.toString());
			nova.addEventFilter(MouseEvent.MOUSE_CLICKED, new EventHandler<Event>() {
				@Override
				public void handle(Event arg0) {
					mudarMusicaClick(nova);
				}
			});;
			playlistAtualTocando.getChildren().add(nova);
		}
	}

	public void mudarMusicaClick(Label clicada) {
		for(int i = 0; i < songs.size(); i++) {
			File file = songs.get(i);
			if(file.toString() == clicada.getText()) {
				songNumber = i;
				nextMedia();
				previousMedia();
			}
		}
	}
	
	public void gerenciarPlaylist() {
		try {
			AnchorPane root = (AnchorPane) FXMLLoader.load(getClass().getResource("Playlist.fxml"));
			Stage stage = new Stage();
			stage.setTitle("Gerenciando Playlists");
			Scene cena = new Scene(root, 600, 600);
			cena.getStylesheets().add(getClass().getResource("playlist.css").toExternalForm());
			stage.setScene(cena);
			stage.setOnCloseRequest(new EventHandler<WindowEvent>() {

				@Override
				public void handle(WindowEvent arg0) {
					inicializar();

				}
			});
			stage.show();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	public void shuffleMedia() {
		Collections.shuffle(songs);
		playlistAtualTocando.getChildren().clear();
		atualizarVisor();
		songNumber = 0;
		nextMedia();
		previousMedia();
	}

	public void playMedia() {
		if (!running) {
			timer = null;
			beginTimer();
			playPauseImage.setImage(pauseImage);
			mediaPlayer.play();
			getMetadata();
		} else {
			cancelTimer();
			playPauseImage.setImage(playImage);
			mediaPlayer.pause();
		}
	}

	public void changeAlbumCover(String url, String url2) {
		try {
			System.out.println(url);
			System.out.println(url2);
			boolean exists = new File("covers/"
					+ songs.get(songNumber).toString().replace(".mp3", "").replace("music/", "") + "Cover.jpg")
							.exists();
			boolean exists2 = new File("covers/"
					+ songs.get(songNumber).toString().replace(".mp3", "").replace("music/", "") + "Background.jpg")
							.exists();
			if (!exists || !exists2) {
				ImageDownloader.saveImage(url, 1,
						songs.get(songNumber).toString().replace(".mp3", "").replace("music/", ""));
				ImageDownloader.saveImage(url2, 2,
						songs.get(songNumber).toString().replace(".mp3", "").replace("music/", ""));
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	@FXML
	public void mute() {

		if (mediaPlayer.getVolume() == 0) {
			volumeSlider.setValue(100);
			mediaPlayer.setVolume(1);
		} else {
			volumeSlider.setValue(0);
			mediaPlayer.setVolume(0);
		}
	}

	public void getMetadata() {
		Task<Void> task = new Task<Void>() {
			@Override
			public Void call() {
				try {

					JavaRunCommand jrc = new JavaRunCommand();
					String results = jrc.getMetadata(songs.get(songNumber)).get(0);
					JSONObject jo = new JSONObject(results);
					System.out.println(jo);
					String url = jo.getJSONObject("track").getJSONObject("images").get("coverarthq").toString();
					String url2 = jo.getJSONObject("track").getJSONObject("images").getString("background").toString();
					String songName = jo.getJSONObject("track").getString("title");
					String artistName = jo.getJSONObject("track").getString("subtitle");
					String songYear = "";
					try {
						songYear = jo.getJSONObject("track").getJSONArray("sections").getJSONObject(0)
								.getJSONArray("metadata").getJSONObject(2).getString("text");
					} catch (Exception e) {
						e.printStackTrace();
					}

					System.out.println(songName + " " + artistName + " " + songYear);
					try {
						String finalSongYear = songYear;
						Platform.runLater(() -> {
							songNameShazam(songName, artistName, finalSongYear);
						});
						JSONArray name = jo.getJSONObject("track").getJSONArray("sections").getJSONObject(1)
								.getJSONArray("text");
						String lyric = "";
						for (Object linha : name) {
							lyric = lyric + linha;
							lyric = lyric + "\n";
						}
						new FadeOut(lyricArea).play();
						lyricArea.setText(lyric);
						new FadeIn(lyricArea).play();
					} catch (Exception e) {
						lyricArea.setText("No lyric found");
					}

				try {
					changeAlbumCover(url, url2);
				} catch (Exception e) {
					e.printStackTrace();
				}
				return null;
				} catch (Exception e ) {
					e.printStackTrace();
				}


				return null;
			}
		};
		new Thread(task).start();
	}

	public void nextMediaSemPlay() {
		if (songNumber < songs.size() - 1) {
			songNumber++;
			mediaPlayer.stop();
			if (running) {
				cancelTimer();
			}
		} else {
			songNumber = 0;
			mediaPlayer.stop();
			if (running) {
				cancelTimer();
			}
		}
	}

	@FXML
	public void nextMedia() {
		if (songNumber < songs.size() - 1) {
			songNumber++;
			mediaPlayer.stop();
			if (running) {
				cancelTimer();
			}
			songName();
			playMedia();
		} else {
			songNumber = 0;
			mediaPlayer.stop();
			if (running) {
				cancelTimer();
			}
			songName();
			playMedia();
		}
	}

	public void previousMedia() {
		if (songNumber > 0) {
			songNumber--;
			mediaPlayer.stop();
			if (running) {
				cancelTimer();
			}
			songName();
			playMedia();

		} else {
			songNumber = songs.size() - 1;
			mediaPlayer.stop();
			if (running) {
				cancelTimer();
			}
			songName();
			playMedia();
		}
	}

	public void beginTimer() {
		timer = new Timer();
		task = new TimerTask() {
			@Override
			public void run() {
				running = true;
				double current = mediaPlayer.getCurrentTime().toSeconds();
				double end = media.getDuration().toSeconds();
				Platform.runLater(() -> {
					for (Node label : playlistAtualTocando.getChildren()) {
						label = (Label) label;
						label.setId("none");
						label.applyCss();
					}
					playlistAtualTocando.getChildren().get(songNumber).setId("musicaSelecionada");
					playlistAtualTocando.getChildren().get(songNumber).applyCss();

				});
				Platform.runLater(() -> {
					URL coverPng;
					URL backgroundPng;
					try {
						boolean existsCover = new File(
								"covers/" + songs.get(songNumber).toString().replace(".mp3", "").replace("music/", "")
										+ "Cover.jpg").exists();
						boolean existsBack = new File(
								"covers/" + songs.get(songNumber).toString().replace(".mp3", "").replace("music/", "")
										+ "Background.jpg").exists();

						if (existsCover && existsBack) {
							coverPng = new File("covers/"
									+ songs.get(songNumber).toString().replace(".mp3", "").replace("music/", "")
									+ "Cover.jpg").toURI().toURL();
							coverImage = new Image(coverPng.toURI().toString());
							albumCover.setImage(coverImage);
							backgroundPng = new File("covers/"
									+ songs.get(songNumber).toString().replace(".mp3", "").replace("music/", "")
									+ "Background.jpg").toURI().toURL();
							backImage = new Image(backgroundPng.toURI().toString());
							backgroundImage.setImage(backImage);
						} else {
							Image cPng = null, bPng = null;

							URL coverURL = getClass().getResource("/images/200.gif");
							URL backURL = getClass().getResource("/images/bdcf5656423c10a27ea82ae880f5488e.png");
							try {
								cPng = new Image(coverURL.toURI().toString());
								bPng = new Image(backURL.toURI().toString());
							} catch (Exception e) {
								System.out.println(e);
							}

							coverImage = cPng;
							albumCover.setImage(coverImage);
							backImage = bPng;
							backgroundImage.setImage(backImage);
						}

					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				});
				Platform.runLater(() -> {
					mediaPlayer.setVolume(volumeSlider.getValue() * 0.01);
				});
				Platform.runLater(() -> {
					int minutosAtuais = (int) (current / 60);
					int segundosAtuais = (int) (current % 60);
					int minutosTotais = (int) (end / 60);
					int segundosTotais = (int) (end % 60);
					String timer = new String("%02d:%02d/%02d:%02d");
					timer = String.format(timer, minutosAtuais, segundosAtuais, minutosTotais, segundosTotais);
					musicTime.setText(timer);
				});
				progressSlider.setValue((current * 100) / end);
				if (current / end == 1) {
					Platform.runLater(() -> {
						nextMedia();
					});
				}
			}
		};

		timer.scheduleAtFixedRate(task, 1000, 1000);

	}

	public void cancelTimer() {
		running = false;
		timer.cancel();
	}
}
