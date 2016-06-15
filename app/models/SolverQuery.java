package models;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Created by enrico on 16/05/2016.
 */
public class SolverQuery {
    private int leagueID;
    private int season;
    private int teamID;
    private List<Integer> selectedInputs;
    private List<Integer> selectedOutputs;


    public SolverQuery(String query) {

        try {

            selectedInputs = new ArrayList<Integer>();
            selectedOutputs = new ArrayList<Integer>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(query);

            this.leagueID =rootNode.path("leagueID").asInt();
            //System.out.println("leagueID: " + this.leagueID);
            this.season = rootNode.path("season").asInt();
            //System.out.println("season: " + this.season);
            this.teamID = rootNode.path("teamID").asInt();
            //System.out.println("teamID: " + this.teamID);


            JsonNode selectedInputsNode = rootNode.path("selectedInputs");
            Iterator<JsonNode> iterator = selectedInputsNode.iterator();
            //System.out.print("selectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                //System.out.print(input.asInt() + " ");
                selectedInputs.add(input.asInt());
            }

            //System.out.println("]");

            JsonNode selectedOutputsNode = rootNode.path("selectedOutputs");
             iterator = selectedOutputsNode.iterator();
            //System.out.print("selectedOutputs: [ ");

            while (iterator.hasNext()) {
                JsonNode output = iterator.next();
                //System.out.print(output.asInt() + " ");
                selectedOutputs.add(output.asInt());
            }

            //System.out.println("]");
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }



    }


    public int getLeagueID() {
        return leagueID;
    }

    public void setLeagueID(int leagueID) {
        this.leagueID = leagueID;
    }

    public int getSeason() {
        return season;
    }

    public void setSeason(int season) {
        this.season = season;
    }

    public int getTeamID() {
        return teamID;
    }

    public void setTeamID(int teamID) {
        this.teamID = teamID;
    }

    public List<Integer> getSelectedInputs() {
        return selectedInputs;
    }

    public void setSelectedInputs(List<Integer> selectedInputs) {
        this.selectedInputs = selectedInputs;
    }

    public List<Integer> getSelectedOutputs() {
        return selectedOutputs;
    }

    public void setSelectedOutputs(List<Integer> selectedOutputs) {
        this.selectedOutputs = selectedOutputs;
    }
}
