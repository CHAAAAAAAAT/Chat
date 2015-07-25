import java.io.*;
import java.net.*;
import java.util.*;


class ChatClientHandler extends Thread{
    
    private Socket socket;
    private BufferedReader in;
    private BufferedWriter out;
    private List clients;
    private String name;
    

    public ChatClientHandler(Socket sock,List clients){
	this.socket = sock;
	this.clients = clients;
	this.name = "undefined" + (clients.size()+1);
    }
    public String getClientName(){
	return name;
    }

    
  
    public void run(){
	try{
	    open();//ソケットを開く		
	    System.out.println((String)getClientName() + " connected");
	    while(true){
		out.write(">  ");
		out.flush();
		String message = receive();
		String[] commands = message.split(" ");
		if(commands[0].equalsIgnoreCase("post")){
		    post(commands[1]);
		}
		else if(commands[0].equalsIgnoreCase("bye")){
		    bye();
		    break;
		}
		else if(commands[0].equalsIgnoreCase("help")){
		    help();
		}
		else if(commands[0].equalsIgnoreCase("whoami")){
		    whoami()
		}
		else if(commands[0].equalsIgnoreCase("name")){
		    newname(commands[1]);
		}
		else if(commands[0].equalsIgnoreCase("users")){
		    users();
		}
		else if(commands[0].equalsIgnoreCase("tell")){
		    tell(commands[1],commands[2]);
		}	
	    }
	}catch(IOException e){
	    e.printStackTrace();
	}finally{
	    close();
	}
    }
    

    public void open() throws IOException{
        in = new BufferedReader(
            new InputStreamReader(socket.getInputStream())
        );
        out = new BufferedWriter(
            new OutputStreamWriter(socket.getOutputStream())
        );
    }    
 
    public String receive() throws IOException{
        String line = in.readLine();
        System.out.println(line);
        return line;
    }
    
    public void send(String message) throws IOException{
        out.write(message);
        out.write("\r\n");
        out.flush();
    }
    
    public void close(){
        if(in != null){
            try{
                in.close();
            } catch(IOException e){ }
        }
        if(out != null){
            try{
                out.close();
            } catch(IOException e){ }
        }
        if(socket != null){
            try{
                socket.close();
            } catch(IOException e){ }
        }
    }
    
    public void help() throws IOException{
	this.send(": help  name whoami  bye  post users tell");
	System.out.println(": help  name whoami  bye  post users tell" );
    }

}
