package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by carlodidomenico on 10/02/16.
 */
@Entity
public class Team extends Model {

    @Id
    public String id;

    public String name;
    public String transfermarktName;
    public String logo;



}
