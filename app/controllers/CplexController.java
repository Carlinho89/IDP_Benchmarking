package controllers;

import com.avaje.ebean.Model;
import ilog.concert.IloNumVar;
import ilog.cplex.IloCplex;
import models.Input;
import models.SeasonalData;
import models.Team;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

/**
 * Created by carlodidomenico on 20/02/16.
 */
public class CplexController {
    private IloCplex cplex;
    private HashMap<String, IloNumVar> numVars;

    public CplexController(){
        super();
        cplex = null;
        numVars = new HashMap<String, IloNumVar>();
    }

    public String[] createDMUArray(int league, int season) {
        List<Team> teamsForSeason = Team.getAllbySeason(season, league);
        String[] teamsNamesForSeason = new String[teamsForSeason.size()];
        for (int i = 0; i < teamsForSeason.size(); i++) {
            teamsNamesForSeason[i] = teamsForSeason.get(i).name;
        }
        return teamsNamesForSeason;
    }
    /*
    * The method is given a String which separates the selected tables by a comma.
    * This string is then turned into an array from which the parameters are gained
    * */
    public double[][] createParameterArray(int league, int season, List<Integer> choice)    {
        SeasonalData stringPara[][] = new SeasonalData[choice.size()][];
        System.out.println("Fetching DB, choices number: " + choice.size());
        for (int i = 0; i < choice.size(); i++){
            List<SeasonalData> result = SeasonalData.getBySeasonAndLeague(season, league, choice.get(i));
            stringPara[i] = result.toArray(new SeasonalData[result.size()]);
            //System.out.println("Done choice n" + i + " has n: " + result.size() + " elements");
        }

        System.out.println("stringPara.length: " + stringPara.length);
        System.out.println("stringPara[0].length: " + stringPara[0].length);

        /*for (int i = 0; i < stringPara.length; i++) {
            System.out.println("i: "+i);
            for (int j = 0; j < stringPara[i].length; j++) {
                System.out.println("j: "+j);
                System.out.println("Team " + stringPara[i][j].team_name + " val: " + stringPara[i][j].value);

            }

          }*/


        double[][] parameter = new double[stringPara.length][stringPara[0].length];   //Create final array for return
        for(int i = 0; i < stringPara.length; i++)
            for (int j = 0; j < stringPara[i].length; j++)
                parameter[i][j] = stringPara[i][j].value;


        return parameter;
    }

    static public String listToCSString(List<Integer> l){
        String result = "";
        List<Input> inputs = new ArrayList<Input>();
        //System.out.println("ListToCCSting size is: " + l.size());
        for (Integer id: l) {
            //System.out.println("Id is: " + id);
            Input input = (Input) new Model.Finder(Input.class).byId(id);
            //System.out.println("Name is: " + input.name);
            inputs.add(input);
        }

        for (int i=0; i < inputs.size(); i++){
            if (i != inputs.size()-1)
                result += inputs.get(i).name + ", ";
            else
                result += inputs.get(i).name;
        }

        //System.out.println("result string is '" + result + "'");
        return result;
    }


}
