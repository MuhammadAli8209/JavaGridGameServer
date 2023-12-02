package com.jtconnors.multisocketserverfx;

//CREATE OBSTACLES-- done
//CREATE FACTORIES--done
//CREATE DROPS -- done
//CREATE BULLETS--done
//CREATE PORTALS --done
//CREATE BOSS-- none
//FINISH RESPAWN BETWEEN PEOPLE--done
//FINISH DEATH--done
//FINISH WEAPON BULLET TYPES -- done
//FINISH ITEM BEHAVIOR -- done

import com.jtconnors.socket.SocketListener;
import java.net.URL;
import java.util.ArrayList;
import java.util.Random;
import java.util.ResourceBundle;

import javafx.animation.AnimationTimer;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TextField;
import javafx.scene.control.Tooltip;

import com.jtconnors.socketfx.FxMultipleSocketServer;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.GridPane;

/**
 *
 * @author jtconnor
 */
public class FXMLDocumentController implements Initializable {

//    @FXML
//    private TextField sendTextField;
//    @FXML
//    private TextField selectedTextField;
//    @FXML
//    private Button sendButton;
    @FXML
    private Label connectionsLabel, lblsecs;
    @FXML
    private int numberofpplready, numberofpplconnnected;
    @FXML
    private Button connectButton, btnrestart;
    @FXML
    private long starttime, timed, fivesecondrespawn, droprespawn;
    @FXML
    private Button disconnectButton, startbtn;

    @FXML
    private int chosenrow, chosencol;

    @FXML
    private ArrayList<String> namesofppl = new ArrayList<>();

    @FXML
    private String selectedinventoryitemthingy;


    @FXML
    private ArrayList<Portal> allportals = new ArrayList<>();

    @FXML
    private ArrayList<Drop> alldrops = new ArrayList<>();

    @FXML
    private String[] portalcolors = {"#151E3F", "#F6F930", "#10FFCB", "#F18F01", "#49B6FF", "#9B7EDE", "#FF5A5F"};

    @FXML
    private Label readylbl;
    @FXML
    private ArrayList<Player> playerlist = new ArrayList<>();

    @FXML
    private int[][] environmentgrid = new int[50][80];

    @FXML
    private ListView weaponslist, armorlist, itemlist, factoryupglist, playerstatslist, playerinventorylist;
    @FXML
    private GridPane gPane;
    @FXML
    private Button[][] buttongrid = new Button[50][80];

    @FXML
    private TextField portTextField, txtnamebox;
    @FXML
    private ListView<String> rcvdMsgsListView;
//    private ObservableList<String> sentMsgsData;
//    @FXML
//    private ListView<String> sentMsgsListView;
//    private ObservableList<String> rcvdMsgsData;

    private FxMultipleSocketServer socketServer;
//    private ListView<String> lastSelectedListView;
    private Tooltip portTooltip;

    public enum ConnectionDisplayState {

        DISCONNECTED, WAITING, CONNECTED
    }

    private void displayState(ConnectionDisplayState state) {
        switch (state) {
            case DISCONNECTED:
                connectButton.setDisable(false);
                disconnectButton.setDisable(true);
                //sendButton.setDisable(true);
                //sendTextField.setDisable(true);
                connectionsLabel.setText("Not connected");
                break;
            case WAITING:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                //sendButton.setDisable(false);
                //sendTextField.setDisable(false);
                connectionsLabel.setText("Waiting for connections");
                break;
            case CONNECTED:
                connectButton.setDisable(true);
                disconnectButton.setDisable(false);
                
                //sendButton.setDisable(false);
                //sendTextField.setDisable(false);
                int numConnections = socketServer.getListenerCount();
                numberofpplconnnected = numConnections;
                StringBuilder connectionsSB
                        = new StringBuilder(numConnections + " connection");
                if (numConnections != 1) {
                    connectionsSB.append("s");
                }
                connectionsLabel.setText(new String(connectionsSB));
                break;
        }
    }

    class FxSocketListener implements SocketListener {

        @Override
        public void onMessage(String line) {
            if(line!=null){

                if(line.equals("RestartGame")){
                    restart(new ActionEvent());
                }


                if(line.equals("Ready")){
                    numberofpplready+=1;
                    checkifallready();
                } else if (line.contains(":")) {
                    if (line.substring(0,line.indexOf(":")).equals("CreatePlayer")) {
                        String name = line.substring(line.indexOf(":")+1);
                        namesofppl.add(name);
                    }
                    if(line.substring(0,line.indexOf(":")).equals("Move")){
                        String nameandmove = line.substring(line.indexOf(":")+1);
                        String[] splitthing = nameandmove.split(",");
                        for (int i = 0; i < playerlist.size(); i++) {
                            if(splitthing[0].equals(playerlist.get(i).getName())){

                                ArrayList<Location> playerlocs = playerlist.get(i).getPlayerlocation();
                                //boolean playercanmove = true;

                                if(splitthing[1].equals("up")){
                                    if(environmentgrid[playerlocs.get(1).getRow()-1][playerlocs.get(1).getColumn()]==2||environmentgrid[playerlocs.get(3).getRow()-1][playerlocs.get(3).getColumn()]==2){
                                        System.out.println("Cannot Go");
                                    }else{
                                        playerlist.get(i).changeplayerloc("up");
                                    }
                                }
                                if(splitthing[1].equals("down")){
                                    if(environmentgrid[playerlocs.get(0).getRow()+1][playerlocs.get(0).getColumn()]==2||environmentgrid[playerlocs.get(2).getRow()+1][playerlocs.get(2).getColumn()]==2){
                                        System.out.println("Cannot Go");
                                    }else{
                                        playerlist.get(i).changeplayerloc("down");
                                    }
                                }
                                if(splitthing[1].equals("left")){
                                    if(environmentgrid[playerlocs.get(0).getRow()][playerlocs.get(1).getColumn()-1]==2||environmentgrid[playerlocs.get(1).getRow()][playerlocs.get(1).getColumn()-1]==2){
                                        System.out.println("Cannot Go");
                                    }else{
                                        playerlist.get(i).changeplayerloc("left");
                                    }
                                }
                                if(splitthing[1].equals("right")){
                                    if(environmentgrid[playerlocs.get(2).getRow()][playerlocs.get(2).getColumn()+1]==2||environmentgrid[playerlocs.get(3).getRow()][playerlocs.get(3).getColumn()+1]==2){
                                        System.out.println("Cannot Go");
                                    }else{
                                        playerlist.get(i).changeplayerloc("right");
                                    }
                                }

                            }
                        }
                        checkifondrop();
                        checkifonfactory();


                        for (int b = 0; b < playerlist.size(); b++) {
                            if(splitthing[0].equals(playerlist.get(b).getName())){
                                for (int i = 0; i < playerlist.get(b).getFactories().size(); i++) {
                                    for (int j = 0; j < playerlist.get(b).getFactories().get(i).getFactorylocations().size(); j++) {
                                        boolean boo = false;
                                        if(playerlist.get(b).getPlayerlocation().get(0).getRow()==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getRow()&&playerlist.get(b).getPlayerlocation().get(0).getColumn()==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                                            boo = true;
                                        }
                                        if(boo){
                                            socketServer.postUpdate("ClearPlayerFactoryList:"+playerlist.get(b).getName());
                                            Factories thisone = playerlist.get(b).getFactories().get(i);

                                            socketServer.postUpdate("Level:"+playerlist.get(b).getName()+","+thisone.getLevel());
                                            socketServer.postUpdate("MoneyMultiplier:"+playerlist.get(b).getName()+","+thisone.getMoneymultiplier());
                                            socketServer.postUpdate("Health:"+playerlist.get(b).getName()+","+thisone.getHealth());
                                            socketServer.postUpdate("ResourceAmount:"+playerlist.get(b).getName()+","+thisone.getResourceamt());
                                            System.out.println("ResourceAmount:"+thisone.getResourceamt());
                                            socketServer.postUpdate("Upgrade Factory:"+playerlist.get(b).getName());
                                            socketServer.postUpdate("Exit:"+playerlist.get(b).getName());

                                        }
                                    }
                                }
                            }
                        }

                        sendoutplayerinventories();
                        showpeopleonscreen();
                    }

                    if(line.substring(0,line.indexOf(":")).equals("BulletFromClient")){
                        System.out.println("Shouldbullet");
                        String spliotter = line.substring(line.indexOf(":")+1);
                        String [] namecords = spliotter.split(",");
                        System.out.println(namecords[0]);
                        for (int i = 0; i < playerlist.size(); i++) {
                            if(namecords[0].equals(playerlist.get(i).getName())){

                                if(namecords[3].equals("Shotgun")){
                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow(),playerlist.get(i).getPlayerlocation().get(0).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow()-2,playerlist.get(i).getPlayerlocation().get(0).getColumn()+2), playerlist.get(i),new Location(Integer.parseInt(namecords[1])+2, Integer.parseInt(namecords[2])-2)));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgoals = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoals);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow()+3,playerlist.get(i).getPlayerlocation().get(0).getColumn()-2), playerlist.get(i),new Location(Integer.parseInt(namecords[1])-2, Integer.parseInt(namecords[2])+3)));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgoaaal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoaaal);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow()-4,playerlist.get(i).getPlayerlocation().get(0).getColumn()+2), playerlist.get(i),new Location(Integer.parseInt(namecords[1])-4, Integer.parseInt(namecords[2])-3)));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesaaandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesaaandgoal);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow()+3,playerlist.get(i).getPlayerlocation().get(0).getColumn()+2), playerlist.get(i),new Location(Integer.parseInt(namecords[1])+3, Integer.parseInt(namecords[2])+4)));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgwwoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgwwoal);
                                }
                                if(namecords[3].equals("AR")){

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow(),playerlist.get(i).getPlayerlocation().get(0).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                                    allbullets.add(new Bullets(new Location(((Integer.parseInt(namecords[1]) - playerlist.get(i).getPlayerlocation().get(0).getRow())/3)  +playerlist.get(i).getPlayerlocation().get(0).getRow()   ,   ((Integer.parseInt(namecords[2]) - playerlist.get(i).getPlayerlocation().get(0).getColumn())/3)+ playerlist.get(i).getPlayerlocation().get(0).getColumn()  ), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesanssdgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesanssdgoal);


                                    allbullets.add(new Bullets(new Location(((Integer.parseInt(namecords[1]) - playerlist.get(i).getPlayerlocation().get(0).getRow())/3)*2  +playerlist.get(i).getPlayerlocation().get(0).getRow()   ,   ((Integer.parseInt(namecords[2]) - playerlist.get(i).getPlayerlocation().get(0).getColumn())/3)*2+ playerlist.get(i).getPlayerlocation().get(0).getColumn()  ), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinssatesanssdgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinssatesanssdgoal);

                                }


                                if(namecords[3].equals("SMG")){
                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow(),playerlist.get(i).getPlayerlocation().get(0).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(1).getRow(),playerlist.get(i).getPlayerlocation().get(1).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bullssetcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bullssetcoordinatesandgoal);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(2).getRow(),playerlist.get(i).getPlayerlocation().get(2).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String ssasfgee = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+ssasfgee);

                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(3).getRow(),playerlist.get(i).getPlayerlocation().get(3).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String weeerrr = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+weeerrr);


                                }
                                if(namecords[3].equals("Sniper")){
                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow(),playerlist.get(i).getPlayerlocation().get(0).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);
                                }
                                if(namecords[3].equals("Grenade")){
                                    allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow(),playerlist.get(i).getPlayerlocation().get(0).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
                                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                                    allbullets.get(allbullets.size()-1).setgrenade();
                                    String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                                    for (int h = 0; h < playerlist.get(i).getItemsinventory().size(); h++) {
                                        if(playerlist.get(i).getItemsinventory().get(h).getItemname().equals("Grenade")){
                                            playerlist.get(i).getItemsinventory().remove(h);
                                            break;
                                        }
                                    }
                                }
                                if(namecords[3].equals("Invisibility")){

                                    playerlist.get(i).setInvisible(System.nanoTime());
                                    for (int s = 0; s < playerlist.get(i).getItemsinventory().size(); s++) {
                                        if(playerlist.get(i).getItemsinventory().get(s).getItemname().equals("Invisibility")){
                                            playerlist.get(i).getItemsinventory().remove(s);
                                            break;
                                        }
                                    }
                                }
                                if(namecords[3].equals("Shrink")){

                                    playerlist.get(i).setshrunked(System.nanoTime());
                                    for (int s = 0; s < playerlist.get(i).getItemsinventory().size(); s++) {
                                        if(playerlist.get(i).getItemsinventory().get(s).getItemname().equals("Shrink")){
                                            playerlist.get(i).getItemsinventory().remove(s);
                                            break;
                                        }
                                    }
                                }


