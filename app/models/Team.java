package models;

import com.avaje.ebean.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by carlodidomenico on 10/02/16.
 */
@Entity
public class Team extends Model {

    @Id
    public int id;

    public String name;
    public int tm_id;
    public String logo;
    public int league_id;
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


    /**
     * Query to get all "Team" tuples for a certain year and league
     * @return list with all the fetched objs
     * "select * from team where id in (select distinct team_id from seasonal_data where year = 2010 and league_id=1 )"
     */
    public static List<Team> getAllbySeason(int year, int league_id){
        RawSql rawSql = RawSqlBuilder.parse("select id, name, tm_id, logo, league_id from team where id in (select distinct team_id from seasonal_data where year = "+year+" and league_id="+league_id+" )")
                .columnMapping("id", "id")
                .columnMapping("name", "name")
                .columnMapping("tm_id", "tm_id")
                .columnMapping("logo", "logo")
                .columnMapping("league_id", "league_id")
                .create();

        Query<Team> query = Ebean.find(Team.class);
        query.setRawSql(rawSql);
        List<Team> result = query.findList();
        return result;
    }
}
