package models;

import com.avaje.ebean.*;

import javax.persistence.Entity;
import javax.persistence.Id;
import java.util.List;

/**
 * Created by carlodidomenico on 10/02/16.
 */
@Entity
public class SeasonalData extends Model {
    @Id
    public int id;

    public int team_id;
    public String team_name;
    public int year;
    public int league_id;
    public int input_id;
    public float value;

    /**
     * Query to DB to get a "SeasonalData" by ID
     * @param id the id
     * @return the SeasonalData fetched obj
     */
    public static SeasonalData getById(int id){
        return (SeasonalData) new Model.Finder(SeasonalData.class).byId(id);
    }

    /**
     * Query to get all "SeasonalData" tuples from the DB
     * @return list with all the fetched objs
     */
    public static List<SeasonalData> getAll(){
        return (List<SeasonalData>) new Model.Finder(SeasonalData.class).all();
    }

    public static List<SeasonalData> getBySeasonAndLeague(int season, int league_id, int input_id){
        RawSql rawSql = RawSqlBuilder.parse("select id, team_id, team_name, year, league_id, input_id, value  " +
                "from seasonal_data " +
                "where year =" + season + "AND input_id = " + input_id + "AND league_id = " + league_id)
                .columnMapping("id","id")
                .columnMapping("team_id","team_id")
                .columnMapping("team_name","team_name")
                .columnMapping("year","year")
                .columnMapping("league_id","league_id")
                .columnMapping("input_id","input_id")
                .columnMapping("value","value")
                .create();
        Query<SeasonalData> query = Ebean.find(SeasonalData.class);
        query.setRawSql(rawSql);

        List<SeasonalData> result = query.findList();

        return result;
    }

}
