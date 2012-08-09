package ClientServer;

import java.net.*;
import java.io.*;

import LBJ2.classify.Classifier;
import LbjTagger.NETagPlain;
import LbjTagger.Parameters;
import LbjTagger.ParametersForLbjCode;

import LbjFeatures.NETaggerLevel1;
import LbjFeatures.NETaggerLevel2;

public class NerServer {
	
	
	private NETaggerLevel1 tagger1=null;
	private NETaggerLevel2 tagger2=null;

	public NerServer(String configFile) throws Exception{
		Parameters.readConfigAndLoadExternalData(configFile);
		ParametersForLbjCode.forceNewSentenceOnLineBreaks=true;
		tagger1 = new NETaggerLevel1();
		System.out.println("Reading model file : "+ Parameters.pathToModelFile+".level1");
		tagger1=(NETaggerLevel1)Classifier.binaryRead(Parameters.pathToModelFile+".level1");
		tagger2 = new NETaggerLevel2();
		System.out.println("Reading model file : "+ Parameters.pathToModelFile+".level2");
		tagger2=(NETaggerLevel2)Classifier.binaryRead(Parameters.pathToModelFile+".level2");		
	}
	
	public String annotateText(String s) throws Exception{
		return NETagPlain.tagLine(s, tagger1, tagger2);
	}
		
	public static void main(String[] args) throws Exception {
		String configFile=args[0];
		int port=Integer.parseInt(args[1]);
		NerServer tagger=new NerServer(configFile);
        ServerSocket serverSocket = null;
        while(true){            
            try {
                serverSocket = new ServerSocket(port);
            } catch (IOException e) {
                System.err.println("Could not listen on port: "+port);
                System.exit(1);
            }

            Socket clientSocket = null;
            try {
                clientSocket = serverSocket.accept();
            } catch (IOException e) {
                System.err.println("Accept failed.");
                System.exit(1);
            }

            PrintWriter out = new PrintWriter(clientSocket.getOutputStream(), true);
            BufferedReader in = new BufferedReader(new InputStreamReader(clientSocket.getInputStream()));
            String inputLine=in.readLine();
            String text="";
            while (inputLine != null &&!inputLine.equals("*endinput*")) {
                 text+=inputLine+"\n";
                 inputLine=in.readLine();
            }
            out.println(tagger.annotateText(text));
            //out.println(text);
            out.close();
            in.close();
            clientSocket.close();
            serverSocket.close();
        }
    }
}
