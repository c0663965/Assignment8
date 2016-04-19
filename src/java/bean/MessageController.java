package bean;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import javax.faces.bean.ApplicationScoped;
import javax.ws.rs.core.Response;

/**
 *
 * Kihoon, Lee 
 */
@ApplicationScoped
public class MessageController {
    
    Gson gson = new GsonBuilder().setPrettyPrinting().create();
    public static List<Message> list;   //Initial data to run or verify this code

    public MessageController(){
        list= new ArrayList(Arrays.asList(new Message("1","Java2","Term Project","Kihoon","2015-03-21 12:02:25"),
                                new Message("2","JQuery","Preparing Presentation","Lee","2012-04-21 13:02:25"),
                                new Message("3","English","Reading Enlish novels","Kim","2016-01-18 22:12:07"),
                                new Message("4","Oracle","Database PL/SQL","Choi","2015-08-31 03:15:07")));
    } 
    
    public List<Message> getList() {
        return list;
    }
   
    public String getAll() {    //GET
        return gson.toJson(getList());
    }
    
    public String getMessageById(String id){    //GET
        
        String str="";
        
        for (Message m : list) {
            if (m.getId().equals(id)) {
                str=m.toString();
                break;
            }
        }
        return str;
    }
    
    public String addMessage(String json){   //POST
        
        JsonParser parser = new JsonParser();
        JsonObject messageJson = (JsonObject) parser.parse(json);

        String title = messageJson.get("title").getAsString();
        String contents = messageJson.get("contents").getAsString();
        String author = messageJson.get("author").getAsString();

        String id=String.valueOf(Integer.parseInt(list.get(list.size()-1).getId())+1); //The id of the newly inserted message. 
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
        list.add(new Message(id, title, contents, author, sdf.format(new Date())));
        
        return getMessageById(id);
    }
    
    public String getMessagebyDate(String startDate, String endDate) throws ParseException{
        
        SimpleDateFormat format1 = new SimpleDateFormat("yyyy-MM-dd", Locale.CANADA);
        SimpleDateFormat format2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CANADA);
        
        Date start = format1.parse(startDate); //Converting string to date
        Date end = format1.parse(endDate); //Converting string to date
        List<Message> messages=new ArrayList<>();

        Date senttime;
        
        for (Message m : list) {
            senttime = format2.parse(m.getSenttime());
            if (start.before(senttime) && senttime.before(end)) {
                messages.add(m);
            }
        }
        return  gson.toJson(messages);
    }
    
    public Response deleteMessageById(String id){    //DELET
        Iterator<Message> iter= list.iterator();
        
        while (iter.hasNext()){
            Message m = iter.next();
            if(m.getId().equals(id)){
                iter.remove();
                break;
            }
        }
        return Response.ok("OK!").build();
    }
    
    public Response editMessage(String id, String json){   //PUT
        Response res;
        String oldData = getMessageById(id);
        oldData=oldData.substring(1,oldData.length()-1);
        String newData = json;
        
        JsonParser parser = new JsonParser();
        JsonObject oldMessage = (JsonObject) parser.parse(oldData);
        JsonObject newMessage = (JsonObject) parser.parse(newData);

        String[] str ={"title","contents","author"};
        String oldContent, newContent;
        boolean isNew=false;
      
        List<String> updt = new ArrayList<>();
        
        for (String title : str) {
            oldContent = oldMessage.get(title).getAsString();
            newContent = newMessage.get(title).getAsString();
            if (!oldContent.equals(newContent)) {
                updt.add(newContent);
                isNew=true;
            }else
                updt.add(oldContent);
        }

        if(isNew){
            SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
            for(int i=0;i<list.size();i++)
                if(list.get(i).getId().equals(id)){
                    list.set(i,new Message(id,updt.get(0),updt.get(1),updt.get(2),sdf.format(new Date())));
                    String Index = String.valueOf(i);
                    break;
                }
            res=Response.ok(list.get(Index)).build();
        }else
            res=null;
        return res;
    }
    
    @Override
    public String toString() {
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(list);
    }
    
    public static boolean isValidId(String id){
        boolean isIdValid = false;

        for (Message m : list) {
            if (id.equals(m.getId())) {
                isIdValid=true;
                break;
            }
        }
        return isIdValid;
    }
}