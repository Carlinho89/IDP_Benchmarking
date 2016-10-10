package models;

import com.fasterxml.jackson.core.JsonParseException;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;

/**
 * Created by carlodidomenico on 08/07/2016.
 */
public class SolverComplexQuery {

    private boolean superEff;
    private String solver;
    private String selectedMethod;
    private String leagueName;
    private String teamName;
    private ArrayList<Integer> selectedInputs;
    private ArrayList<String> selectedInputsNames;
    private ArrayList<Integer> selectedOutputs;
    private ArrayList<String> selectedOutputsNames;
    private ArrayList<DEAWrapper> stage1DEA;
    private ArrayList<DEAWrapper> stage2DEA;
    private ArrayList<DEAWrapper> stage3DEA;
    private int season;
    private int numberOfTeams;
    private int teamID;
    private int numberOfSeasons;
    private int leagueID;


    public SolverComplexQuery(String query){
        try {
            season = numberOfSeasons = teamID = numberOfSeasons = leagueID = -1;
            superEff = false;
            solver = selectedMethod = leagueName = teamName = null;
            selectedInputs = new ArrayList<>();
            selectedInputsNames = new ArrayList<>();
            selectedOutputs = new ArrayList<>();
            selectedOutputsNames = new ArrayList<>();
            stage1DEA = new ArrayList<>();
            stage2DEA = new ArrayList<>();
            stage3DEA = new ArrayList<>();

            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(query);
            if (rootNode.has("superEff"))
                superEff = rootNode.path("superEff").asBoolean();
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
            if (rootNode.has("stage1DEA")){
                JsonNode stage1DEA = rootNode.path("stage1DEA");
                Iterator<JsonNode> iterator = stage1DEA.iterator();
                DEAWrapper wrapper = null;

                System.out.println("stage1DEA: " + stage1DEA);


                while (iterator.hasNext()){
                    ArrayList<Integer> selectedInputHolder = new ArrayList<>();
                    ArrayList<Integer> selectedOutputsHolder = new ArrayList<>();
                    ArrayList<Integer> previousResultsHolder = new ArrayList<>();
                    int stageHolder = -1;
                    boolean inputOrientedHolder = false;
                    int deaIDHolder = -1;

                    JsonNode deaNode = iterator.next();
                    System.out.print("DeaNode: "+ deaNode);
                    if (deaNode.has("stage"))
                        stageHolder = deaNode.path("stage").asInt();
                    if (deaNode.has("inputOriented"))
                        inputOrientedHolder = deaNode.path("inputOriented").asBoolean();

                    if (deaNode.has("deaID"))
                        deaIDHolder = deaNode.path("deaID").asInt();

                    if (deaNode.has("selectedInputs")){
                        JsonNode selectedInputs = deaNode.path("selectedInputs");
                        Iterator<JsonNode> iterator2 = selectedInputs.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            selectedInputHolder.add(input.asInt());
                        }
                    }

                    if (deaNode.has("previousResults")){
                        JsonNode previousResults = deaNode.path("previousResults");
                        Iterator<JsonNode> iterator2 = previousResults.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            previousResultsHolder.add(input.asInt());
                        }
                    }
                    if (deaNode.has("selectedOutputs")){
                        JsonNode selectedOutputs = deaNode.path("selectedOutputs");
                        Iterator<JsonNode> iterator2 = selectedOutputs.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            selectedOutputsHolder.add(input.asInt());
                        }
                    }
                    wrapper = new DEAWrapper(selectedInputHolder, previousResultsHolder, selectedOutputsHolder, stageHolder, inputOrientedHolder, deaIDHolder);
                    this.stage1DEA.add(wrapper);
                }
            }
            if (rootNode.has("stage2DEA")){
                JsonNode stage2DEA = rootNode.path("stage2DEA");
                Iterator<JsonNode> iterator = stage2DEA.iterator();
                DEAWrapper wrapper = null;
                while (iterator.hasNext()){
                    ArrayList<Integer> selectedInputHolder = new ArrayList<>();
                    ArrayList<Integer> selectedOutputsHolder = new ArrayList<>();
                    ArrayList<Integer> previousResultsHolder = new ArrayList<>();
                    int stageHolder = -1;
                    boolean inputOrientedHolder = false;
                    boolean supereffHolder = false;
                    int deaIDHolder = -1;

                    JsonNode deaNode = iterator.next();
                    System.out.print("DeaNode: "+ deaNode);
                    if (deaNode.has("stage"))
                        stageHolder = deaNode.path("stage").asInt();
                    if (deaNode.has("inputOriented"))
                        inputOrientedHolder = deaNode.path("inputOriented").asBoolean();

                    if (deaNode.has("deaID"))
                        deaIDHolder = deaNode.path("deaID").asInt();

                    if (deaNode.has("selectedInputs")){
                        JsonNode selectedInputs = deaNode.path("selectedInputs");
                        Iterator<JsonNode> iterator2 = selectedInputs.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            selectedInputHolder.add(input.asInt());
                        }
                    }

                    if (deaNode.has("previousResults")){
                        JsonNode previousResults = deaNode.path("previousResults");
                        Iterator<JsonNode> iterator2 = previousResults.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            previousResultsHolder.add(input.asInt());
                        }
                    }
                    if (deaNode.has("selectedOutputs")){
                        JsonNode selectedOutputs = deaNode.path("selectedOutputs");
                        Iterator<JsonNode> iterator2 = selectedOutputs.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            selectedOutputsHolder.add(input.asInt());
                        }
                    }
                    wrapper = new DEAWrapper(selectedInputHolder, previousResultsHolder, selectedOutputsHolder, stageHolder, inputOrientedHolder, deaIDHolder);
                    this.stage2DEA.add(wrapper);
                }
            }
            if (rootNode.has("stage3DEA")){
                JsonNode stage3DEA = rootNode.path("stage3DEA");
                Iterator<JsonNode> iterator = stage3DEA.iterator();
                DEAWrapper wrapper = null;
                while (iterator.hasNext()) {
                    ArrayList<Integer> selectedInputHolder = new ArrayList<>();
                    ArrayList<Integer> selectedOutputsHolder = new ArrayList<>();
                    ArrayList<Integer> previousResultsHolder = new ArrayList<>();
                    int stageHolder = -1;
                    boolean inputOrientedHolder = false;
                    boolean supereffHolder = false;
                    int deaIDHolder = -1;

                    JsonNode deaNode = iterator.next();
                    System.out.print("DeaNode: "+ deaNode);
                    if (deaNode.has("stage"))
                        stageHolder = deaNode.path("stage").asInt();
                    if (deaNode.has("inputOriented"))
                        inputOrientedHolder = deaNode.path("inputOriented").asBoolean();

                    if (deaNode.has("deaID"))
                        deaIDHolder = deaNode.path("deaID").asInt();

                    if (deaNode.has("selectedInputs")){
                        JsonNode selectedInputs = deaNode.path("selectedInputs");
                        Iterator<JsonNode> iterator2 = selectedInputs.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            selectedInputHolder.add(input.asInt());
                        }
                    }

                    if (deaNode.has("previousResults")){
                        JsonNode previousResults = deaNode.path("previousResults");
                        Iterator<JsonNode> iterator2 = previousResults.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            previousResultsHolder.add(input.asInt());
                        }
                    }
                    if (deaNode.has("selectedOutputs")){
                        JsonNode selectedOutputs = deaNode.path("selectedOutputs");
                        Iterator<JsonNode> iterator2 = selectedOutputs.iterator();
                        while (iterator2.hasNext()){
                            JsonNode input = iterator2.next();
                            selectedOutputsHolder.add(input.asInt());
                        }
                    }
                    wrapper = new DEAWrapper(selectedInputHolder, previousResultsHolder, selectedOutputsHolder, stageHolder, inputOrientedHolder, deaIDHolder);
                    this.stage3DEA.add(wrapper);
                }
            }
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }




    }

    /*---------Getters---------*/
    public boolean isSuperEff() {
        return superEff;
    }

    public String getSolver() {
        return solver;
    }

    public String getSelectedMethod() {
        return selectedMethod;
    }

    public int getLeagueID() {
        return leagueID;
    }

    public String getLeagueName() {
        return leagueName;
    }

    public int getSeason() {
        return season;
    }

    public int getNumberOfTeams() {
        return numberOfTeams;
    }

    public int getTeamID() {
        return teamID;
    }

    public String getTeamName() {
        return teamName;
    }

    public ArrayList<Integer> getSelectedInputs() {
        return selectedInputs;
    }

    public ArrayList<String> getSelectedInputsNames() {
        return selectedInputsNames;
    }

    public ArrayList<Integer> getSelectedOutputs() {
        return selectedOutputs;
    }

    public ArrayList<String> getSelectedOutputsNames() {
        return selectedOutputsNames;
    }

    public ArrayList<DEAWrapper> getStage1DEA() {
        return stage1DEA;
    }

    public ArrayList<DEAWrapper> getStage2DEA() {
        return stage2DEA;
    }

    public ArrayList<DEAWrapper> getStage3DEA() {
        return stage3DEA;
    }

    public int getNumberOfSeasons() {
        return numberOfSeasons;
    }
}
