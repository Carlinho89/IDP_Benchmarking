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
    public int id;

    public String name;
    public String type;
    public boolean output;
    public String value_type;

    public static Finder find = new Finder(Input.class);
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

    /**
     * Query DB for specific input types
     * @param type
     * @return
     */
    public static List<Input> getByType(String type){
        List<Input> inputs = (List<Input>) find.where().ilike("type", type).findList();
        if (inputs == null){
            System.out.println("input is null");
        }
        return inputs;
    }

    /**
     * Query DB for inputs which can be outputs
     * @param
     * @return
     */

    public static List<Input> getOutputs(){
            return find.where()
                    .eq("output", true)
                    .findList();



    }

    /**
     * Function that check the type of an input
     * @param type string: input type
     * @return True -> match
     *         False -> missmatch
     */
    public boolean isOfType(String type){
        if(this.type.equals(type))
        return true;
        else return false;
    }

}
