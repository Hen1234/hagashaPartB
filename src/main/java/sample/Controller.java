package sample;

import Model.*;
import com.sun.scenario.effect.impl.sw.sse.SSEBlend_SRC_OUTPeer;
import com.sun.xml.internal.ws.policy.privateutil.PolicyUtils;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.shape.Path;
import javafx.stage.DirectoryChooser;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import org.apache.commons.io.FileUtils;
import org.apache.commons.lang3.SerializationUtils;
//import org.controlsfx.control.CheckComboBox;
import sun.reflect.generics.tree.Tree;

import java.io.*;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

/**
 * The class represent the Controller of the GUI
 */
public class Controller implements Initializable {

    static ReadFile reader;
    static Searcher searcher;

    //bring from disc to memory
    public TreeMap<String, String> Dictionary;
    public HashMap<String, Docs> Documents;
    public ArrayList<String> DicToShow;
    HashMap<String, City> CitiesHashMap;
    HashSet<String> LanguagesHashSet;
    public ArrayList<String> citiesList;

    @FXML
    public boolean corpusPathIsNull;
    public boolean postingPathIsNull;
    public Button Run;
    public Button LoadCorpus;
    public Button SavePosting;
    public Button reset;
    public Button ShowDictionary;
    public Button LoadDictionary;
    public Button LoadQueryFile;
    public Button RunQuery;
    public Button ChooseResultPath;
    public Button RunQueryFile;
    public TextField txt_fiedResultPath;
    public TextField txt_fiedCorpus;
    public TextField txt_fiedPosting;
    public TextField txt_fiedQueries;
    public TextField txt_fiedInsertQuery;
    public CheckBox Stemming;
    public CheckBox FilterByCity;
    public CheckBox isSemantic;
    public CheckBox ShowEntities;
    public ComboBox Languages;
    public ChoiceBox RelevantDocs;
    public Alert badPathAlert;
    public Alert validCorpusAlert;
    public Label labelEntities;
    public String FirstPath;
    public String SecondPath;
    public String quertPathFromUser;
    public String pathFromUser;
    public String resultPath;

    public MenuButton menu;
    public ObservableList<MenuItem> menuItem;


    @Override
    public void initialize(URL location, ResourceBundle resources) {


        try {
            reader = new ReadFile();
        } catch (Exception e){}
        FirstPath = "";
        SecondPath = "";
        corpusPathIsNull = true;
        postingPathIsNull = true;
        badPathAlert = new Alert(Alert.AlertType.ERROR, "Please insert Valid path", ButtonType.OK);
        validCorpusAlert = new Alert(Alert.AlertType.ERROR, "Please insert Valid Corpus path", ButtonType.OK);
        searcher = new Searcher();
        Stemming.setSelected(false);
        reset.setDisable(true);
        ShowDictionary.setDisable(true);
        quertPathFromUser = "";
        FilterByCity.setSelected(false);
        RunQuery.setDisable(true);
        RunQueryFile.setDisable(true);
        LoadQueryFile.setDisable(true);
        LoadDictionary.setDisable(true);
        Run.setDisable(true);
        ChooseResultPath.setDisable(true);


    }

    /**
     * The method is called while the user press the "Run" button.
     * it presents a message if the user did not fill the all the fields.
     * it operates the method "ReadJsoup" of the ReadFile object.
     * it calculates the time of the whole process.
     * it gets the languages from the ReadFile object and initialize the "combobox" of the languages.
     * it presents an information message of the process at the end of it.
     *
     * @param event
     * @throws Exception
     */
    public void run(ActionEvent event) throws Exception {

        if (txt_fiedCorpus.getText().isEmpty() || txt_fiedPosting.getText().isEmpty()) {

            showAlert("Message", "Error", "All the fields should be full");
        } else {
            final long startTime = System.nanoTime();
            try {
                reader.setPostingPath(txt_fiedPosting.getText());
                reader.ReadJsoup();
            } catch (Exception e) {
                validCorpusAlert.show();
                return;
            }

            //init the Languages
            HashSet<String> languages = reader.getLanguages();
            Languages.setItems(FXCollections.observableArrayList(languages));
            Languages.setDisable(false);

            //setDisable to the relevant buttons
            reset.setDisable(false);
            ShowDictionary.setDisable(false);
            LoadDictionary.setDisable(false);
            ChooseResultPath.setDisable(false);

            citiesCombo();

            //init documents
            Documents = reader.getIndexer().getDocsHashMap();

            //setDisable to the relevant buttons
            LoadQueryFile.setDisable(false);
            RunQuery.setDisable(false);

            //show the details message
            StringBuilder data = new StringBuilder("Number of Documents: ");
            data.append(reader.getIndexer().getDocuments().size());
            data.append(System.lineSeparator());
            data.append("Number of Unique Terms: ");
            data.append(reader.getIndexer().getSorted().size());
            data.append(System.lineSeparator());
            data.append("Run Time: ");
            final long duration = System.nanoTime() - startTime;
            data.append(duration * (Math.pow(10, -9)));
            data.append(" seconds");
            data.append(System.lineSeparator());
            showAlert("Data Message", "Process Information", data.toString());

        }

    }

