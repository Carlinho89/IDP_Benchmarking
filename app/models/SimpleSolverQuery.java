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
public class SimpleSolverQuery {
    public int leagueID;
    public int season;
    public int teamID;
    public List<Integer> selectedInputs;
    public List<Integer> selectedOutputs;


    public SimpleSolverQuery(String query) {

        try {

            selectedInputs = new ArrayList<Integer>();
            selectedOutputs = new ArrayList<Integer>();
            ObjectMapper mapper = new ObjectMapper();

            JsonNode rootNode = mapper.readTree(query);

            this.leagueID =rootNode.path("leagueID").asInt();
            System.out.println("leagueID: " + this.leagueID);
            this.season = rootNode.path("season").asInt();
            System.out.println("season: " + this.season);
            this.teamID = rootNode.path("teamID").asInt();
            System.out.println("teamID: " + this.teamID);


            JsonNode selectedInputsNode = rootNode.path("selectedInputs");
            Iterator<JsonNode> iterator = selectedInputsNode.iterator();
            System.out.print("selectedInputs: [ ");

            while (iterator.hasNext()) {
                JsonNode input = iterator.next();
                System.out.print(input.asInt() + " ");
                selectedInputs.add(input.asInt());
            }

            System.out.println("]");

            JsonNode selectedOutputsNode = rootNode.path("selectedOutputs");
             iterator = selectedOutputsNode.iterator();
            System.out.print("selectedOutputs: [ ");

            while (iterator.hasNext()) {
                JsonNode output = iterator.next();
                System.out.print(output.asInt() + " ");
                selectedInputs.add(output.asInt());
            }

            System.out.println("]");
        }
        catch (JsonParseException e) { e.printStackTrace(); }
        catch (JsonMappingException e) { e.printStackTrace(); }
        catch (IOException e) { e.printStackTrace(); }



    }


}
