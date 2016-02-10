package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by carlodidomenico on 10/02/16.
 */
@Entity
public class SeasonalData extends Model {
    @Id
    public String id;

    public String team_id;
    public String team_name;
    public String year;
    public String league_id;
    public String input_id;
    public String value;


}
