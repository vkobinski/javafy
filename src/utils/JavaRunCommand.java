package utils;
import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class JavaRunCommand {

	public static String quote(String s) {
		return new StringBuilder()
				.append("\"")
				.append(s)
				.append("\"")
		        .toString();
	}
	
    public ArrayList<String> getMetadata(File song){
        String s = null;
        ArrayList<String> result = new ArrayList<String>();
        File script = new File("script.py");	
        	
        try {
        	String comando = " C:\\Users\\games\\AppData\\Local\\Programs\\Python\\Python310\\python.exe " + script.getAbsolutePath() + " " + song.toString();
        	Process p = Runtime.getRuntime().exec(comando);
            
            BufferedReader stdInput = new BufferedReader(new 
                 InputStreamReader(p.getInputStream()));

            BufferedReader stdError = new BufferedReader(new 
                 InputStreamReader(p.getErrorStream()));

            while ((s = stdInput.readLine()) != null) {
                result.add(s);
            }
            while ((s = stdError.readLine()) != null) {
                System.out.println(s);
            }
        }
        catch (IOException e) {
            System.out.println("exception happened - here's what I know: ");
            e.printStackTrace();
        }
        return result;
    }
}