package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by carlodidomenico on 15/02/16.
 */
@Entity
public class League extends Model {
    @Id
    public String id;

    public String name;
    public String teamNumber;
    public String logo;

}
