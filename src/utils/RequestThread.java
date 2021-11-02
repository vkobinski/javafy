package utils;

import java.io.File;
import java.util.ArrayList;

public class RequestThread implements Runnable {

	private File song;
	private String metadata;
	
	public RequestThread(File song) {
		this.song = song;
	}
	
	public void run() {
		ArrayList<String> jrc =  new JavaRunCommand().getMetadata(song);
		metadata = jrc.get(0);
		System.out.println(metadata);
	}
	
	public String getMetadata() {
		return metadata;
	}
	
}
