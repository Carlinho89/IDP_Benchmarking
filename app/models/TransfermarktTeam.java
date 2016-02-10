package models;

import com.avaje.ebean.Model;

import javax.persistence.Entity;
import javax.persistence.Id;

/**
 * Created by carlodidomenico on 10/02/16.
 */
@Entity
public class TransfermarktTeam extends Model{
    @Id
    public String id;

    public String name;
    public String position;
    public String year;
    public String league_id;

}