//                                allbullets.add(new Bullets(new Location(playerlist.get(i).getPlayerlocation().get(0).getRow(),playerlist.get(i).getPlayerlocation().get(0).getColumn()), playerlist.get(i),new Location(Integer.parseInt(namecords[1]), Integer.parseInt(namecords[2]))));
//                                String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
//                                socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);
//                                allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
//                                allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                            }
                        }
                    }

                    if(line.substring(0,line.indexOf(":")).equals("Buy")){
                        String usethis = line.substring(line.indexOf(":")+1);
                        String [] namewhat = usethis.split(",");
                        for (int i = 0; i < allitemsinshop.size(); i++) {

                            System.out.println(namewhat[1]+" BEING COMPARED TO "+allitemsinshop.get(i).getItemname());
                            if(namewhat[1].equals(allitemsinshop.get(i).getItemname())){
                                System.out.println("ClientDidBuy");
                                System.out.println(namewhat[1]);
                                if(namewhat[1].equals("Shrink")){
                                    System.out.println("SHRINKBROUGHT");
                                }
                                clientboughtitem(namewhat);

                                break;
                            }
                        }
                        for (int i = 0; i < allweaponsinshop.size(); i++) {
                            if(namewhat[1].equals(allweaponsinshop.get(i).getWeaponname())){
                                clientboughtweapon(namewhat);
                                break;
                            }
                        }

                        //sendoutplayerinventories();

                    }


                    if(line.substring(0,line.indexOf(":")).equals("ShowFactoryStats")){
                        System.out.println("GottenShowStatsThing");

                        for (int b = 0; b < playerlist.size(); b++) {
                            String usethis = line.substring(line.indexOf(":")+1);
                            String [] namewhat = usethis.split(",");
                            if(namewhat[0].equals(playerlist.get(b).getName())){
                                for (int i = 0; i < playerlist.get(b).getFactories().size(); i++) {
                                    for (int j = 0; j < playerlist.get(b).getFactories().get(i).getFactorylocations().size(); j++) {
                                        boolean boo = false;
                                        if(Integer.parseInt(namewhat[1])==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getRow()&&Integer.parseInt(namewhat[2])==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                                            boo = true;
                                        }
                                        if(boo){
                                            socketServer.postUpdate("StatsLine:"+playerlist.get(b).getName());
                                            System.out.println("SentBackTheThing");
                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(line.substring(0,line.indexOf(":")).equals("ShowRealFactoryStats")){
                        System.out.println("GottenShowStatsThing");

                        for (int b = 0; b < playerlist.size(); b++) {
                            String usethis = line.substring(line.indexOf(":")+1);
                            String [] namewhat = usethis.split(",");
                            if(namewhat[0].equals(playerlist.get(b).getName())){
                                for (int i = 0; i < playerlist.get(b).getFactories().size(); i++) {
                                    for (int j = 0; j < playerlist.get(b).getFactories().get(i).getFactorylocations().size(); j++) {
                                        boolean boo = false;
                                        if(Integer.parseInt(namewhat[1])==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getRow()&&Integer.parseInt(namewhat[2])==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                                            boo = true;
                                        }
                                        if(boo){
                                            socketServer.postUpdate("ClearPlayerFactoryList:"+playerlist.get(b).getName());
                                            Factories thisone = playerlist.get(b).getFactories().get(i);

                                            socketServer.postUpdate("Level:"+playerlist.get(b).getName()+","+thisone.getLevel());
                                            socketServer.postUpdate("MoneyMultiplier:"+playerlist.get(b).getName()+","+thisone.getMoneymultiplier());
                                            socketServer.postUpdate("Health:"+playerlist.get(b).getName()+","+thisone.getHealth());
                                            socketServer.postUpdate("ResourceAmount:"+playerlist.get(b).getName()+","+thisone.getResourceamt());
                                            socketServer.postUpdate("Upgrade Factory:"+playerlist.get(b).getName());
                                            socketServer.postUpdate("Exit:"+playerlist.get(b).getName());

                                        }
                                    }
                                }
                            }
                        }
                    }

                    if(line.substring(0,line.indexOf(":")).equals("UpgradeFactory")){
//                        String usethis = line.substring(line.indexOf(":")+1);
//                        String [] namewhat = usethis.split(",");

                        for (int b = 0; b < playerlist.size(); b++) {
                            String usethis = line.substring(line.indexOf(":")+1);
                            String [] namewhat = usethis.split(",");
                            if(namewhat[0].equals(playerlist.get(b).getName())){
                                for (int i = 0; i < playerlist.get(b).getFactories().size(); i++) {
                                    for (int j = 0; j < playerlist.get(b).getFactories().get(i).getFactorylocations().size(); j++) {
                                        boolean boo = false;
                                        if(Integer.parseInt(namewhat[1])==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getRow()&&Integer.parseInt(namewhat[2])==playerlist.get(b).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                                            boo = true;
                                        }
                                        if(boo){
                                            socketServer.postUpdate("ClearPlayerFactoryList:"+playerlist.get(b).getName());
                                            Factories thisone = playerlist.get(b).getFactories().get(i);
                                            thisone.setMoneymultiplier(thisone.getMoneymultiplier() + 1);
                                            thisone.increaselevel();
                                            thisone.setHealth(thisone.getHealth()*thisone.getLevel());
                                            socketServer.postUpdate("Level:"+playerlist.get(b).getName()+","+thisone.getLevel());
                                            socketServer.postUpdate("MoneyMultiplier:"+playerlist.get(b).getName()+","+thisone.getMoneymultiplier());
                                            socketServer.postUpdate("Health:"+playerlist.get(b).getName()+","+thisone.getHealth());
                                            socketServer.postUpdate("ResourceAmount:"+playerlist.get(b).getName()+","+thisone.getResourceamt());
                                            socketServer.postUpdate("Upgrade Factory:"+playerlist.get(b).getName());
                                            socketServer.postUpdate("Exit:"+playerlist.get(b).getName());

                                        }
                                    }
                                }
                            }
                        }




                    }




                    if(line.substring(0,line.indexOf(":")).equals("BuyFactory")){
                        String usethis = line.substring(line.indexOf(":")+1);
                        String [] namewhat = usethis.split(",");
                        for (int i = 0; i < playerlist.size(); i++) {
                            if(namewhat[0].equals(playerlist.get(i).getName())){
                                Location givenlocation = new Location(Integer.parseInt(namewhat[1]),Integer.parseInt(namewhat[2]));
                                playerlist.get(i).getFactories().add(new Factories(playerlist.get(i),givenlocation));
                                for (int j = 0; j < playerlist.get(i).getFactories().get(playerlist.get(i).getFactories().size()-1).getFactorylocations().size(); j++) {
                                    socketServer.postUpdate("NewFactory:"+playerlist.get(i).getFactories().get(playerlist.get(i).getFactories().size()-1).getFactorylocations().get(j).getRow()+","+playerlist.get(i).getFactories().get(playerlist.get(i).getFactories().size()-1).getFactorylocations().get(j).getColumn());
                                }
                            }
                        }
                    }

                }



            }else {
                System.out.println("I broke it, skull emoji");
            }
        }

        @FXML
        public void clientboughtweapon(String[] namewhat){
            String personname = namewhat[0];
            String personweapon = namewhat[1];
            for (int i = 0; i < playerlist.size(); i++) {
                if(personname.equals(playerlist.get(i).getName())){
                    for (int j = 0; j < allweaponsinshop.size(); j++) {
                        if(personweapon.equals(allweaponsinshop.get(j).getWeaponname())){
                            playerlist.get(i).getWeaponsininventory().add(allweaponsinshop.get(j));
                            //socketServer.postUpdate("PlayerInventory:"+personname+","+allweaponsinshop.get(j).getWeaponname()+": Damage: "+allweaponsinshop.get(j).getWapondamage()+". Range: "+allweaponsinshop.get(j).getWeaponrange()+". Spread: "+allweaponsinshop.get(j).getWeaponspread()+". Cost: "+allweaponsinshop.get(j).getWeaponcost());
                        }
                    }
                }
            }
            sendoutplayerinventories();



        }


        @FXML
        public void clientboughtitem(String[] namewhat){
            Items objbought = null;


            for (int i = 0; i < allitemsinshop.size(); i++) {
                System.out.println(namewhat[1]+" Being Compared To "+allitemsinshop.get(i).getItemname() );
                if(namewhat[1].equals(allitemsinshop.get(i).getItemname())){
                    objbought = allitemsinshop.get(i);
                    String itemname = allitemsinshop.get(i).getItemname();
                    if(itemname.equals("Grenade")){

                        numgrenad--;
                    }else if (itemname.equals("Invisibility")) {

                        numinvis--;
                    }else if(itemname.equals("Shrink")) {

                        numshrink--;
                    }
                    for (int m = 0; m < playerlist.size(); m++) {
                        if(namewhat[0].equals(playerlist.get(m).getName())){
                            playerlist.get(m).getItemsinventory().add(objbought);
                            allitemsinshop.remove(objbought);
//                    for (int j = 0; j < playerlist.get(i).getItemsinventory().size(); j++) {
//                        socketServer.postUpdate("PlayerInventory:"+namewhat[0]+","+playerlist.get(i).getItemsinventory().get(j).getItemname()+": Cost: "+playerlist.get(i).getItemsinventory().get(j).getItemCost()+". Damage: "+playerlist.get(i).getItemsinventory().get(j).getItemDamage());
//                    }
                        }
                    }
                    //allitemsinshop.remove(allitemsinshop.get(i));

                    break;
                }
            }



            sendoutshopitems();
            itemlist.getItems().clear();
            itemlist.getItems().add("Grenade: Cost: 100, Damage: 100, Amount Left: "+numgrenad);
            //itemlist.getItems().add("Heal: Cost: 50, Damage: 0, Heal 75, Amount Left: "+numheal);
            itemlist.getItems().add("Invisibility: Cost: 150, Damage: 0, Amount Left: "+numinvis);
            itemlist.getItems().add("Shrink: Cost: 150, Damage: 0, Amount Left: "+numshrink);
            sendoutplayerinventories();


        }






        @Override
        public void onClosedStatus(boolean isClosed) {
            if (socketServer.isServerSocketClosed()) {
                displayState(ConnectionDisplayState.DISCONNECTED);
            } else {
                displayState(ConnectionDisplayState.CONNECTED);
            }
        }
    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        displayState(ConnectionDisplayState.DISCONNECTED);
//        sentMsgsData = FXCollections.observableArrayList();
//        sentMsgsListView.setItems(sentMsgsData);
//        sentMsgsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        sentMsgsListView.setOnMouseClicked((Event event) -> {
//            String selectedItem
//                    = sentMsgsListView.getSelectionModel().getSelectedItem();
//            if (selectedItem != null && !selectedItem.equals("null")) {
//                //selectedTextField.setText("Sent: " + selectedItem);
//                lastSelectedListView = sentMsgsListView;
//            }
//        });
//
//        rcvdMsgsData = FXCollections.observableArrayList();
//        rcvdMsgsListView.setItems(rcvdMsgsData);
//        rcvdMsgsListView.getSelectionModel().setSelectionMode(SelectionMode.SINGLE);
//        rcvdMsgsListView.setOnMouseClicked((Event event) -> {
//            String selectedItem
//                    = rcvdMsgsListView.getSelectionModel().getSelectedItem();
//            if (selectedItem != null && !selectedItem.equals("null")) {
//                //selectedTextField.setText("Received: " + selectedItem);
//                lastSelectedListView = rcvdMsgsListView;
//            }
//        });

        portTooltip = new Tooltip("Port number cannot be modified once\n" +
        "the first connection attempt is initiated.\n" +
        "Restart application in order to change.");

        portTextField.textProperty().addListener((obs, oldText, newText) -> {
            try {
                Integer.parseInt(newText);
            } catch (NumberFormatException e) {
                portTextField.setText(oldText);
            }
        });

    }


    @FXML
    private void handleConnectButton(ActionEvent event) {
        displayState(ConnectionDisplayState.WAITING);
        portTextField.setEditable(false);
        portTextField.setTooltip(portTooltip);
        socketServer = new FxMultipleSocketServer(new FxSocketListener(),
                Integer.valueOf(portTextField.getText()));
        new Thread(socketServer).start();
    }

    @FXML
    public void readybutton(){
        numberofpplready+=1;
        System.out.println("PEOPLE READY\uD83D\uDC80: "+numberofpplready);
        System.out.println("PEOPLE CONNECTED: "+numberofpplconnnected);
        checkifallready();
    }
    @FXML
    public void checkifallready(){
        if(numberofpplready==numberofpplconnnected+1){
            startbtn.setVisible(true);
            readylbl.setText("All Ready");
            for (int i = 0; i < playerlist.size(); i++) {
                System.out.println(playerlist.get(i).getName());
            }
        }
    }


    @FXML
    public void showplayerstats(){
        for (int i = 0; i < playerlist.size()-1; i++) {
            socketServer.postUpdate("Stats:"+playerlist.get(i).getName()+","+playerlist.get(i).getHealth()+","+playerlist.get(i).getMoneypts());
        }
        playerstatslist.getItems().clear();
        playerstatslist.getItems().add("Name:"+playerlist.get(playerlist.size()-1).getName());
        //playerstatslist.getItems().add("Money:"+playerlist.get(playerlist.size()-1).getMoneypts());
        playerstatslist.getItems().add("Health:"+playerlist.get(playerlist.size()-1).getHealth());
    }


    @FXML
    public ArrayList<Bullets> allbullets = new ArrayList<>();


    @FXML
    public void selectheinventoryitem(){
        selectedinventoryitemthingy = playerinventorylist.getSelectionModel().getSelectedItem().toString();
    }

    @FXML
    public void restart(ActionEvent x){
        playerlist.clear();
        allbullets.clear();
        alldrops.clear();
        allweaponsinshop.clear();
        allitemsinshop.clear();
        playerinventorylist.getItems().clear();
        playerstatslist.getItems().clear();
        socketServer.postUpdate("Restart");
        startgame(x);
    }

    @FXML
    public void startgame(ActionEvent event){
        System.out.println("Game can start");
        factoryupglist.getItems().clear();
        String name = txtnamebox.getText();
        socketServer.postUpdate("CREATEBOARD");
        gPane.setGridLinesVisible(true);
        gPane.setVisible(true);
        for (int i = 0; i < buttongrid.length; i++) {
            for (int j = 0; j < buttongrid[0].length; j++) {
                buttongrid[i][j] = new Button();
                buttongrid[i][j].setPrefSize(15,15);
                buttongrid[i][j].setMinSize(15,15);
                buttongrid[i][j].setStyle("-fx-background-color: #c2d9ff; ");
                //buttongrid[i][j].setPrefSize(50, 25);
                gPane.add(buttongrid[i][j], j, i);
            }
        }
        //do stuff to make ur thingy here
        for (int i = 0; i < environmentgrid.length; i++) {
            for (int j = 0; j < environmentgrid[0].length; j++) {
                environmentgrid[i][j]=0;
            }
        }
        createrectangles();
        for (int i = 0; i < namesofppl.size(); i++) {
            int row = (int)(Math.random() * 48)+1;
            int column = (int)(Math.random() * 78);
            while(environmentgrid[row][column]==2||environmentgrid[row-1][column]==2||environmentgrid[row][column+1]==2||environmentgrid[row-1][column+1]==2){
                row = (int)(Math.random() * 48)+1;
                column = (int)(Math.random() * 78);
            }
            playerlist.add(new Player(namesofppl.get(i), 100, new Location(row, column)));
        }


        int row = (int)(Math.random() * 48)+1;
        int column = (int)(Math.random() * 78);
        while(environmentgrid[row][column]==2||environmentgrid[row-1][column]==2||environmentgrid[row][column+1]==2||environmentgrid[row-1][column+1]==2){
            row = (int)(Math.random() * 48)+1;
            column = (int)(Math.random() * 78);
        }
//        for (int i = 0; i < playerlist.size(); i++) {
//            //do while loop for player list to see if player is in environment location.
//        }

        playerlist.add(new Player(name, 100, new Location(row, column)));
        showplayerstats();



        showpeopleonscreen();
        makeitems();
        makeweapons();
        factoryupglist.getItems().add("Factory");
        socketServer.postUpdate("StartFactory");





        EventHandler z = new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent event) {
                chosenrow = GridPane.getRowIndex(((Button) event.getSource()));
                chosencol = GridPane.getColumnIndex(((Button) event.getSource()));


                System.out.println(chosenrow);
                System.out.println(chosencol);

                if(factoryupglist.getSelectionModel().getSelectedItem()==null){//"Sniper,50,200,0,500", "Shotgun,40,20,3,100", "AR,30,80,0,250", "SMG,25,50,0,50"

                    if(selectedinventoryitemthingy.contains("Shotgun")){
                        System.out.println("ShotgunShot");
                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow()-2,playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()+2), playerlist.get(playerlist.size()-1),new Location(chosenrow+2, chosencol-2)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgoals = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoals);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow()+3,playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()-2), playerlist.get(playerlist.size()-1),new Location(chosenrow-2, chosencol+3)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgoaaal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoaaal);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow()-4,playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()+2), playerlist.get(playerlist.size()-1),new Location(chosenrow-4, chosencol-3)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesaaandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesaaandgoal);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow()+3,playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()+2), playerlist.get(playerlist.size()-1),new Location(chosenrow+3, chosencol+4)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgwwoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgwwoal);
                    }
                    if(selectedinventoryitemthingy.contains("AR")){
                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                        allbullets.add(new Bullets(new Location(((chosenrow - playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow())/3)  +playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow()   ,   ((chosencol - playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn())/3)+ playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()  ), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesanssdgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesanssdgoal);


                        allbullets.add(new Bullets(new Location(((chosenrow - playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow())/3)*2  +playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow()   ,   ((chosencol - playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn())/3)*2+ playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()  ), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinssatesanssdgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinssatesanssdgoal);
                    }
                    if(selectedinventoryitemthingy.contains("SMG")){
                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(1).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(1).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bullssetcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bullssetcoordinatesandgoal);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(2).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(2).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String ssasfgee = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+ssasfgee);

                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(3).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(3).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String weeerrr = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+weeerrr);


                    }
                    if(selectedinventoryitemthingy.contains("Sniper")){
                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);
                    }
                    if(selectedinventoryitemthingy.contains("Grenade")){
                        allbullets.add(new Bullets(new Location(playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getRow(),playerlist.get(playerlist.size()-1).getPlayerlocation().get(0).getColumn()), playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
                        allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                        allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                        allbullets.get(allbullets.size()-1).setgrenade();
                        String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                        socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);
                        for (int i = 0; i < playerlist.get(playerlist.size()-1).getItemsinventory().size(); i++) {
                            if(playerlist.get(playerlist.size()-1).getItemsinventory().get(i).getItemname().equals("Grenade")){
                                playerlist.get(playerlist.size()-1).getItemsinventory().remove(i);
                                selectedinventoryitemthingy = "";
                                break;
                            }
                        }
                    }
                    if(selectedinventoryitemthingy.contains("Shrink")){
                        playerlist.get(playerlist.size()-1).setshrunked(System.nanoTime());
                        for (int i = 0; i < playerlist.get(playerlist.size()-1).getItemsinventory().size(); i++) {
                            if(playerlist.get(playerlist.size()-1).getItemsinventory().get(i).getItemname().equals("Shrink")){
                                playerlist.get(playerlist.size()-1).getItemsinventory().remove(i);
                                selectedinventoryitemthingy="";
                                break;
                            }
                        }
                    }
                    if(selectedinventoryitemthingy.contains("Invisibility")){
                        playerlist.get(playerlist.size()-1).setInvisible(System.nanoTime());
                        for (int i = 0; i < playerlist.get(playerlist.size()-1).getItemsinventory().size(); i++) {
                            if(playerlist.get(playerlist.size()-1).getItemsinventory().get(i).getItemname().equals("Invisibility")){
                                playerlist.get(playerlist.size()-1).getItemsinventory().remove(i);
                                selectedinventoryitemthingy="";
                                break;
                            }
                        }
                    }


                }

                for (int i = 0; i < playerlist.get(playerlist.size()-1).getFactories().size(); i++) {
                    for (int j = 0; j < playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().size(); j++) {
                        boolean boo = false;
                        if(chosenrow==playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().get(j).getRow()&&chosencol==playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                            boo = true;
                        }
                        if(boo){
                            factoryupglist.getItems().add("ShowFactoryStats");
                        }
                    }
                }


                if(factoryupglist.getSelectionModel().getSelectedItem() != null){
                    if(factoryupglist.getSelectionModel().getSelectedItem().toString().equals("Exit")){
                        factoryupglist.getItems().clear();
                        factoryupglist.getItems().add("Factory");
                    }


                    if(factoryupglist.getSelectionModel().getSelectedItem() != null){
                        if(factoryupglist.getSelectionModel().getSelectedItem().toString().equals("ShowFactoryStats")){
                            for (int i = 0; i < playerlist.get(playerlist.size()-1).getFactories().size(); i++) {
                                for (int j = 0; j < playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().size(); j++) {
                                    boolean boo = false;
                                    if(chosenrow==playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().get(j).getRow()&&chosencol==playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                                        boo = true;
                                    }
                                    if(boo){
                                        factoryupglist.getItems().clear();
                                        Factories usethis = playerlist.get(playerlist.size()-1).getFactories().get(i);
                                        factoryupglist.getItems().add("Level: "+usethis.getLevel());
                                        factoryupglist.getItems().add("MoneyMultiplier: "+usethis.getMoneymultiplier());
                                        factoryupglist.getItems().add("Health: "+usethis.getHealth());
                                        factoryupglist.getItems().add("ResourceAmount: "+usethis.getResourceamt());
                                        factoryupglist.getItems().add("Upgrade Factory");
                                        factoryupglist.getItems().add("Exit");
                                    }
                                }
                            }
                        }
                    }

                    if(factoryupglist.getSelectionModel().getSelectedItem() != null){
                        if(factoryupglist.getSelectionModel().getSelectedItem().toString().equals("Upgrade Factory")){
                            for (int i = 0; i < playerlist.get(playerlist.size()-1).getFactories().size(); i++) {
                                for (int j = 0; j < playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().size(); j++) {
                                    boolean boo = false;
                                    if(chosenrow==playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().get(j).getRow()&&chosencol==playerlist.get(playerlist.size()-1).getFactories().get(i).getFactorylocations().get(j).getColumn()){
                                        boo = true;
                                    }
                                    if(boo){
                                        factoryupglist.getItems().clear();
                                        Factories usethis = playerlist.get(playerlist.size()-1).getFactories().get(i);
                                        usethis.setMoneymultiplier(usethis.getMoneymultiplier() + 1);
                                        usethis.increaselevel();
                                        usethis.setHealth(usethis.getHealth()*usethis.getLevel());
                                        factoryupglist.getItems().add("Level: "+usethis.getLevel());
                                        factoryupglist.getItems().add("MoneyMultiplier: "+usethis.getMoneymultiplier());
                                        factoryupglist.getItems().add("Health: "+usethis.getHealth());
                                        factoryupglist.getItems().add("ResourceAmount: "+usethis.getResourceamt());
                                        factoryupglist.getItems().add("Upgrade Factory");
                                        factoryupglist.getItems().add("Exit");
                                    }
                                }
                            }
                        }
                    }


                }




                ((Button) event.getSource()).setStyle("-fx-background-color: #000000;");

            }
        };

        for (int i = 0; i < buttongrid.length; i++) {
            for (int j = 0; j < buttongrid[0].length; j++) {
                buttongrid[i][j].setOnAction(z);
            }

        }

        for (int i = 0; i < playerlist.size(); i++) {
            System.out.println(playerlist.get(i).getName());
        }
        starttime = System.nanoTime();
        timed = System.nanoTime();
        fivesecondrespawn = System.nanoTime();
        droprespawn = System.nanoTime();
        start();


    }

    @FXML
    public void movebullets(){

        String allbulletcoordinates = "";

        for (int i = 0; i < allbullets.size(); i++) {
            buttongrid[allbullets.get(i).getBulletLocation().getRow()][allbullets.get(i).getBulletLocation().getColumn()].setStyle("-fx-background-color: #c2d9ff; ");
        }

        for (int i = 0; i < allbullets.size(); i++) {

            if(i<0 && allbullets.size()<1){
                System.out.println("no bullets to move");
            }
            if(i <0 && allbullets.size()>0){
                i=0;
            }
            if(allbullets.get(i).getBounces()>6){
                if(allbullets.get(i).isGrenade()){
                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow()+2, allbullets.get(i).getBulletLocation().getColumn())));
                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                    String bulletcoordinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                    socketServer.postUpdate("BulletToAdd:"+bulletcoordinatesandgoal);

                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow()-2, allbullets.get(i).getBulletLocation().getColumn())));
                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                    String asfff = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                    socketServer.postUpdate("BulletToAdd:"+asfff);

                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow(), allbullets.get(i).getBulletLocation().getColumn()-2)));
                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                    String asfasf = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                    socketServer.postUpdate("BulletToAdd:"+asfasf);

                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow(), allbullets.get(i).getBulletLocation().getColumn()+2)));
                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
                    String bulletcoorwwwdinatesandgoal = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
                    socketServer.postUpdate("BulletToAdd:"+bulletcoorwwwdinatesandgoal);

