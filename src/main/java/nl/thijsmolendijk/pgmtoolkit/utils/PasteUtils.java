package nl.thijsmolendijk.pgmtoolkit.utils;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.URL;
import java.net.URLConnection;

public class PasteUtils {
	/*
	 * Uploads a string to DPaste and returns the link to it.
	 */
	public static String uploadToDPaste(String data) throws Exception {
		String adress = "content="+data;
		URL url = new URL("http://dpaste.com/api/v1/");
		URLConnection conn = url.openConnection();
		conn.setDoOutput(true);
		OutputStreamWriter writer = new OutputStreamWriter(conn.getOutputStream());
		writer.write(adress);
		writer.flush();
		String line;
		BufferedReader reader = new BufferedReader(new InputStreamReader(conn.getInputStream()));
		while ((line = reader.readLine()) != null) {
		    if (line.contains("<title>")) {
		    	return "http://dpaste.com/"+line.replace("<title>", "").replace("</title>", "").replace("dpaste: #", "").replace("	","");
		    }
		}
		writer.close();
		reader.close(); 
		return "";
	}
}
