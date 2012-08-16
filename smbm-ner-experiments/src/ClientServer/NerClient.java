package ClientServer;

import java.io.*;
import java.net.*;

import IO.InFile;

public class NerClient {
	public static void main(String[] args) throws IOException{
		InFile in=new InFile(args[0]);
		String line=in.readLine();
		StringBuffer text=new StringBuffer();
		while(line!=null){
			text.append(line+"\n");
			in.readLine();
		}
		in.close();
		System.out.println(getAnnotation(text.toString(),args[1],Integer.parseInt(args[2])));
	}
	
    public static String getAnnotation(String s,String machine,int port) throws IOException {
        Socket nerSocket = null;
        PrintWriter out = null;
        BufferedReader in = null;

        try {
            nerSocket = new Socket(machine,port);
            out = new PrintWriter(nerSocket.getOutputStream(), true);
            in = new BufferedReader(new InputStreamReader(nerSocket.getInputStream()));
        } catch (UnknownHostException e) {
            System.err.println("Don't know about host: localhost.");
            System.exit(1);
        } catch (IOException e) {
            System.err.println("Couldn't get I/O for the connection to: localhost.");
            System.exit(1);
        }


        out.println(s+"\n*endinput*");
        String output=in.readLine();
        out.close();
        in.close();
        nerSocket.close();
        return output;
    }
}
