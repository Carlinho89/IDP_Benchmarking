package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by carlodidomenico on 15/02/16.
 */
@Entity
public class Fixed_Data extends Model{
    @Id
    public int id;

    public int team_id;
    public String team_name;
    public int league_id;
    public int input_id;
    public float value;

    /**
     * Query to DB to get a "Fixed_Data" by ID
     * @param id the id
     * @return the Fixed Data fetched obj
     */
    public static Fixed_Data getById(int id){
        return (Fixed_Data) new Model.Finder(Fixed_Data.class).byId(id);
    }

    /**
     * Query to get all "Fixed_Data" tuples from the DB
     * @return list with all the fetched objs
     */
    public static List<Fixed_Data> getAll(){
        return (List<Fixed_Data>) new Model.Finder(Fixed_Data.class).all();
    }



}
