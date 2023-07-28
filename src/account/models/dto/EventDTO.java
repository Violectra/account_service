package account.models.dto;

import account.models.entities.enums.Action;

import java.util.Date;

public class EventDTO {
    private Date date;
    private Action action;
    private String subject;
    private String object;
    private String path;

    public EventDTO(Date date, Action action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public Date getDate() {
        return date;
    }

    public Action getAction() {
        return action;
    }

    public String getSubject() {
        return subject;
    }

    public String getObject() {
        return object;
    }

    public String getPath() {
        return path;
    }
}
