package account.models.entities;

import account.models.entities.enums.Action;
import jakarta.persistence.*;

import java.util.Date;

@Entity
@Table(name = "events")
public class EventEntity {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "event_id_seq")
    @SequenceGenerator(name = "event_id_seq", sequenceName = "EVENT_ID_SEQ", allocationSize = 100)
    private long id;


    private Date date;
    private Action action;
    private String subject;
    private String object;
    private String path;

    public EventEntity() {
    }

    public EventEntity(Date date, Action action, String subject, String object, String path) {
        this.date = date;
        this.action = action;
        this.subject = subject;
        this.object = object;
        this.path = path;
    }

    public long getId() {
        return id;
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
