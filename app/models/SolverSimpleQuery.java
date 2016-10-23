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
public class SolverSimpleQuery {
    private int season;
    private int numberOfTeams;
    private int teamID;
    private int numberOfSeasons;
    private int leagueID;
    private ArrayList<String> selectedInputsNames;
    private ArrayList<String> selectedOutputsNames;
    private List<Integer> selectedInputs;
    private List<Integer> selectedOutputs;
    private boolean superEff;
    private boolean inputOriented;
    private String solver;
    private String selectedMethod;
    private String leagueName;
    private String teamName;


    public SolverSimpleQuery(String query) {

        try {
            season = numberOfSeasons = teamID = numberOfSeasons = leagueID = -1;
            superEff = false;
            solver = selectedMethod = leagueName = teamName = null;
            selectedInputs = new ArrayList<>();
            selectedInputsNames = new ArrayList<>();
            selectedOutputs = new ArrayList<>();
            selectedOutputsNames = new ArrayList<>();


            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(query);
            if (rootNode.has("superEff"))
                superEff = rootNode.path("superEff").asBoolean(true);
            if (rootNode.has("inputOriented"))
                inputOriented = rootNode.path("inputOriented").asBoolean();
            if (rootNode.has("season"))
                season = rootNode.path("season").asInt();
            if (rootNode.has("numberOfTeams"))
                numberOfTeams = rootNode.path("numberOfTeams").asInt();
            if (rootNode.has("numberOfSeasons"))
                numberOfSeasons = rootNode.path("numberOfSeasons").asInt(1);
            if (rootNode.has("teamID"))
                teamID = rootNode.path("teamID").asInt();
            if (rootNode.has("leagueID"))
                leagueID = rootNode.path("leagueID").asInt();
            if (rootNode.has("solver"))
                solver = rootNode.path("solver").asText();
            if (rootNode.has("selectedMethod"))
                selectedMethod = rootNode.path("selectedMethod").asText();
            if (rootNode.has("leagueName"))
                leagueName = rootNode.path("leagueName").asText();
            if (rootNode.has("teamName"))
                teamName = rootNode.path("teamName").asText();
            if (rootNode.has("selectedInputs")){
                JsonNode selectedInputs = rootNode.path("selectedInputs");
                Iterator<JsonNode> iterator = selectedInputs.iterator();
                while (iterator.hasNext()){
                    JsonNode input = iterator.next();
                    this.selectedInputs.add(input.asInt());
                }
            }
            if (rootNode.has("selectedInputsNames")){
                JsonNode selectedInputsNames = rootNode.path("selectedInputsNames");
                Iterator<JsonNode> iterator = selectedInputsNames.iterator();
                while (iterator.hasNext()){
                    JsonNode input = iterator.next();
                    this.selectedInputsNames.add(input.asText());
                }
            }
            if (rootNode.has("selectedOutputs")){
                JsonNode selectedOutputs = rootNode.path("selectedOutputs");
                Iterator<JsonNode> iterator = selectedOutputs.iterator();
                while (iterator.hasNext()){
                    JsonNode input = iterator.next();
                    this.selectedOutputs.add(input.asInt());
                }
            }
            if (rootNode.has("selectedOutputsNames")){
                JsonNode selectedOutputsNames = rootNode.path("selectedOutputsNames");
                Iterator<JsonNode> iterator = selectedOutputsNames.iterator();
                while (iterator.hasNext()){
                    JsonNode input = iterator.next();
                    this.selectedOutputsNames.add(input.asText());
                }
            }
            //System.out.println("]");
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }



    }

    public boolean isSuperEff() {
        return superEff;
    }

    public void setSuperEff(boolean superEff) {
        this.superEff = superEff;
    }

    public int getLeagueID() {
        return leagueID;
    }

    public void setLeagueID(int leagueID) {
        this.leagueID = leagueID;
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

    public int getSeason() {
        return season;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public void setNumberOfTeams(int numberOfTeams) {
        this.numberOfTeams = numberOfTeams;
    }

    public Integer getNumberOfSeasons() {
        return numberOfSeasons;
    }

    public void setNumberOfSeasons(int numberOfSeasons) {
        this.numberOfSeasons = numberOfSeasons;
    }

    public ArrayList<String> getSelectedInputsNames() {
        return selectedInputsNames;
    }

    public void setSelectedInputsNames(ArrayList<String> selectedInputsNames) {
        this.selectedInputsNames = selectedInputsNames;
    }

    public ArrayList<String> getSelectedOutputsNames() {
        return selectedOutputsNames;
    }

    public void setSelectedOutputsNames(ArrayList<String> selectedOutputsNames) {
        this.selectedOutputsNames = selectedOutputsNames;
    }

    public boolean isInputOriented() {
        return inputOriented;
    }

    public void setInputOriented(boolean inputOriented) {
        this.inputOriented = inputOriented;
    }

    public String getSolver() {
        return solver;
    }

    public void setSolver(String solver) {
        this.solver = solver;
    }

    public String getSelectedMethod() {
        return selectedMethod;
    }

    public void setSelectedMethod(String selectedMethod) {
        this.selectedMethod = selectedMethod;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public void setLeagueName(String leagueName) {
        this.leagueName = leagueName;
    }

    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String teamName) {
        this.teamName = teamName;
    }
}
