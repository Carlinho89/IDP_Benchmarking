package case_studies;

import controllers.CplexController;
import models.League;
import org.apache.commons.math3.stat.correlation.SpearmansCorrelation;
import workpackage.Scenario;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carlodidomenico on 11/06/2016.
 */
public class CaseOne {
    private List<Integer> league;
    private List<Integer> input;
    private List<Integer> output;
    private List<Integer> correlation;
    private List<Integer> correlationTwo;
    private List<double[][]> overList;
    private Scenario caseOne;
    private boolean orientation = false;
    private boolean superEff = true;
    private int start = 2009;
    private int seasons = 4;
    private CplexController connection;


    public CaseOne(){
        league = new ArrayList<Integer>();
        input = new ArrayList<Integer>();
        output = new ArrayList<Integer>();
        correlation = new ArrayList<Integer>();
        correlationTwo = new ArrayList<Integer>();
        connection = new CplexController();
        orientation = false;
        boolean superEff = true;
        int start = 2010;
        int seasons = 4;
    }

    public void solve() {
        /*
         * The goal of this case study is to determine whether the spectators reward good defensive football with their attendance.
         * I.e. the question is if and how a club's offensive efficiency correlates with the amount of spectators.
         * For the defensive efficiency, we are going to use intercepts and 1/shots conceived as inputs.
         * The only output used is 1/goals conceived.
         * We are going to measure the output-oriented efficiency, because defensive play appears useless when conceiving numerous goals.
         * Also, we assume that the crowd and the fans do not want to go to the stadium to see a total defensive failure.
         * So any output-shortcomings need to be punished.
         * The correlation will be calculated according to Spearman's Correlation Coefficient. The base efficiency will be BCC.
         */
        //Define attributes
        //String[]_league = {"bundesliga", "premier_league", "primera_division"};

        league.add(4); //bundes
        league.add(2); //premier
        league.add(3); //liga

        //String _input = "intercept, oneByShCon";
        input.add(13); //intercept
        input.add(11); //shots per game conceded

        //String _output = "oneByGoCon";
        output.add(4); //goals scored

        //String _correlation = "spec";
        correlation.add(25); // StediumCapacity

        //String _correlationTwo = "oneByGoCon";
        correlationTwo.add(4); ////goals scored

        //Run the model and gather results
        for(int i = 0; i < league.size(); i++)
            try{
                caseOne = new Scenario(league.get(i), input, output, orientation, superEff, start, seasons);
                overList.addAll(caseOne.getOverviewList());
            }catch(Exception ex){
                System.out.println("Failed to compute the case for league " + League.getById(league.get(i)));
            }

        //Gather spectator data in a list
        CplexController connection = new CplexController();
        List<double[][]>spectator = new ArrayList<>();
        List<double[][]>goals = new ArrayList<>();
        for(int i = 0; i < league.size(); i++)
            for(int j = 0; j < seasons; j++)
            {
                spectator.add(connection.createParameterArray(league.get(i), start + j, correlation));  //Creates only two-dimensional arrays due to the parameter format
                goals.add(connection.createParameterArray(league.get(i), start + j, correlationTwo));
            }
        
        //Compute correlation
        double[] spearman = new double[league.size() * seasons];    //FORMEL: Statistikskript, S.72

        double[][]interOver;
        double[][]interSpec;
        System.out.println("Correlation between defensive efficiency and spectators:");
        for(int i = 0; i < spearman.length; i++)
        {
            interOver = overList.get(i);
            interSpec = spectator.get(i);
            spearman[i] = new SpearmansCorrelation().correlation(interOver[1], interSpec[0]);
            System.out.println(spearman[i]);
        }
        double[][]interGoal;
        System.out.println("Correlation between 1/goals shot and spectators:");
        for(int i = 0; i < spearman.length; i++)
        {
            interSpec = spectator.get(i);
            interGoal = goals.get(i);
            spearman[i] = new SpearmansCorrelation().correlation(interGoal[0], interSpec[0]);
            System.out.println(spearman[i]);
        }
    }
}
