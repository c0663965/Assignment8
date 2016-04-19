package bean;


import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

/**
 *
 * @author Kihoon, Lee
 */

public class Message {

    private String id;
    private String title;
    private String contents;
    private String author;
    private String senttime;
    
    public Message(){
    }

    public Message(String id, String title, String contents, String autor, String senttime) {
        this.id = id;
        this.title = title;
        this.contents = contents;
        this.author = autor;
        this.senttime=senttime;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getContents() {
        return contents;
    }

    public void setContents(String contents) {
        this.contents = contents;
    }

    public String getAuthor() {
        return author;
    }

    public void setAuthor(String autor) {
        this.author = autor;
    }
    
    public String getSenttime() {
        return senttime;
    }

    public void setSenttime(String senttime) {
        this.senttime = senttime;
    }
    
    @Override
    public String toString(){
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return "["+gson.toJson(this)+"]";
    }
}