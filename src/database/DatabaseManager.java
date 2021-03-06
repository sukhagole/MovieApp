package database;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import model.CinemaTableModel;
import model.Movie;
import model.MovieTableModel;

import java.util.Date;

import model.MovieTableModel;

import java.sql.*;

public class DatabaseManager {

    private static Connection conn;
    private MovieTableModel movieTableModel;
    private CinemaTableModel cinemaTableModel;

    public DatabaseManager(){
        movieTableModel = new MovieTableModel();
        cinemaTableModel = new CinemaTableModel();
    }

    //ESTABLISHES CONNECTION TO DATABASE
    //IN MY CASE, MY DATABASE NAME = CSCI370
    public void getConnection() throws Exception {
        try{
            String classpath = System.getProperty("java.class.path");
            System.out.println(classpath);

            String driver = "com.mysql.cj.jdbc.Driver";
            String url = "jdbc:mysql://csci370email.ctfatxog5vne.us-east-2.rds.amazonaws.com/CS370email?autoReconnect=true&useSSL=false";
            String username = "dbteam";
            String password = "opensesame420";
            Class.forName(driver);
            this.conn = DriverManager.getConnection(url,username,password);
        } catch(Exception e) {
            System.out.println(e); }
            retrieveMovies();
            retrieveCinemas();
    }

    public void createTable() throws Exception{
        try{
            //GETTING SQL STATEMENT READY FOR USE LATER
            PreparedStatement create = conn.prepareStatement(
                    "CREATE TABLE IF NOT EXISTS inbox(id int NOT NULL AUTO_INCREMENT, username VARCHAR(255), PRIMARY KEY(id))");
            create.executeUpdate();


        } catch(Exception e) {
            System.out.println(e);}
        finally {
            System.out.println("function completed");
        }
    }

