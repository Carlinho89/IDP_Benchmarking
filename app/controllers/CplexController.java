package controllers;

import com.avaje.ebean.Model;
import models.Input;
import models.SeasonalData;
import models.Team;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 20/02/16.
 *
 * CplexController is the controller that interacts with cplex
 */
public class CplexController {

    public CplexController(){
        super();
    }

    /**
     * Method that creates the correct DMU array from the id of the selected league
     * @param league id of selected league
     * @param season season between 2010-2015
     * @return DMU array for selected season
     */
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
        for (int i = 0; i < choice.size(); i++){
            List<SeasonalData> result = SeasonalData.getBySeasonAndLeague(season, league, choice.get(i));
            stringPara[i] = result.toArray(new SeasonalData[result.size()]);
        }
        double[][] parameter = new double[stringPara.length][stringPara[0].length];   //Create final array for return
        for(int i = 0; i < stringPara.length; i++)
            for (int j = 0; j < stringPara[i].length; j++)
                parameter[i][j] = stringPara[i][j].value;


        return parameter;
    }

    /**
     * Method that transforms a list into a Comma Separated String
     * @param l list
     * @return Comma Separated String
     */
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
