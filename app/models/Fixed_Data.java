package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by carlodidomenico on 15/02/16.
 */
@Entity
public class Fixed_Data extends Model{
    @Id
    public String id;

    public String team_id;
    public String team_name;
    public String league_id;
    public String input_id;
    public String value;
}
