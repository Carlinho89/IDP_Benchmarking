package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

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

    /**
     * Query to DB to get a "Team" by ID
     * @param id the id
     * @return the Team fetched obj
     */
    public static Team getById(int id){
        return (Team) new Model.Finder(Team.class).byId(id);
    }

    /**
     * Query to get all "Team" tuples from the DB
     * @return list with all the fetched objs
     */
    public static List<Team> getAll(){
        return (List<Team>) new Model.Finder(Team.class).all();
    }


}
