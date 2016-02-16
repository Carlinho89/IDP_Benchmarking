package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by carlodidomenico on 09/02/16.
 */
@Entity
public class Input extends Model {
    @Id
    public String id;

    public String name;
    public String type;

    /**
     * Query to DB to get a "Input" by ID
     * @param id the id
     * @return the Input fetched obj
     */
    public static Input getById(int id){
        return (Input) new Model.Finder(Input.class).byId(id);
    }

    /**
     * Query to get all "Input" tuples from the DB
     * @return list with all the fetched objs
     */
    public static List<Input> getAll(){
        return (List<Input>) new Model.Finder(Input.class).all();
    }

}