    public void createNewMovie(
                                 String movieTitle,
                                 String rating,
                                 String releaseType,
                                 String imageURL,
                                 String summary) throws Exception{

        summary = summary.replaceAll("'", "''");
        try{
            PreparedStatement stmt = this.conn.prepareStatement(
                    "INSERT INTO `CS370email`.`" + "MovieList" + "` (`Title`, `Rating`, " +
                            "`ReleaseType`, `urlImage`, `Summary`) VALUES ('" + movieTitle + "','" + rating + "','" +
                            releaseType + "','" + imageURL + "','" + summary + "');");
            //POST NEW ENTRY
            stmt.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void createNewCinema(
            String cinemaName,
            String address) throws Exception{

        try{
            PreparedStatement stmt = this.conn.prepareStatement(
                    "INSERT INTO `CS370email`.`" + "Cinemas" + "` (`cinemaName`, `Address`) VALUES ('" + cinemaName +
                            "','" + address + "');");
            //POST NEW ENTRY
            stmt.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void addMovieToCinema( String movieTitle, String cinemaName, String showtime) throws Exception{

        try{
            PreparedStatement stmt = this.conn.prepareStatement(
                    "INSERT INTO `CS370email`.`" + "movies-cinema" + "` (`movieID`, `cinemaID`, `showTimes`) VALUES ('" + movieTitle +
                            "','" + cinemaName +  "','" + showtime + "');");
            //POST NEW ENTRY
            stmt.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }
    }

    public void updateDraftMessage(String message,
                                   String recipient,
                                   String sender,
                                   String subject,
                                   int id) throws Exception{
        Date date = new Date();
        Timestamp timestamp = new Timestamp(date.getTime());
        message = message.replaceAll("'", "''");
        try{
            PreparedStatement stmt = this.conn.prepareStatement(
                    "UPDATE `CS370email`.`DraftTable` SET `Timestamp` = '" + timestamp + "', " +
                            "`Recipient` = '" + recipient + "', `Message` = '" + message + "', `Subject` = '" + subject +"' WHERE (`ID` = '" + id + "');");
            //POST NEW ENTRY
            stmt.executeUpdate();
        }catch (Exception e){
            System.out.println(e);
        }
    }

//    public void deleteDraftMessage(Message message) throws Exception{
//        try{
//            PreparedStatement stmt = this.conn.prepareStatement(
//                    "DELETE FROM `CS370email`.`DraftTable` WHERE (`ID` = '" + message.getID() + "');");
//            //POST NEW ENTRY
//            stmt.executeUpdate();
//            draftMessageList.removeMessage(message);
//        }catch (Exception e){
//            System.out.println(e);
//        }
//    }
//
//    public void deleteMessage(Message message, String table, MessageTableModel model) throws Exception{
//        try{
//            PreparedStatement stmt = this.conn.prepareStatement(
//                    "DELETE FROM `CS370email`.`" + table + "` WHERE (`ID` = '" + message.getID() + "');");
//            //POST NEW ENTRY
//            stmt.executeUpdate();
//            model.removeMessage(message);
//        }catch (Exception e){
//            System.out.println(e);
//        }
//    }

    //retrieve data from DataBase method
    public void retrieveMovies() throws Exception{
        try {
            movieTableModel.reset();
            Statement stmt = conn.createStatement();

            String sql = "SELECT `Title`, " +
                    "`Rating`, `ReleaseType`, `urlImage`, `Summary` FROM MovieList";
            ResultSet rs = stmt.executeQuery(sql);

            //System.out.println("So far so good");
            while(rs.next()){
                movieTableModel.addMovie(
                        rs.getString("Title"),
                        rs.getString("Rating"),
                        rs.getString("ReleaseType"),
                        rs.getString("urlImage"),
                        rs.getString("Summary")
                );
            }
            rs.close();
        }
        catch (Exception e){
            System.err.println(e);
        }
    }

    //retrieve data from DataBase method
    public void retrieveCinemas() throws Exception{
        try {
            cinemaTableModel.reset();
            Statement stmt = conn.createStatement();

            String sql = "SELECT `cinemaName`, `Address` FROM Cinemas";
            ResultSet rs = stmt.executeQuery(sql);

            //System.out.println("So far so good");
            while(rs.next()){
                cinemaTableModel.addCinema(
                        rs.getString("cinemaName"),
                        rs.getString("Address")
                );
            }
            rs.close();
        }
        catch (Exception e){
            System.err.println(e);
        }
    }
    
//    public void retrieveCinemas() throws Exception{
//        try {
//            ObservableList<String> listOfCinemas = FXCollections.observableArrayList();
//
//            Statement stmt = conn.createStatement();
////            String sql = "SELECT `Location` FROM Cinemas WHERE `Title` = '" + movieTitle + "';";
//            String sql = "SELECT `cinemaName` FROM Cinemas WHERE `Title` = '" + movieTitle + "';";
//
//            ResultSet rs = stmt.executeQuery(sql);
//
//            while(rs.next()){
//                listOfCinemas.add(
//                        rs.getString("Location")
//                );
//            }
//            rs.close();
//            movie.setListOfCinemas(listOfCinemas);
//        }
//        catch (Exception e){
//            System.err.println(e);
//        }
//    }


    //retrieve data from DataBase method
//    public void retrieveDraftMessages() throws Exception{
//        try {
//            draftMessageList.reset();
//            Statement stmt = conn.createStatement();
//
//            String sql = "SELECT DATE_FORMAT(Timestamp, '%a %b %e %l:%i %p') As Time_stamp, `Sender`, `Recipient`, " +
//                    "`Subject`, `Message`, `ID` FROM DraftTable WHERE Sender = '" + currentUser + "' " +
//                    "ORDER BY `Timestamp`;";
//            ResultSet rs = stmt.executeQuery(sql);
//
//            //System.out.println("So far so good");
//            while(rs.next()){
//                draftMessageList.addMessage(
//                        rs.getString("Sender"),
//                        rs.getString("Recipient"),
//                        rs.getString("Subject"),
//                        rs.getString("Time_stamp"),
//                        rs.getString("Message"),
//                        rs.getInt("ID")
//                );
//            }
//            draftMessageList.setDraftMessage(true);
//            rs.close();
//        }
//        catch (Exception e){
//            System.err.println(e);
//        }
//    }


//     CHECK IF MOVIE TITLE EXISTS AFTER ADDING MOVIE
//     RETURN O OTHERWISE
//    public boolean Authenticate(String movieTitle){
//        int success;
//        try{
//            String sql = "SELECT EXISTS(SELECT 1 FROM MovieList WHERE Title = '" + movieTitle + "');";
//            Statement stmt = conn.createStatement();
//            ResultSet rs = stmt.executeQuery(sql);
//            rs.next();
//            success = rs.getInt(1);
//            if (success == 1) {
//                setCurrentUser(username);
//                retrieveMessages("Inbox", messageTableModel);
//                return true;
//            }
//        } catch (Exception e) {
//            System.out.println(e);
//        }
//        return false;
//    }

    public boolean checkIfMovieTitleExists(String movieTitle){
        int success;
        try{
            String sql = "SELECT EXISTS(SELECT 1 FROM MovieList WHERE Title = '" + movieTitle + "');";
            Statement stmt = conn.createStatement();
            ResultSet rs = stmt.executeQuery(sql);
            rs.next();
            success = rs.getInt(1);
            if (success == 1) {
                return true;
            }
        } catch (Exception e) {
            System.out.println(e);
        }
        return false;
    }

//    public boolean isCorrectFormat(String username) {
//        // TRUNCATE DOMAIN NAME
//        String domainName;
//        String userName;
//        try {
//            domainName = username.split("@")[1];
//            userName = username.split("@")[0];
//            if (userName.equals("")) {
//                return false;
//            }
//        } catch (Exception e) {
//            return false;
//        }
//        // TEST IF DOMAIN NAME IS ACCEPTABLE AND RETURN 1 IF TRUE 0 OTHERWISE
//        return checkDomainNameEligibility(domainName);
//    }

//    private boolean checkDomainNameEligibility(String userDomainName) {
//        for (int i = 0; i < AcceptableDomainNames.length; i++) {
//            if (userDomainName.equals(AcceptableDomainNames[i])) {
//                return true;
//            }
//        }
//        return false;
//    }
//
//    public String[] getAcceptableDomainNames() {
//        return AcceptableDomainNames;
//    }
//
//    private void setCurrentUser (String username) {
//        currentUser = username;
//    }
//
//    public String getCurrentUser () {
//        return currentUser;
//    }
//
    public MovieTableModel getMovieTableModel() {
        return movieTableModel; }

    public CinemaTableModel getCinemaTableModel() {
        return cinemaTableModel;
    }
//
//    public MessageTableModel getDraftMessageList() {
//        return draftMessageList;
//    }
//
//    public MessageTableModel getOutboxMessageList() {
//        return outboxMessageList;
//    }
//
//    public MessageTableModel getSpamMessageList() {
//        return spamMessageList;
//    }
//
//    public void updateMessageTableModel(String tableName) {
//        messageTableModel.reset();
//        try{
//            retrieveMessages(tableName, messageTableModel);
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//    }
//
//    public void updateSpamMessageTableModel(String tableName) {
//        spamMessageList.reset();
//        try{
//            retrieveMessages(tableName, spamMessageList);
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//    }
//
//    public void updateOutboxMessageList(String tableName) {
//        outboxMessageList.reset();
//        try{
//            retrieveMessages(tableName, outboxMessageList);
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//    }
//
//    public void updateDraftMessageList() {
//        draftMessageList.reset();
//        draftMessageList.setDraftMessage(true);
//        try{
//            retrieveDraftMessages();
//        } catch (Exception e) {
//            System.err.println(e);
//        }
//    }
}
