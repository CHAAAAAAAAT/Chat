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
	    open();		
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

    public void name(String name)throws IOException{
	List names = new ArrayList();
	for(int i = 0;i < clients.size();i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    names.add(handler.getClientName());
	}

	if(names.contains(name)){
	    this.send("その名前は他の人に使われています.");
	}else{
	    this.name = name;
	    this.send("名前を\"" + name + "\"に変更しました.");
	}
	System.out.println(": " + name);
    }

    public void whoami() throws IOException{
	this.send(getClientName());
    }
    
    public void users() throws IOException{
        List names = new ArrayList();
        for(int i = 0; i < clients.size(); i++){
            ChatClientHandler handler = (ChatClientHandler)clients.get(i);
            names.add(handler.getClientName());
        }
        Collections.sort(names);
        String returnMessage = "";
        for(int i = 0; i < names.size(); i++) {
            returnMessage = returnMessage + names.get(i) + ",";
        }
        this.send(returnMessage);
    }

     public void bye() throws IOException{
	 String object= (String)getClientName();
	 this.send("bye " + (String)getClientName());
	 System.out.println(": bye " + (String)getClientName());
	 for(int i=0; i<clients.size(); i++){
	     ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	     if(object.equals(handler.getClientName())){
		 clients.remove(i)
	     }
	 }	
     }

    public void post(String message)throws IOException{
	List names = new ArrayList();
	for(int i = 0;i < clients.size();i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    if(handler != this){
		names.add(handler.getClientName());
		handler.send("[" + this.getClientName() + "] "+ message);
	    }
	}
	Collections.sort(names);
	String returnMessage = "";
	for(int i = 0;i < names.size();i++){
	    returnMessage = returnMessage + names.get(i) + ",";
	}
	if(returnMessage != ""){
	    this.send(returnMessage);
	    System.out.println(": "+ message);
	}else{
	    this.send("no one receive message");
	    System.out.println(": no one receive message");
	}
    }

    public void tell(String name, String message) throws IOException{
	List<String> names = new ArrayList<String>(); 
	int receive_flag = 0;
	for(int i = 0; i < clients.size(); i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    if((handler.getClientName().equals(name)) && !(handler.rejectList.contains(this))){
		names.add(handler.getClientName());
		handler.send("[" + this.getClientName() + "->" + handler.getClientName() + "]" + message);
		receive_flag = 1;
	    }else{
		if((i == clients.size() - 1) && receive_flag == 0){
		    this.send("no one receive message");
		}
	    }
	}
	Collections.sort(names);
	String returnUsers = "";
	int flag = 0;
	for(int i = 0; i < names.size(); i++){
	    if(flag == 0){
		returnUsers = returnUsers + names.get(i);
		flag = 1;
	    }else{
		returnUsers =  returnUsers + "," +names.get(i);
	    }
	}
	this.send(returnUsers);
    }

    public void reject(String name) throws IOException{
	for(int i = 0; i < clients.size(); i++){
	    ChatClientHandler handler = (ChatClientHandler)clients.get(i);
	    if(handler.getClientName().equals(name)){
		if(rejectList.contains(handler)){
		    rejectList.remove(handler);
		}else{
		    rejectList.add(handler);
		}
		return;
	    }
	}
	this.send("ユーザが存在しません");
	return;
    }

    public void reject() throws IOException{
	String returnUsers = "";
	int flag = 0;
	for(int i = 0; i < rejectList.size(); i++){
	    if(flag == 0){
		returnUsers = returnUsers + rejectList.get(i).getClientName();
	    }else{
		returnUsers = returnUsers + "," +rejectList.get(i).getClientName();
	    }
	}
	this.send(returnUsers);
    }
}
