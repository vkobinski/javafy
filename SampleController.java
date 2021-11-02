package application;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
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
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.concurrent.Task;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.Slider;
import javafx.scene.control.TextArea;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;
import javafx.scene.media.Media;
import javafx.scene.media.MediaPlayer;
import javafx.util.Duration;
import utils.ImageDownloader;
import utils.JavaRunCommand;

public class SampleController implements Initializable {

		@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
			
		
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

	public void changeTime(double time) {
		double user = progressSlider.getValue();
		double end = media.getDuration().toSeconds();
		double porcentagem = (end * (user / 100));
		mediaPlayer.seek(Duration.seconds(porcentagem));
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
				JavaRunCommand jrc = new JavaRunCommand();
				String results = jrc.getMetadata(songs.get(songNumber)).get(0);
				JSONObject jo = new JSONObject(results);
				String url = jo.getJSONObject("track").getJSONObject("images").get("coverarthq").toString();
				String url2 = jo.getJSONObject("track").getJSONObject("images").getString("background").toString();
				String songName = jo.getJSONObject("track").getString("title");
				String artistName = jo.getJSONObject("track").getString("subtitle");
				String songYear = jo.getJSONObject("track").getJSONArray("sections").getJSONObject(0)
						.getJSONArray("metadata").getJSONObject(2).getString("text");
				try {
					Platform.runLater(() -> {
						songNameShazam(songName, artistName, songYear);
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

			}
		};
		new Thread(task).start();
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
