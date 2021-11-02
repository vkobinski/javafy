package utils;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

import org.asynchttpclient.AsyncCompletionHandler;
import org.asynchttpclient.AsyncHttpClient;
import org.asynchttpclient.Dsl;
import org.asynchttpclient.HttpResponseBodyPart;
import org.asynchttpclient.Response;

public class ImageDownloader {
	
	public static void saveImage(String imageUrl, int type, String songName) throws IOException {
		File destinationFile;
		
		if(type == 1) {
			destinationFile = new File("covers/" + songName + "Cover.jpg");
			if(!destinationFile.exists()) {
				destinationFile.getParentFile().mkdirs();
				destinationFile.createNewFile();
			}
		}else {
			destinationFile = new File("covers/" + songName + "Background.jpg");
			if(!destinationFile.exists()) {
				destinationFile.getParentFile().mkdirs();
				destinationFile.createNewFile();
			}
		}
		
		AsyncHttpClient client = Dsl.asyncHttpClient();
		FileOutputStream stream = new FileOutputStream(destinationFile.toString());
		client.prepareGet(imageUrl).execute(new AsyncCompletionHandler<FileOutputStream>() {

		    @Override
		    public State onBodyPartReceived(HttpResponseBodyPart bodyPart) 
		      throws Exception {
		        stream.getChannel().write(bodyPart.getBodyByteBuffer());
		        return State.CONTINUE;
		    }

		    @Override
		    public FileOutputStream onCompleted(Response response) throws Exception {
		        return stream;
		    }
		});

	}
}