    /**
     * The method presents a message according to the arguments it gets
     *
     * @param message - the content of the message
     * @param title   - the title of the message
     * @param header  - the header of the message
     */
    private void showAlert(String message, String title, String header) {

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setContentText(message);
        alert.setTitle(title);
        alert.setHeaderText(header);
        alert.show();
    }

    private boolean askToDelete() {

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation Dialog");
        alert.setHeaderText("let it go let it go, can't hold it back anymore");
        alert.setContentText("Are you sure you want to delete all Stracures from memory?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.get() == ButtonType.OK) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * The method is called while the user enters the path of the corpus and the
     * stop words list.
     * it sets the given path to the field of the ReadFile object.
     *
     * @param event
     * @throws IOException
     */
    public void corpusPath(ActionEvent event) throws IOException {
        Stage stage = new Stage();
        DirectoryChooser dir = new DirectoryChooser();
        File corpusFromUser = dir.showDialog(stage);
        if (corpusFromUser != null) {
            //pathFromUser = corpusFromUser.getPath();
            reader.setCorpusPath(corpusFromUser.getPath());
            txt_fiedCorpus.setText(corpusFromUser.getPath());
            corpusPathIsNull = false;
            if (!postingPathIsNull)
                Run.setDisable(false);
        }

    }

    /**
     * The method is called while the user enters the path of the posting files
     * it sets the given path to the field of the ReadFile object.
     *
     * @throws IOException
     */
    public void postingPath() throws IOException {
        Stage stage = new Stage();
        DirectoryChooser dir = new DirectoryChooser();
        File postingPathFromUser = dir.showDialog(stage);
        if (postingPathFromUser != null) {
            pathFromUser = postingPathFromUser.getPath();
            reader.setPostingPath(postingPathFromUser.getPath());
            txt_fiedPosting.setText(postingPathFromUser.getPath());
            postingPathIsNull = false;
            LoadDictionary.setDisable(false);
            if (!corpusPathIsNull) {
                Run.setDisable(false);
            }
        }
    }

    public void resultPath() throws IOException {
        Stage stage = new Stage();
        DirectoryChooser dir = new DirectoryChooser();
        File ResultPathFromUser = dir.showDialog(stage);
        if (ResultPathFromUser != null) {
            resultPath = ResultPathFromUser.getPath();
            searcher.setResultPath(resultPath);
            txt_fiedResultPath.setText(ResultPathFromUser.getPath());
            postingPathIsNull = false;
            if (!txt_fiedQueries.getText().equals(""))
                RunQueryFile.setDisable(false);
        }
    }

    public void queriesPath(ActionEvent event) throws IOException {

        Stage stage = new Stage();
        FileChooser dir = new FileChooser();
        File queriesFromUser = dir.showOpenDialog(stage);
        if (queriesFromUser != null) {
            txt_fiedQueries.setText(queriesFromUser.getPath());
            if (!txt_fiedResultPath.getText().equals(""))
                RunQueryFile.setDisable(false);
        }
    }

    /**
     * The method is called while the user marks the option of "Stemming".
     * it sets the given choice to the field of the ReadFile object.
     *
     * @param event
     */
    public void stemming(ActionEvent event) {
        if (Stemming.isSelected()) {
            reader.setStemming(true);
        } else {
            reader.setStemming(false);
        }
    }

    /**
     * The method is called while the user press the "Reset" button.
     * it deletes the whole posting files and dictionary and reset the main memory of the process.
     *
     * @param event
     * @throws IOException
     */
    public void reset(ActionEvent event) throws IOException {
        if (!askToDelete()) {
            return;
        }
        String pathToDelete = reader.getPostingPath();
        //FileUtils.cleanDirectory(new File(pathToDelete));
        try {
            FileUtils.deleteDirectory(new File(reader.getPostingPath()));
        } catch (IOException e) {
        }
        reader = new ReadFile();
        Stemming.setSelected(false);
        reset.setDisable(true);
        ShowDictionary.setDisable(true);
        quertPathFromUser = "";
        FilterByCity.setSelected(false);
        RunQuery.setDisable(true);
        RunQueryFile.setDisable(true);
        LoadQueryFile.setDisable(true);
        //LoadDictionary.setDisable(true);
        Run.setDisable(true);
        ChooseResultPath.setDisable(true);
    }

    /**
     * The method is called while the user press the "Show Dictionary" button.
     * it creats a new stage for the list of the words in the dictionary
     *
     * @param event
     */
    public void showDictionary(ActionEvent event) {

        try {

            Stage stage = new Stage();
            stage.setTitle("Dictionary");
            Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ShowDic.fxml"));
            Scene scene = new Scene(root, 700, 500);
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.show();


        } catch (Exception e) {   //////exception not found
        }
    }

    /**
     * The method is called while the user press the "Load Dictionary" button.
     * it loads the dictionary to the memory.
     *
     * @throws IOException
     */
    public void loadDictionary() throws IOException, ClassNotFoundException {

        String postpath = pathFromUser;
        FileInputStream f = null;
        try {
            f = new FileInputStream(new File(postpath + "\\" + "SortedAsObject.txt"));
            ObjectInputStream o = new ObjectInputStream(f);
            Dictionary = (TreeMap<String, String>) o.readObject();
            reader.getIndexer().setSorted(Dictionary);
            //searcher.setDictionary(Dictionary);
            o.close();
        } catch (Exception e) {
            badPathAlert.show();
            return;
        }

        try {
            loadDocuments();
        } catch (Exception e) {
            e.printStackTrace();
            badPathAlert.show();
            return;
        }

        try {
            loadHeaders();
        } catch (Exception e) {
            badPathAlert.show();
            return;
        }

        try {
            loadDicToShow();
        } catch (Exception e) {
            badPathAlert.show();
            return;
        }

        try {
            loadCities();
        } catch (Exception e) {
            badPathAlert.show();
            return;
        }

        try {
            loadLanguages();
        } catch (Exception e) {
            badPathAlert.show();
            return;
        }

        //update the postPath
        reader.getIndexer().setPathDir(postpath);
        //set Disable to the next buttons
        reset.setDisable(false);
        ShowDictionary.setDisable(false);
        RunQuery.setDisable(false);
        ChooseResultPath.setDisable(false);
        //RunQueryFile.setDisable(false);
        LoadQueryFile.setDisable(false);
        showAlert("Data Message", "Process Information", "Data loaded successfully!");

    }

    /**
     * The method loads the languages to the memory
     * @throws IOException
     * @throws ClassNotFoundException
     */
    private void loadLanguages() throws IOException, ClassNotFoundException {

        String postpath = pathFromUser;
        FileInputStream f = null;
        f = new FileInputStream(new File(postpath + "\\" + "LanguagesAsObject.txt"));
        ObjectInputStream o = new ObjectInputStream(f);
        LanguagesHashSet = (HashSet<String>) o.readObject();
        reader.setLanguages(LanguagesHashSet);
        o.close();

        //init the Languages
        Languages.setItems(FXCollections.observableArrayList(LanguagesHashSet));
        Languages.setDisable(false);


    }

    /**
     * The method loads the cities to the memory
     * @throws IOException
     */
    private void loadCities() throws IOException {

        String postpath = pathFromUser;
        byte[] encode = Files.readAllBytes(Paths.get(postpath + File.separator + "CitiesAsObject.txt"));
        byte[] output = Base64.getMimeDecoder().decode(encode);
        Object out = SerializationUtils.deserialize(output);
        CitiesHashMap = ((HashMap<String, City>) out);

        reader.setCities(CitiesHashMap);

        citiesCombo();


    }

    /**
     * The method loads the DicToShow to the memory
     * @throws IOException
     * @throws ClassNotFoundException
     */
    public void loadDicToShow() throws IOException, ClassNotFoundException {

        String postpath = pathFromUser;
        FileInputStream f = null;
        f = new FileInputStream(new File(postpath + "\\" + "DicToShowAsObject.txt"));
        ObjectInputStream o = new ObjectInputStream(f);
        DicToShow = (ArrayList<String>) o.readObject();
        reader.getIndexer().setDicToShow(DicToShow);
        o.close();

    }

    /**
     * The method loads the documents to the memory
     * @throws IOException
     */
    private void loadDocuments() throws IOException {
        String postpath = pathFromUser;

        byte[] encode = Files.readAllBytes(Paths.get(postpath + File.separator + "DocsAsObject.txt"));
        byte[] output = Base64.getMimeDecoder().decode(encode);
        Object out = SerializationUtils.deserialize(output);
        Documents = ((HashMap<String, Docs>) out);

        reader.getIndexer().setDocsHashMap(Documents);
    }

    /**
     * The method loads the headers to the memory
     * @throws IOException
     */
    private void loadHeaders() throws IOException {
        String postpath = pathFromUser;

        byte[] encode = Files.readAllBytes(Paths.get(postpath + File.separator + "TermsInHeaderAsObject.txt"));
        byte[] output = Base64.getMimeDecoder().decode(encode);
        Object out = SerializationUtils.deserialize(output);
        HashMap temp = ((HashMap<String, HashSet<String>>) out);
        reader.getP().setTermsInHeaderToDoc(temp);
    }

    /**
     * The method runs get the path of the queries file from the user
     * @param event
     * @throws IOException
     */
    public void runQueriesPath(ActionEvent event) throws IOException {
        String queriesFromUser = txt_fiedQueries.getText();
        if (queriesFromUser != null) {
            try {
                if (!FilterByCity.isSelected()) {
                    searcher.setCities(null);

               }
                if (Stemming.isSelected()) {
                    reader.setStemming(true);
                } else {
                    reader.setStemming(false);
                }
                if(!isSemantic.isSelected()){
                    searcher.setSemantic(false);
                }else{
                    searcher.setSemantic(true);
                }
                searcher.readQueriesFile(queriesFromUser);
            } catch (Exception e) {
                badPathAlert.setContentText("Please choose a valid path for queries file");
                badPathAlert.show();
                badPathAlert.setContentText("Please insert Valid path");
                return;
            }
        }


        showAlert("Data Message", "Process Information", "QueriesFile run successfully!");

    }

    /**
     * The method checks if the user selected filter by city and set the cities in the searcher
     */
    public void FilterByCity() {

        if (FilterByCity.isSelected()) {
            HashSet<String> citiesHashSet = new HashSet<>();
            for (MenuItem x : menu.getItems()) {
                CheckMenuItem item = (CheckMenuItem) x;
                if (item.isSelected()) {
                    citiesHashSet.add(item.getText());
                }

            }
            searcher.setCities(citiesHashSet);
        }

    }

    /**
     * The method gets one query from the user and sends it to the Searcher
     * @throws IOException
     */
    public void getQueryFromUser() throws IOException {

        String query = txt_fiedInsertQuery.getText();
        if (query == null || query.equals("") || query.equals(" ")) {
            showAlert("Message", "Error", "Please insert a valid query");
            return;
        }

        try {
            if (!FilterByCity.isSelected()) {
                searcher.setCities(null);

            }
            if (Stemming.isSelected()) {
                reader.setStemming(true);
            } else {
                reader.setStemming(false);
            }
            if(!isSemantic.isSelected()){
                searcher.setSemantic(false);
            }else{
                searcher.setSemantic(true);
            }
            searcher.pasreQuery(query);
        } catch (Exception e) {


            badPathAlert.setContentText("Please insert query for search");
            badPathAlert.show();
            badPathAlert.setContentText("Please insert Valid path");
            return;
        }
        //new stage for the list of the ranked doc
        Stage stage = new Stage();
        stage.setTitle("Results");
        Parent root = FXMLLoader.load(getClass().getClassLoader().getResource("ShowQueryResults.fxml"));
        Scene scene = new Scene(root, 500, 400);
        stage.setScene(scene);
        stage.initModality(Modality.APPLICATION_MODAL);
        stage.show();

        //init the docs for show entities
        ArrayList<String> QueryResultsList = searcher.getQueryResults();
        ObservableList<String> QueryResultsListObser = FXCollections.observableArrayList();

        for (String key : QueryResultsList) {
            QueryResultsListObser.add(key);
        }

        RelevantDocs.setItems(QueryResultsListObser);
        searcher.setQueryResults(new ArrayList<String>());


    }

    /**
     * The method checks if the user selected "semantic"
     */
    public void isSemantic() {

        if (isSemantic.isSelected()) {

            searcher.setSemantic(true);
        }

    }

    /**
     * The method shows the entities of the doc
     */
    public void showEntities() {


        String docChoiced = (String) RelevantDocs.getValue();
        if (docChoiced != null) {
            Docs choicesDoc = (Docs) Documents.get(docChoiced);
            PriorityQueue<TermsPerDoc> newDocQueue = new PriorityQueue<>();
            String entities = "";

            while (!choicesDoc.getMostFiveFrequencyEssences().isEmpty()) {
                TermsPerDoc current = choicesDoc.getMostFiveFrequencyEssences().poll();
                entities = entities + current.getValue() + " - " + current.getTf() + System.lineSeparator();
                newDocQueue.add(current);
            }
            choicesDoc.setMostFiveFrequencyEssences(newDocQueue);
            labelEntities.setText(entities);


        }

    }

    /**
     * The method initialize the citiesComboBox
     */
    private void citiesCombo() {

        try{

            CitiesHashMap = reader.getCities();
            citiesList = new ArrayList<>();
            for (String s : CitiesHashMap.keySet()) {

                citiesList.add(s);
            }

            ObservableList<String> citiesObserv = FXCollections.observableArrayList(citiesList);
            for (String s1 : citiesObserv) {
                CheckMenuItem x = new CheckMenuItem();
                x.setText(s1);
                menu.getItems().add(x);
            }

        }catch (Exception e){}


    }


}
