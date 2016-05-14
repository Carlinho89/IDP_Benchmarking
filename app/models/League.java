package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.ArrayList;
import java.util.List;
/**
 * Created by carlodidomenico on 15/02/16.
 */
@Entity
public class League extends Model {
    @Id
    public int id;

    public String name;
    public int team_number;
    public String logo;

    /**
     * Query to DB to get a "League" by ID
     * @param id the id
     * @return the League fetched obj
     */
    public static League getById(int id){
        return (League) new Model.Finder(League.class).byId(id);
    }

    /**
     * Query to get all "League" tuples from the DB
     * @return list with all the fetched objs
     */
    public static List<League> getAll(){
        return (List<League>) new Model.Finder(League.class).all();
    }

    /**
     * Query to DB to get all "Leagues" names
     * @return the Leagues names fetched obj
     */
    public static String[] getLeaguesNames(){
        String names[] = null;

        List<League> leagues =(List<League>) new Model.Finder(League.class).all();
        List<String> leagueNames = new ArrayList<String>();

        for (League l:leagues) {
            leagueNames.add(l.name);
        }
        names = leagueNames.toArray(new String[leagueNames.size()]);


        return names;
    }


}
