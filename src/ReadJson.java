import java.awt.*;
import java.awt.event.*;
import javax.swing.*;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;

import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ReadJson implements ActionListener {

    private JFrame mainFrame;
    private JTextField searchField;
    private JButton searchButton;
    private JTextArea resultsArea;

    public static void main(String[] args) {
        new ReadJson();
    }

    public ReadJson() {
        prepareGUI();
    }

    private void PokemonRead(String pokemonName) {
        try {
            URL url = new URL("https://pokeapi.co/api/v2/pokemon/" + pokemonName.toLowerCase());
            URLConnection urlc = url.openConnection();
            urlc.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader reader = new BufferedReader(new InputStreamReader(urlc.getInputStream()));
            String line, jsonText = "";
            while ((line = reader.readLine()) != null) jsonText += line;
            reader.close();

            JSONParser parser = new JSONParser();
            JSONObject jsonObject = (JSONObject) parser.parse(jsonText);

            resultsArea.setText("");
            resultsArea.append("Name: " + jsonObject.get("name") + "\n\n");

            JSONObject sprites = (JSONObject) jsonObject.get("sprites");
            String imageUrl = (String) sprites.get("front_default");
            if (imageUrl != null) resultsArea.append("Image URL: " + imageUrl + "\n\n");

            resultsArea.append("Types:\n");
            JSONArray types = (JSONArray) jsonObject.get("types");
            for (Object o : types) {
                JSONObject typeObj = (JSONObject) o;
                JSONObject typeData = (JSONObject) typeObj.get("type");
                resultsArea.append("- " + typeData.get("name") + "\n");
            }

            resultsArea.append("\nAbilities:\n");
            JSONArray abilities = (JSONArray) jsonObject.get("abilities");
            for (Object o : abilities) {
                JSONObject abilityObj = (JSONObject) o;
                JSONObject abilityData = (JSONObject) abilityObj.get("ability");
                resultsArea.append("- " + abilityData.get("name") + "\n");
            }

            JSONObject species = (JSONObject) jsonObject.get("species");
            String speciesUrl = (String) species.get("url");
            URL sUrl = new URL(speciesUrl);
            URLConnection sConn = sUrl.openConnection();
            sConn.setRequestProperty("User-Agent", "Mozilla/5.0");

            BufferedReader sReader = new BufferedReader(new InputStreamReader(sConn.getInputStream()));
            String speciesJson = "";
            while ((line = sReader.readLine()) != null) speciesJson += line;
            sReader.close();

            JSONObject speciesObj = (JSONObject) parser.parse(speciesJson);
            JSONObject generation = (JSONObject) speciesObj.get("generation");
            resultsArea.append("\nRegion: " + generation.get("name"));

        } catch (Exception ex) {
            resultsArea.setText("Pokémon not found.");
        }
    }

    private void prepareGUI() {
        mainFrame = new JFrame("Pokédex Search");
        mainFrame.setSize(500, 600);
        mainFrame.setLayout(new GridLayout(4, 1));

        searchField = new JTextField("Enter Pokémon Name:");
        searchButton = new JButton("Search");
        searchButton.addActionListener(this);

        resultsArea = new JTextArea();
        resultsArea.setEditable(false);
        JScrollPane scrollPane = new JScrollPane(resultsArea);

        mainFrame.add(searchField);
        mainFrame.add(searchButton);
        mainFrame.add(scrollPane);

        mainFrame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        mainFrame.setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        String pokemon = searchField.getText().trim();
        if (pokemon.isEmpty() || pokemon.equals("Enter Pokémon Name:")) return;
        PokemonRead(pokemon);
    }
}