//                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow()+1, allbullets.get(i).getBulletLocation().getColumn()+1)));
//                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
//                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
//                    String rwrwrwr = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
//                    socketServer.postUpdate("BulletToAdd:"+rwrwrwr);
//
//                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow()-1, allbullets.get(i).getBulletLocation().getColumn()+1)));
//                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
//                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
//                    String xxxxaaaa = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
//                    socketServer.postUpdate("BulletToAdd:"+xxxxaaaa);
//
//                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow()+1, allbullets.get(i).getBulletLocation().getColumn()-1)));
//                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
//                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
//                    String tetett = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
//                    socketServer.postUpdate("BulletToAdd:"+tetett);
//
//                    allbullets.add(new Bullets(new Location(allbullets.get(i).getBulletLocation().getRow(),allbullets.get(i).getBulletLocation().getColumn()), allbullets.get(i).getOwnerplayer(),new Location(allbullets.get(i).getBulletLocation().getRow()-1, allbullets.get(i).getBulletLocation().getColumn()-1)));
//                    allbullets.get(allbullets.size()-1).setthegrid(environmentgrid);
//                    allbullets.get(allbullets.size()-1).setPortallocations(allportals);
//                    String teafsaf = allbullets.get(allbullets.size()-1).getBulletLocation().getRow() + ","+allbullets.get(allbullets.size()-1).getBulletLocation().getColumn() + ","+allbullets.get(allbullets.size()-1).getGoal().getRow()+","+allbullets.get(allbullets.size()-1).getGoal().getColumn();
//                    socketServer.postUpdate("BulletToAdd:"+teafsaf); 

                    socketServer.postUpdate("RemoveBullet:"+i);

                }
                allbullets.remove(i);
                i--;
            }
            if(i>=0){
                allbullets.get(i).movebullet();
            }

        }

        for (int i = 0; i < allbullets.size(); i++) {
            buttongrid[allbullets.get(i).getBulletLocation().getRow()][allbullets.get(i).getBulletLocation().getColumn()].setStyle("-fx-background-color: #0a0000; ");
            allbulletcoordinates+=allbullets.get(i).getBulletLocation().getRow()+","+allbullets.get(i).getBulletLocation().getColumn()+",";
        }

        for (int i = 0; i < environmentgrid.length; i++) {
            for (int j = 0; j < environmentgrid[0].length; j++) {
                if(environmentgrid[i][j]==2){
                    buttongrid[i][j].setStyle("-fx-background-color:#05f525; ");
                }
            }
        }

        for (int i = 0; i < playerlist.size(); i++) {

            if(!playerlist.get(i).isIsdead()){
                for (int j = 0; j < playerlist.get(i).getPlayerlocation().size(); j++) {
                    int r = playerlist.get(i).getPlayerlocation().get(j).getRow();
                    int c = playerlist.get(i).getPlayerlocation().get(j).getColumn();
                    buttongrid[r][c].setStyle("-fx-background-color:#fc1303; ");
                }
            }

        }

        for (int i = 0; i < playerlist.size(); i++) {
            for (int j = 0; j < playerlist.get(i).getFactories().size(); j++) {
                for (int k = 0; k < playerlist.get(i).getFactories().get(j).getFactorylocations().size(); k++) {
                    buttongrid[playerlist.get(i).getFactories().get(j).getFactorylocations().get(k).getRow()][playerlist.get(i).getFactories().get(j).getFactorylocations().get(k).getColumn()].setStyle("-fx-background-color:#e811f0; ");
                }
            }
        }
        //socketServer.postUpdate("BulletShow:"+allbulletcoordinates);


    }



    @FXML
    public void addmoneytoallplayers(){
        for (int i = 0; i < playerlist.size(); i++) {
            for (int j = 0; j < playerlist.get(i).getFactories().size(); j++) {
                System.out.println(playerlist.get(i).getName()+ " has "+ playerlist.get(i).getFactories().size()+" Factories");
                playerlist.get(i).addmoney(playerlist.get(i).getFactories().get(j).thismoneygivenbyfactory());
            }
        }
    }


    @FXML
    public void start(){
        new AnimationTimer(){
            @Override
            public void handle(long now){
                int time = (int) (now/1000000000 - starttime/1000000000);
                
                if(now-starttime > (900000000L * 500)){
                    for (int i = 0; i < buttongrid.length; i++) {
                        for (int j = 0; j < buttongrid[0].length; j++) {
                            buttongrid[i][j].setStyle("-fx-background-color:#FFFFFF; ");
                        }
                    }
                    btnrestart.setVisible(true);
                    socketServer.postUpdate("ShowRestart");

                }

                if(now - fivesecondrespawn > (900000000L * 5)){

                    addmoneytoallplayers();
                    for (int i = 0; i < playerlist.get(playerlist.size()-1).getItemsinventory().size(); i++) {
                        Player playeruised = playerlist.get(playerlist.size()-1);
                        playerinventorylist.getItems().add(playeruised.getItemsinventory().get(i).getItemname() + ": Damage: "+playeruised.getItemsinventory().get(i).getItemDamage());
                    }
                    for (int i = 0; i < playerlist.get(playerlist.size()-1).getWeaponsininventory().size(); i++) {
                        Player userthis = playerlist.get(playerlist.size()-1);
                        int dmg = userthis.getWeaponsininventory().get(i).getWapondamage();
                        String name = userthis.getWeaponsininventory().get(i).getWeaponname();
                        int rng = userthis.getWeaponsininventory().get(i).getWeaponrange();
                        int sprd = userthis.getWeaponsininventory().get(i).getWeaponspread();
                        int cost = userthis.getWeaponsininventory().get(i).getWeaponcost();
                        playerinventorylist.getItems().add(name+": Damage= "+dmg+", Range= "+rng+", Spread= "+sprd+", Cost= "+cost);
                    }
                    playerinventorylist.getItems().add("Money: "+playerlist.get(playerlist.size()-1).getMoneypts());
                    sendoutplayerinventories();
                    fivesecondrespawn = System.nanoTime();
                }

                if((now-droprespawn) > (900000000L * 150)){
                    int howmanydrops = (int)(Math.random()*20)+8;
                    for (int i = 0; i < howmanydrops; i++) {
                        int row = (int)(Math.random() * 45)+2;
                        int col = (int)(Math.random() * 76) + 2;
                        while(environmentgrid[row][col]!=0  || environmentgrid[row-1][col]!=0 || environmentgrid[row][col+1]!=0 || environmentgrid[row-1][col+1]!=0){
                            row =(int)(Math.random() * 45)+2;
                            col = (int)(Math.random() * 76) + 2;
                        }
                        int itemorresource = (int)(Math.random() * 2);
                        if(itemorresource == 0){
                            //itemused
                            int randomindex = (int)(Math.random()*3);
                            int cost = 0;
                            int damage = 0;
                            if(itemnames[randomindex].equals("Grenade")){
                                cost = 100;
                                damage = 100;
                                //numgrenad++;
                            }else if (itemnames[randomindex].equals("Invisibility")) {
                                cost = 150;
                                numinvis++;
                            }else {
                                cost = 150;
                                //numshrink++;
                            }
                            int randomcolorindex = (int)(Math.random()*portalcolors.length);

                            alldrops.add(new Drop(new Items(itemnames[randomindex], damage, cost), portalcolors[randomcolorindex], new Location(row, col)));
                            socketServer.postUpdate("NewDrop:"+portalcolors[randomcolorindex]+","+row+","+col);

                        }else{
                            String resourcecolor = portalcolors[(int)(Math.random()*portalcolors.length)];
                            int amtgiven = (int)(Math.random()*150)+25;
                            alldrops.add(new Drop(amtgiven, resourcecolor, new Location(row,col)));
                            socketServer.postUpdate("NewDrop:"+resourcecolor+","+row+","+col);
                        }
                        droprespawn = System.nanoTime();
                    }
                    showpeopleonscreen();


                }

                if(now-timed>10000000){
                    lblsecs.setText("Seconds: "+time);




                    if(allbullets.size()>0){

                        movebullets();

                        for (int i = 0; i < allbullets.size(); i++) {
                            for (int j = 0; j < playerlist.size(); j++) {
                                for (int k = 0; k < playerlist.get(j).getPlayerlocation().size(); k++) {
                                    if(allbullets.get(i).getBulletLocation().getRow()==playerlist.get(j).getPlayerlocation().get(k).getRow()&&allbullets.get(i).getBulletLocation().getColumn()==playerlist.get(j).getPlayerlocation().get(k).getColumn()){
                                        if((!allbullets.get(i).getOwnerplayer().getName().equals(playerlist.get(j).getName()))&& (!playerlist.get(j).isIsdead())){
                                            playerlist.get(j).setIsdead(true);
                                            buttongrid[playerlist.get(j).getPlayerlocation().get(0).getRow()][playerlist.get(j).getPlayerlocation().get(0).getColumn()].setStyle("-fx-background-color:#c2d9ff; ");
                                            buttongrid[playerlist.get(j).getPlayerlocation().get(1).getRow()][playerlist.get(j).getPlayerlocation().get(1).getColumn()].setStyle("-fx-background-color:#c2d9ff; ");
                                            buttongrid[playerlist.get(j).getPlayerlocation().get(2).getRow()][playerlist.get(j).getPlayerlocation().get(2).getColumn()].setStyle("-fx-background-color:#c2d9ff; ");
                                            buttongrid[playerlist.get(j).getPlayerlocation().get(3).getRow()][playerlist.get(j).getPlayerlocation().get(3).getColumn()].setStyle("-fx-background-color:#c2d9ff; ");

                                            playerlist.get(j).setPlayerdeathtime(now);
                                            allbullets.get(i).setBounces(7);
                                            socketServer.postUpdate("RemoveBullet:"+i);
                                        }
                                        showpeopleonscreen();
                                    }
                                }

                                for (int k = 0; k < playerlist.get(j).getFactories().size(); k++) {
                                    for (int l = 0; l < playerlist.get(j).getFactories().get(k).getFactorylocations().size(); l++) {
                                        int factoryrow = playerlist.get(j).getFactories().get(k).getFactorylocations().get(l).getRow();
                                        int factorycol = playerlist.get(j).getFactories().get(k).getFactorylocations().get(l).getColumn();
                                        if(allbullets.get(i).getBulletLocation().getRow()==factoryrow&&allbullets.get(i).getBulletLocation().getColumn()==factorycol){

                                            if(allbullets.get(i).getOwnerplayer().getName()!=playerlist.get(j).getName()){
                                                for (int n = 0; n < playerlist.get(j).getFactories().get(k).getFactorylocations().size(); n++) {
                                                    environmentgrid[playerlist.get(j).getFactories().get(k).getFactorylocations().get(n).getRow()][playerlist.get(j).getFactories().get(k).getFactorylocations().get(n).getColumn()] = 0;
                                                    socketServer.postUpdate("SetEnvironmentToThis:"+playerlist.get(j).getFactories().get(k).getFactorylocations().get(n).getRow()+","+playerlist.get(j).getFactories().get(k).getFactorylocations().get(n).getColumn()+","+0);
                                                }
                                                playerlist.get(j).getFactories().remove(k);
                                                allbullets.get(i).setBounces(7);
                                                socketServer.postUpdate("RemoveBullet:"+i);
                                            }


                                        }
                                    }
                                }
                            }
                        }


                    }

                    //showpeopleonscreen();
                    timed = System.nanoTime();

                }

                for (int i = 0; i < playerlist.size(); i++) {
                    if(playerlist.get(i).isIsdead()){
                        if ((int)(now/1000000000 - playerlist.get(i).getPlayerdeathtime()/1000000000) > 4) {
                            playerlist.get(i).setIsdead(false);
                            showpeopleonscreen();
                        }
                    }
                }
                for (int i = 0; i < playerlist.size(); i++) {
                    if(playerlist.get(i).isInvisible()){
                        if ((int)(now/1000000000 - playerlist.get(i).getInvistime()/1000000000) > 10) {
                            playerlist.get(i).invisibilitygone(false);
                            showpeopleonscreen();
                        }
                    }
                }

                for (int i = 0; i < playerlist.size(); i++) {
                    if(playerlist.get(i).isIsshrunk()){
                        if ((int)(now/1000000000 - playerlist.get(i).getShrinktime()/1000000000) > 10) {
                            playerlist.get(i).shrinkgoner(false);
                            showpeopleonscreen();
                        }
                    }
                }





            }
        }.start();
    }

    @FXML
    public void move(KeyEvent o){

        //up down left and right only, no diagonal, can add later

        ArrayList<Location> playerlocs = playerlist.get(playerlist.size()-1).getPlayerlocation();

        if(o.getCode().equals(KeyCode.W)){
            if(environmentgrid[playerlocs.get(1).getRow()-1][playerlocs.get(1).getColumn()]==2||environmentgrid[playerlocs.get(3).getRow()-1][playerlocs.get(3).getColumn()]==2){
                System.out.println("Cannot Go");
            }else{
                playerlist.get(playerlist.size()-1).changeplayerloc("up");
            }

        } else if (o.getCode().equals(KeyCode.A)) {

            if(environmentgrid[playerlocs.get(0).getRow()][playerlocs.get(1).getColumn()-1]==2||environmentgrid[playerlocs.get(1).getRow()][playerlocs.get(1).getColumn()-1]==2){
                System.out.println("Cannot Go");
            }else{
                playerlist.get(playerlist.size()-1).changeplayerloc("left");
            }


        } else if (o.getCode().equals(KeyCode.S)) {

            if(environmentgrid[playerlocs.get(0).getRow()+1][playerlocs.get(0).getColumn()]==2||environmentgrid[playerlocs.get(2).getRow()+1][playerlocs.get(2).getColumn()]==2){
                System.out.println("Cannot Go");
            }else{
                playerlist.get(playerlist.size()-1).changeplayerloc("down");
            }



        }else if (o.getCode().equals(KeyCode.D)){

            if(environmentgrid[playerlocs.get(2).getRow()][playerlocs.get(2).getColumn()+1]==2||environmentgrid[playerlocs.get(3).getRow()][playerlocs.get(3).getColumn()+1]==2){
                System.out.println("Cannot Go");
            }else{
                playerlist.get(playerlist.size()-1).changeplayerloc("right");
            }

        }


        checkifondrop();
        checkifonfactory();

        for (int i = 0; i < playerlist.get(playerlist.size()-1).getPlayerlocation().size(); i++) {
            for (int a = 0; a < playerlist.get(playerlist.size()-1).getFactories().size(); a++) {
                for (int b = 0; b < playerlist.get(playerlist.size()-1).getFactories().get(a).getFactorylocations().size(); b++) {
                    boolean boo = false;
                    if(playerlist.get(playerlist.size()-1).getPlayerlocation().get(i).getRow()==playerlist.get(playerlist.size()-1).getFactories().get(a).getFactorylocations().get(b).getRow()&&playerlist.get(playerlist.size()-1).getPlayerlocation().get(i).getColumn()==playerlist.get(playerlist.size()-1).getFactories().get(a).getFactorylocations().get(b).getColumn()){
                        boo = true;
                    }
                    if(boo){
                        factoryupglist.getItems().clear();
                        Factories usethis = playerlist.get(playerlist.size()-1).getFactories().get(a);
                        factoryupglist.getItems().add("Level: "+usethis.getLevel());
                        factoryupglist.getItems().add("MoneyMultiplier: "+usethis.getMoneymultiplier());
                        factoryupglist.getItems().add("Health: "+usethis.getHealth());
                        factoryupglist.getItems().add("ResourceAmount: "+usethis.getResourceamt());
                        factoryupglist.getItems().add("Upgrade Factory");
                        factoryupglist.getItems().add("Exit");
                    }
                }
            }
        }

        socketServer.postUpdate("ShowDrops");


        showpeopleonscreen();


    }


    public void checkifonfactory(){
        for (int i = 0; i < playerlist.size(); i++) {
            for (int j = 0; j < playerlist.get(i).getPlayerlocation().size(); j++) {
                for (int k = 0; k < playerlist.get(i).getFactories().size(); k++) {
                    for (int l = 0; l < playerlist.get(i).getFactories().get(k).getFactorylocations().size(); l++) {
                        if(playerlist.get(i).getPlayerlocation().get(j).getRow()==playerlist.get(i).getFactories().get(k).getFactorylocations().get(l).getRow() && playerlist.get(i).getPlayerlocation().get(j).getColumn()==playerlist.get(i).getFactories().get(k).getFactorylocations().get(l).getColumn()){
                            playerlist.get(i).getFactories().get(k).addresourceamt(playerlist.get(i).getAmtresources());
                            playerlist.get(i).setAmtresources(0);




                        }

                    }
                }
            }
        }
    }


    public void checkifondrop(){

        for (int i = 0; i < alldrops.size(); i++) {
            for (int j = 0; j < playerlist.size(); j++) {
                for (int k = 0; k < playerlist.get(j).getPlayerlocation().size(); k++) {
                    if(alldrops.get(i).getDroplocation().getRow()==playerlist.get(j).getPlayerlocation().get(k).getRow() && alldrops.get(i).getDroplocation().getColumn()==playerlist.get(j).getPlayerlocation().get(k).getColumn()){
                        playerlist.get(j).droppickedup(alldrops.get(i));
                        System.out.println("THE EYE IS "+i);
                        System.out.println("LOCATION: "+alldrops.get(i).getDroplocation().getRow()+"---"+alldrops.get(i).getDroplocation().getColumn());
                        //removes the drop from the thing
                        socketServer.postUpdate("RemoveDrop:"+alldrops.get(i).getDroplocation().getRow()+","+alldrops.get(i).getDroplocation().getColumn());
                        alldrops.remove(i);
                        playerinventorylist.getItems().clear();
                        socketServer.postUpdate("ClearInventory");
                        sendoutplayerinventories();
                        i--;
                    }
                }
            }
        }
    }

    @FXML
    private ArrayList<Items> allitemsinshop = new ArrayList<>();

    @FXML
    private ArrayList<Weapons> allweaponsinshop = new ArrayList<>();

    @FXML
    private String[] itemnames = {"Grenade", "Invisibility", "Shrink"};


    @FXML
    private int numgrenad = 0;
    @FXML
    private int numheal = 0;
    @FXML
    private int numinvis = 0;
    @FXML
    private int numshrink = 0;

    @FXML
    private String[] weaponnames = {"Sniper,50,200,0,500", "Shotgun,40,20,3,100", "AR,30,80,0,250", "SMG,25,50,0,50"};

    @FXML
    public void makeweapons(){
        weaponslist.getItems().clear();
        for (int i = 0; i < weaponnames.length; i++) {
            String[] splitweapon = weaponnames[i].split(",");
            int dmg = Integer.parseInt(splitweapon[1]);
            int rng = Integer.parseInt(splitweapon[2]);
            int sprd = Integer.parseInt(splitweapon[3]);
            int cost = Integer.parseInt(splitweapon[4]);
            allweaponsinshop.add(new Weapons(splitweapon[0],dmg, rng, sprd, cost));
            weaponslist.getItems().add(splitweapon[0]+": Damage= "+dmg+", Range= "+rng+", Spread= "+sprd+", Cost= "+cost);
            socketServer.postUpdate("WeaponList:"+splitweapon[0]+": Damage= "+dmg+", Range= "+rng+", Spread= "+sprd+", Cost= "+cost);
        }

    }


    @FXML
    public void buyfromfactorylist(){
        if(factoryupglist.getSelectionModel().getSelectedItem().toString().equals("Factory")){
            playerlist.get(playerlist.size()-1).getFactories().add(new Factories(playerlist.get(playerlist.size()-1),new Location(chosenrow, chosencol)));
            for (int i = 0; i < playerlist.get(playerlist.size()-1).getFactories().get(playerlist.get(playerlist.size()-1).getFactories().size()-1).getFactorylocations().size(); i++) {
               socketServer.postUpdate("NewFactory:"+playerlist.get(playerlist.size()-1).getFactories().get(playerlist.get(playerlist.size()-1).getFactories().size()-1).getFactorylocations().get(i).getRow()+","+playerlist.get(playerlist.size()-1).getFactories().get(playerlist.get(playerlist.size()-1).getFactories().size()-1).getFactorylocations().get(i).getColumn()) ;
            }
        }

        sendoutplayerinventories();
        showpeopleonscreen();
        factoryupglist.getSelectionModel().clearSelection();
        System.out.println("YES");
    }



    @FXML
    public void createrectangles(){
        //I will commit here

        int numberofrectangles = (int)(Math.random()*17)+7;

        for (int i = 0; i < numberofrectangles; i++) {
            int width = (int)(Math.random()*8)+3;
            int height = (int)(Math.random()*8)+3;


            int StartingXCoordinate = (int)(Math.random()*(environmentgrid.length - height));
            int StartingYCoordinate = (int)(Math.random()*(environmentgrid[0].length - width));


            boolean overlaps = false;
            for (int j = StartingXCoordinate; j < StartingXCoordinate + height; j++) {
                for (int k = StartingYCoordinate; k < StartingYCoordinate + width; k++) {
                    if (environmentgrid[j][k] == 2) {
                        overlaps = true;
                        break;
                    }
                }
                if (overlaps) {
                    break;
                }
            }

            if (!overlaps) {
                for (int j = StartingXCoordinate; j < StartingXCoordinate + height; j++) {
                    for (int k = StartingYCoordinate; k < StartingYCoordinate + width; k++) {
                        environmentgrid[j][k] = 2;
                    }
                }
            }

        }


        //FIX COLUMN OVERLAP

        //int numberofportals = portalcolors.length;
        int idnumber = 0;
        for (int j = 0; j < portalcolors.length; j++) {//50 rows 80 columns


            for (int b = 0; b < 2; b++) {
                int pickhorizontalvertical = (int)(Math.random()*2)+1;
                String type;

                if(pickhorizontalvertical==1){
                    type = "Horizontal";
                }else{
                    type = "Vertical";
                }

                if(type.equals("Horizontal")){
                    int rowontoporbottom = (int)(Math.random()*2)+1;
                    int whatrowtouse;
                    if(rowontoporbottom==1){
                        whatrowtouse = 0;
                    }else{
                        whatrowtouse=49;
                    }
                    int columtostartportal = (int)(Math.random()*70)+5;

                    for (int i = 0; i < allportals.size(); i++) {
                        for (int k = 0; k < allportals.get(i).getCollist().size(); k++) {
                            while(allportals.get(i).getCollist().get(k)==columtostartportal || allportals.get(i).getCollist().get(k)==columtostartportal+2 || environmentgrid[whatrowtouse][columtostartportal]==2 || environmentgrid[whatrowtouse][columtostartportal+2]==2){
                                columtostartportal = (int)(Math.random()*70)+5;
                            }
                        }
                    }


                    ArrayList<Integer> columnthing = new ArrayList<>();
                    for (int k = 0; k < 3; k++) {
                        columnthing.add(columtostartportal+k);
                    }

                    allportals.add(new Portal(type, whatrowtouse, columnthing, idnumber, portalcolors[idnumber], b));
                    socketServer.postUpdate("PortalNew:"+type+","+whatrowtouse+","+columnthing.get(0)+"."+columnthing.get(1)+"."+columnthing.get(2)+","+idnumber+","+portalcolors[idnumber]+","+b);


                } else if (type.equals("Vertical")) {

                    int colrightorleft = (int)(Math.random()*2)+1;
                    int whatcoltouse;
                    if(colrightorleft==1){
                        whatcoltouse = 0;
                    }else{
                        whatcoltouse=79;
                    }
                    int rowtostartportal = (int)(Math.random()*40)+5;

                    for (int i = 0; i < allportals.size(); i++) {//here lies the problem
                        for (int k = 0; k < allportals.get(i).getRowlist().size(); k++) {
                            while(allportals.get(i).getRowlist().get(k)==rowtostartportal  || allportals.get(i).getRowlist().get(k)==rowtostartportal+2 || environmentgrid[rowtostartportal][whatcoltouse]==2 || environmentgrid[rowtostartportal+2][whatcoltouse]==2){
                                rowtostartportal = (int)(Math.random()*40)+5;
                            }
                        }
                    }


                    ArrayList<Integer> rowthinger = new ArrayList<>();
                    for (int k = 0; k < 3; k++) {
                        rowthinger.add(rowtostartportal+k);
                    }


                    allportals.add(new Portal(type, whatcoltouse, rowthinger, idnumber, portalcolors[idnumber], b));
                    socketServer.postUpdate("PortalNew:"+type+","+whatcoltouse+","+rowthinger.get(0)+"."+rowthinger.get(1)+"."+rowthinger.get(2)+","+idnumber+","+portalcolors[idnumber]+","+b);


                }
            }
            idnumber+=1;
        }

        System.out.println("HOW MANY PORTALS: "+allportals.size());

        for (int j = 0; j < allportals.size(); j++) {
            if(allportals.get(j).getTypeofportal().equals("Vertical")){
                for (int k = 0; k < allportals.get(j).getRowlist().size(); k++) {
                    environmentgrid[allportals.get(j).getRowlist().get(k)][allportals.get(j).getColsame()] = 4;
                }
            }else{
                for (int k = 0; k < allportals.get(j).getCollist().size(); k++) {
                    environmentgrid[allportals.get(j).getRowsame()][allportals.get(j).getCollist().get(k)] = 4;
                }
            }
        }





        String bobtoto = "";
        for (int i = 0; i < environmentgrid.length; i++) {
            for (int j = 0; j < environmentgrid[0].length; j++) {
                if(environmentgrid[i][j]==2){
                    String lol = "";
                    lol+=i+",";
                    lol+=j+",";
                    buttongrid[i][j].setStyle("-fx-background-color:#05f525; ");
                    bobtoto+=lol;
                }
            }
        }
        System.out.println("BOB:"+bobtoto);
        socketServer.postUpdate("EnvironmentNew:"+bobtoto);


    }

    @FXML
    public void makeitems(){
        itemlist.getItems().clear();

        for (int i = 0; i < 20; i++) {
            int randomindex = (int)(Math.random()*3);
            int cost = 0;
            int damage = 0;
            if(itemnames[randomindex].equals("Grenade")){
                cost = 100;
                damage = 100;
                numgrenad++;
            }else if (itemnames[randomindex].equals("Invisibility")) {
                cost = 150;
                numinvis++;
            }else {
                cost = 150;
                numshrink++;
            }

            allitemsinshop.add(new Items(itemnames[randomindex], damage, cost));

        }

        itemlist.getItems().add("Grenade: Cost: 100, Damage: 100, Amount Left: "+numgrenad);
        //itemlist.getItems().add("Heal: Cost: 50, Damage: 0, Heal 75, Amount Left: "+numheal);
        itemlist.getItems().add("Invisibility: Cost: 150, Damage: 0, Amount Left: "+numinvis);
        itemlist.getItems().add("Shrink: Cost: 150, Damage: 0, Amount Left: "+numshrink);

        socketServer.postUpdate("ItemList:Grenade: Cost: 100, Damage: 100, Amount Left: "+numgrenad);
        //socketServer.postUpdate("ItemList:Heal: Cost: 50, Damage: 0, Heal 75, Amount Left: "+numheal);
        socketServer.postUpdate("ItemList:Invisibility: Cost: 150, Damage: 0, Amount Left: "+numinvis);
        socketServer.postUpdate("ItemList:Shrink: Cost: 150, Damage: 0, Amount Left: "+numshrink);



    }

    @FXML
    public void sendoutweapons(){
        socketServer.postUpdate("ClearWeaponsList");
        for (int i = 0; i < allweaponsinshop.size(); i++) {
            socketServer.postUpdate("WeaponList:"+allweaponsinshop.get(i).getWeaponname()+ ": Damage= "+allweaponsinshop.get(i).getWapondamage()+", Range= "+allweaponsinshop.get(i).getWeaponrange()+", Spread= "+allweaponsinshop.get(i).getWeaponspread()+", Cost= "+allweaponsinshop.get(i).getWeaponcost());
        }
    }

    @FXML
    public void sendoutshopitems(){
        socketServer.postUpdate("ClearItemList");
        socketServer.postUpdate("ItemList:Grenade: Cost: 100, Damage: 100, Amount Left: "+numgrenad);
        //socketServer.postUpdate("ItemList:Heal: Cost: 50, Damage: 0, Heal 75, Amount Left: "+numheal);
        socketServer.postUpdate("ItemList:Invisibility: Cost: 150, Damage: 0, Amount Left: "+numinvis);
        socketServer.postUpdate("ItemList:Shrink: Cost: 150, Damage: 0, Amount Left: "+numshrink);
    }

    @FXML
    private String selected = "";


    @FXML
    public void itemlistselected(){
        selected = "ITEMSELECTED";
    }
    @FXML
    public void weaponslistselected(){
        selected = "WEAPONSELECTED";
    }
    @FXML
    public void armorlistselected(){
        selected = "ARMORSELECTED";
    }
    @FXML
    public void factorylistselected(){
        selected = "FACTORYSELECTED";
    }

    @FXML
    public void buyitem(){//add a function that checks which listview is selected and then filters on which function to run
        //itemlist.getItems().clear();

        if(selected=="ITEMSELECTED"){
            buytheitems();
        }
        if(selected=="ARMORSELECTED"){
            System.out.println("DO STHUF");
        }
        if(selected=="FACTORYSELECTED"){
            buyfromfactorylist();
        }
        if(selected=="WEAPONSELECTED"){
            buytheweapon();
        }

    }

    //WORK ON THIS AFTER BULLETS THEN U SHOULD BE GOOD
