package bean;

import static bean.MessageController.isValidId;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import static com.sun.xml.ws.spi.db.BindingContextFactory.LOGGER;

import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Map.Entry;
import javax.websocket.OnError;
import javax.websocket.OnMessage;
import javax.websocket.OnOpen;
import javax.websocket.Session;
import javax.websocket.server.ServerEndpoint;

/**
 *
 *  Kihoon, Lee 
 */
@ServerEndpoint("/messages")
public class messageServer {

    private final MessageController mc = new MessageController();
    private final JsonParser parser = new JsonParser();

    private String response="";
    private String info="";

    @OnMessage
    public void onMessage(String message, Session session) throws IOException, ParseException {

//        "{ "getAll" : true }" --> should respond with a JSON Array of the entire List of Messages
//        "{ "getById" : id }" --> should respond with a single JSON Object as specified by the ID
//        "{ "getFromTo" : [ "startDate", "endDate" ] }" --> should respond with a JSON Array of the Messages between startDate and endDate inclusive
//        "{ "post" : { ... some Message as below without ID ... } }" --> should add the Message to the system and echo back the Message to all connected systems
//        "{ "put" : { ... some Message as below with ID ... } }" --> should edit the Message in the system that matches the given ID and echo back { "ok" : true }
//        "{ "delete" : id }" --> should delete the Message in the system that matches the given ID and echo back { "ok" : true }
//        In the case of any errors, a JSON object should be returned of the form { "error" : "Some meaningful error message." }
        
        JsonObject receivedJSON = (JsonObject) parser.parse(message);
        JsonObject JASON = new JsonObject();
        String key = findFirstKeyName(message);

        switch (key) {
            case "reset":
                {
                    info="Info: The messages have been successfully reset to the intial state.";
                    MessageController mc = new MessageController();
                    response= mc.getAll();
                    break;
                }
            case "getAll":
                info="Info: All the messages have been successfully fetched.";
                response=mc.getAll();
                break;
            case "getById":
                {
                    String id = receivedJSON.get("getById").getAsString();
                    if(isValidId(id)){
                        info="Info: The message at the id=" + id + " has been successfully fetched.";
                        response=mc.getMessageById(id);
                    }else{
                        throw new IllegalArgumentException("Error : Invalid Id");
                    }
                    break;
                }
            case "getFromTo":
                JsonArray date = receivedJSON.get("getFromTo").getAsJsonArray();
                String startDate = date.get(0).getAsString();
                String endDate = date.get(1).getAsString();
                info="The following messages beween " + startDate + " and " + endDate + " have been successfully fetched.";
                response=mc.getMessagebyDate(startDate, endDate);
                break;
            case "delete":
            {
                String id = receivedJSON.get("delete").getAsString();
                if(isValidId(id)){
                    mc.deleteMessageById(id);
                    System.out.println(mc.deleteMessageById(id));
                    info="The message at the id=" + id + " has been successfully deleted.";
                    response=mc.getAll(); //To check if the id entry is deleted or not 
                }else{
                    throw new IllegalArgumentException("Error : Invalid Id");
                }
                break;
            }
            case "post":
                String post = receivedJSON.get("post").getAsJsonObject().toString();
                info="The message you input has been successfully stored to the list as follows.";
                String lastId = mc.getList().get(mc.getList().size()-1).getId();  // The last element's id 
                String nextId = String.valueOf(Integer.parseInt(lastId)+1);
                StringBuilder sb = new StringBuilder(post);
                sb.insert(1,"\"id\":"+nextId+",");  //To insert a new id field into the existig JSON of post whith doesn't have an id field.  
                response = mc.addMessage(post);
                break;
            case "put":  //update with id
                {
                    JsonObject temp= receivedJSON.get("put").getAsJsonObject();
                    JsonObject newMessage = (JsonObject) parser.parse(temp.toString());
                    String id = newMessage.get("id").getAsString();
                    
                    if(isValidId(id)){
                        info="The message at the id="+id+" has been successufully updated.";
                        response=mc.editMessage(id, temp.toString()).toString();
                    }else{
                        throw new IllegalArgumentException("Error : Invalid Id");
                    }      
                    break;
                }
        }//The end of swtich-case
            
        JASON.addProperty("info",info);
        JASON.addProperty("list",response);
        session.getBasicRemote().sendText(JASON.toString());
    }

    @OnError
    public void onError(Throwable exception, Session session) throws IOException {
        JsonObject JASON = new JsonObject();
        JASON.addProperty("error",exception.getMessage());
        session.getBasicRemote().sendText(JASON.toString());
    }
    
    @OnOpen
    public void onOpen(Session session) throws IOException {
    }

    public String findFirstKeyName(String str) {
        JsonObject obj = (JsonObject) parser.parse(str);
        List<String> keys = new ArrayList<>();
        for (Entry<String, JsonElement> e : obj.entrySet()) {
            keys.add(e.getKey());
        }
        return keys.get(0);
    }
}
