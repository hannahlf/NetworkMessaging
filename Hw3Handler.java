/**
 * Handler class containing the logic for echoing results back
 * to the client. 
 *
 * @author Greg Gagne 
 */

import java.io.*;
import java.net.*;

import jdk.nashorn.api.tree.ClassExpressionTree;

// import jdk.internal.util.xml.impl.Input;

// import org.graalvm.compiler.nodes.extended.NullCheckNode;

public class Hw3Handler 
{
	public static final int BUFFER_SIZE = 256;
	public static final int CHUNK = 1024;
	

	
	/**
	 * this method is invoked by a separate thread
	 */
	public void process(Socket client) throws java.io.IOException {
		byte[] buffer = new byte[BUFFER_SIZE];
		int cntr = 0;
		int first = 0;
		int second = 0;
		Socket origin = null;
		String resource = null;
		String host = null;
		DataOutputStream toOrigin = null;
		int none = 0;


		
		try {
			/**
			 * get the input and output streams associated with the socket.
			 */ 
			
		
			BufferedReader in = new BufferedReader(new InputStreamReader(client.getInputStream()));
			PrintWriter out = new PrintWriter(new OutputStreamWriter(client.getOutputStream()));
			

			String line;
			// http get request that client sends
			line = in.readLine();
			System.out.println("server: " + line);

			
			// Begin looking at position 4 look for the second space
			// Everything necessary is between the 2 spaces
			// if there is a second slash between the first slash and the second space
			// the origin server is then between the fisrt and second slash 
			// and the resource is the remaining slash until the second space
			// if there is not a second slash in bewtween the resource is just a slash
			for(int i = 4; i< line.length(); i++){
				if(line.charAt(i) == ' '){
					int end = i;
					for(int j = 4; j<= end; j++){
						if(line.charAt(j) == '/'){
							cntr++;
							if(cntr == 2){
								host = line.substring(5, j);
								resource = line.substring(j, end);
								break;
							}else{
								host = line.substring(5, end);
								resource = "/";
							}
						}

					}
				}
			}
			
			// Just extra print outs for testing
			// System.out.println("Host: " +host);
			// System.out.println("Resource: " +resource);
			// System.out.println("GET " + resource + "\r\n");
			// System.out.println("Host: " + host + "\r\n");
			// System.out.println("Connection: close \r\n\r\n");

			origin = new Socket(host, 80);
			// // dataoutputstream from origin
			toOrigin = new DataOutputStream(origin.getOutputStream());
			
			// write from server to origin 
			toOrigin.writeBytes( "GET " + resource + " HTTP/1.1\r\nHost: " + host + "\r\nConnection: close \r\n\r\n");

			// input/output streams to read and write from server to client
			BufferedInputStream fromOrigin = new BufferedInputStream(origin.getInputStream());
			BufferedOutputStream toClient = new BufferedOutputStream(client.getOutputStream());
		
			byte[] bytesTwo = new byte[CHUNK];
			
			// writes from server to client
			while((none = fromOrigin.read(bytesTwo)) > 0){
				toClient.write(bytesTwo,0, none);
				toClient.flush();
				
				
			}

		   
		}
		catch (IOException ioe) {
			System.err.println(ioe);
		}
		finally {
			// close streams and socket
			client.close();
			origin.close();
		}
	}
}