//    @FXML
//    public void buyafactory(){
//
//    }

    @FXML
    public void buytheweapon(){
        playerinventorylist.getItems().clear();
        String weaponchosen  = weaponslist.getSelectionModel().getSelectedItem().toString();
        String weaponname = weaponchosen.substring(0,weaponchosen.indexOf(":"));
        for (int i = 0; i < allweaponsinshop.size(); i++) {
            if(weaponname.equals(allweaponsinshop.get(i).getWeaponname())){
                playerlist.get(playerlist.size()-1).getWeaponsininventory().add(allweaponsinshop.get(i));
            }
        }
        for (int i = 0; i < playerlist.get(playerlist.size()-1).getItemsinventory().size(); i++) {
            Player playeruised = playerlist.get(playerlist.size()-1);
            playerinventorylist.getItems().add(playeruised.getItemsinventory().get(i).getItemname() + ": Damage: "+playeruised.getItemsinventory().get(i).getItemDamage());
        }
        for (int i = 0; i < playerlist.get(playerlist.size()-1).getWeaponsininventory().size(); i++) {
            Player userthis = playerlist.get(playerlist.size()-1);
            int dmg = userthis.getWeaponsininventory().get(i).getWapondamage();
            String name = userthis.getWeaponsininventory().get(i).getWeaponname();
            int rng = userthis.getWeaponsininventory().get(i).getWeaponrange();
            int sprd = userthis.getWeaponsininventory().get(i).getWeaponspread();
            int cost = userthis.getWeaponsininventory().get(i).getWeaponcost();
            playerinventorylist.getItems().add(name+": Damage= "+dmg+", Range= "+rng+", Spread= "+sprd+", Cost= "+cost);
        }

        playerinventorylist.getItems().add("Money: "+playerlist.get(playerlist.size()-1).getMoneypts());


    }


    @FXML
    public void buytheitems(){
        playerinventorylist.getItems().clear();
        String itemchosen  = itemlist.getSelectionModel().getSelectedItem().toString();
        String itemname = itemchosen.substring(0,itemchosen.indexOf(":"));
        for (int i = 0; i < allitemsinshop.size(); i++) {
            if(itemname.equals(allitemsinshop.get(i).getItemname())){
                playerlist.get(playerlist.size()-1).getItemsinventory().add(allitemsinshop.get(i));
                allitemsinshop.remove(i);
                if(itemname.equals("Grenade")){

                    numgrenad--;
                }else if (itemname.equals("Invisibility")) {

                    numinvis--;
                }else {

                    numshrink--;
                }

                break;
            }
        }
        itemlist.getItems().clear();
        itemlist.getItems().add("Grenade: Cost: 100, Damage: 100, Amount Left: "+numgrenad);
        //itemlist.getItems().add("Heal: Cost: 50, Damage: 0, Heal 75, Amount Left: "+numheal);
        itemlist.getItems().add("Invisibility: Cost: 150, Damage: 0, Amount Left: "+numinvis);
        itemlist.getItems().add("Shrink: Cost: 150, Damage: 0, Amount Left: "+numshrink);

        for (int i = 0; i < playerlist.get(playerlist.size()-1).getItemsinventory().size(); i++) {
            Player playeruised = playerlist.get(playerlist.size()-1);
            playerinventorylist.getItems().add(playeruised.getItemsinventory().get(i).getItemname() + ": Damage: "+playeruised.getItemsinventory().get(i).getItemDamage());
        }
        for (int i = 0; i < playerlist.get(playerlist.size()-1).getWeaponsininventory().size(); i++) {
            Player userthis = playerlist.get(playerlist.size()-1);
            int dmg = userthis.getWeaponsininventory().get(i).getWapondamage();
            String name = userthis.getWeaponsininventory().get(i).getWeaponname();
            int rng = userthis.getWeaponsininventory().get(i).getWeaponrange();
            int sprd = userthis.getWeaponsininventory().get(i).getWeaponspread();
            int cost = userthis.getWeaponsininventory().get(i).getWeaponcost();
            playerinventorylist.getItems().add(name+": Damage= "+dmg+", Range= "+rng+", Spread= "+sprd+", Cost= "+cost);
        }
        playerinventorylist.getItems().add("Money: "+playerlist.get(playerlist.size()-1).getMoneypts());
        //sendoutplayerinventories();

        sendoutshopitems();
    }

    @FXML
    public void sendoutplayerinventories(){
        playerinventorylist.getItems().clear();
        socketServer.postUpdate("ClearInventory");


        for (int i = 0; i < playerlist.size(); i++) {
            for (int j = 0; j < playerlist.get(i).getItemsinventory().size(); j++) {
                socketServer.postUpdate("PlayerInventory:"+playerlist.get(i).getName()+","+playerlist.get(i).getItemsinventory().get(j).getItemname()+": Cost: "+playerlist.get(i).getItemsinventory().get(j).getItemCost()+". Damage: "+playerlist.get(i).getItemsinventory().get(j).getItemDamage());
            }
            for (int j = 0; j < playerlist.get(i).getWeaponsininventory().size(); j++) {
                socketServer.postUpdate("PlayerInventory:"+playerlist.get(i).getName()+","+playerlist.get(i).getWeaponsininventory().get(j).getWeaponname()+": Damage: "+playerlist.get(i).getWeaponsininventory().get(j).getWapondamage()+". Range: "+playerlist.get(i).getWeaponsininventory().get(j).getWeaponrange()+". Spread: "+playerlist.get(i).getWeaponsininventory().get(j).getWeaponspread()+". Cost: "+playerlist.get(i).getWeaponsininventory().get(j).getWeaponcost());
            }
            socketServer.postUpdate("MoneyForClient:"+playerlist.get(i).getName()+","+playerlist.get(i).getMoneypts());
            socketServer.postUpdate("Resources:"+playerlist.get(i).getName()+","+playerlist.get(i).getAmtresources());

        }

        for (int j = 0; j < playerlist.get(playerlist.size()-1).getItemsinventory().size(); j++) {
            playerinventorylist.getItems().add(playerlist.get(playerlist.size()-1).getItemsinventory().get(j).getItemname()+": Cost: "+playerlist.get(playerlist.size()-1).getItemsinventory().get(j).getItemCost()+". Damage: "+playerlist.get(playerlist.size()-1).getItemsinventory().get(j).getItemDamage());
        }
        for (int j = 0; j < playerlist.get(playerlist.size()-1).getWeaponsininventory().size(); j++) {
            playerinventorylist.getItems().add(playerlist.get(playerlist.size()-1).getWeaponsininventory().get(j).getWeaponname()+": Damage: "+playerlist.get(playerlist.size()-1).getWeaponsininventory().get(j).getWapondamage()+". Range: "+playerlist.get(playerlist.size()-1).getWeaponsininventory().get(j).getWeaponrange()+". Spread: "+playerlist.get(playerlist.size()-1).getWeaponsininventory().get(j).getWeaponspread()+". Cost: "+playerlist.get(playerlist.size()-1).getWeaponsininventory().get(j).getWeaponcost());
        }
        playerinventorylist.getItems().add("Money: "+playerlist.get(playerlist.size()-1).getMoneypts());
        playerinventorylist.getItems().add("Resources: "+playerlist.get(playerlist.size()-1).getAmtresources());




    }







    @FXML
    public void showpeopleonscreen(){

        ArrayList<Integer> rows = new ArrayList<>();
        ArrayList<Integer> cols = new ArrayList<>();
        String allnums = "";

        ArrayList<Integer> bulletrows = new ArrayList<>();
        ArrayList<Integer> bulletcols = new ArrayList<>();
        String allbets = "";

        for (int i = 0; i < buttongrid.length; i++) {
            for (int j = 0; j < buttongrid[0].length; j++) {
                buttongrid[i][j].setStyle("-fx-background-color:#c2d9ff; ");
            }
        }

//        String bobtoto = "";
        for (int i = 0; i < environmentgrid.length; i++) {
            for (int j = 0; j < environmentgrid[0].length; j++) {
                if(environmentgrid[i][j]==2){
//                    String lol = "";
//                    lol+=i+",";
//                    lol+=j+",";
                    buttongrid[i][j].setStyle("-fx-background-color:#05f525; ");
//                    bobtoto+=lol;
                }
            }
        }
//        System.out.println("BOB:"+bobtoto);

        for (int i = 0; i < playerlist.size(); i++) {
            String showcolorcoordinates = "";

            if(!playerlist.get(i).isIsdead()){
                for (int j = 0; j < playerlist.get(i).getPlayerlocation().size(); j++) {
                    int r = playerlist.get(i).getPlayerlocation().get(j).getRow();
                    int c = playerlist.get(i).getPlayerlocation().get(j).getColumn();
                    if(!playerlist.get(i).isInvisible()){

                        if(playerlist.get(i).isIsshrunk()){
                            rows.add(playerlist.get(i).getPlayerlocation().get(0).getRow());
                            showcolorcoordinates+=playerlist.get(i).getPlayerlocation().get(0).getRow()+",";
                            cols.add(playerlist.get(i).getPlayerlocation().get(0).getColumn());
                            showcolorcoordinates+=playerlist.get(i).getPlayerlocation().get(0).getColumn()+",";
                            allnums+=showcolorcoordinates;
                            buttongrid[playerlist.get(i).getPlayerlocation().get(0).getRow()][playerlist.get(i).getPlayerlocation().get(0).getColumn()].setStyle("-fx-background-color:#fc1303; ");
                        }else{
                            rows.add(r);
                            showcolorcoordinates+=r+",";
                            cols.add(c);
                            showcolorcoordinates+=c+",";
                            allnums+=showcolorcoordinates;
                            buttongrid[r][c].setStyle("-fx-background-color:#fc1303; ");
                        }


                    }
                }

            }



        }

//        for (int i = 0; i < allbullets.size(); i++) {
//            buttongrid[allbullets.get(i).getBulletLocation().getRow()][allbullets.get(i).getBulletLocation().getColumn()].setStyle("-fx-background-color:#0a0000; ");
//        }
        for (int i = 0; i < playerlist.size(); i++) {
            for (int j = 0; j < playerlist.get(i).getFactories().size(); j++) {
                for (int k = 0; k < playerlist.get(i).getFactories().get(j).getFactorylocations().size(); k++) {
                    buttongrid[playerlist.get(i).getFactories().get(j).getFactorylocations().get(k).getRow()][playerlist.get(i).getFactories().get(j).getFactorylocations().get(k).getColumn()].setStyle("-fx-background-color:#e811f0; ");

                }
            }
        }

        for (int j = 0; j < allportals.size(); j++) {
            if(allportals.get(j).getTypeofportal().equals("Vertical")){
                for (int k = 0; k < allportals.get(j).getRowlist().size(); k++) {
                    buttongrid[allportals.get(j).getRowlist().get(k)][allportals.get(j).getColsame()].setStyle("-fx-background-color:"+allportals.get(j).getColor()+"; ");
                }
            }else{
                for (int k = 0; k < allportals.get(j).getCollist().size(); k++) {
                    buttongrid[allportals.get(j).getRowsame()][allportals.get(j).getCollist().get(k)].setStyle("-fx-background-color:"+allportals.get(j).getColor()+"; ");
                }
            }
        }

        for (int i = 0; i < alldrops.size(); i++) {
            buttongrid[alldrops.get(i).getDroplocation().getRow()][alldrops.get(i).getDroplocation().getColumn()].setStyle("-fx-background-color:"+alldrops.get(i).getColorofresource()+"; ");
        }






        System.out.println(allnums);
        socketServer.postUpdate("Color:"+allnums);
        socketServer.postUpdate("EnvironmentShow:");
        socketServer.postUpdate("ShowFactories");
        socketServer.postUpdate("ShowDrops");
    }

    @FXML
    private void handleDisconnectButton(ActionEvent event) {
        socketServer.shutdown();
    }

}
