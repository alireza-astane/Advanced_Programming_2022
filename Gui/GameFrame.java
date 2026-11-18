package Gui;
import Logic.*;
import Logic.Choice;
import util.resource.*;
import javax.swing.*;
import java.awt.*;
import java.util.*;
import java.util.List;

import static Logic.Game.cmd;

public class GameFrame extends JFrame {
    public static final int DISPLAY_NUMBER = 0;
    public static final int GAME_FRAME_WIDTH = 1100;
    public static final int GAME_FRAME_HEIGHT = 730;
    private JPanel pane;
    private List<Spot> middle_points;
    private List<Spot> left_points;
    private List<Spot> right_points;
    private JLabel board;
    private JLabel background;
    private JLabel bhand;
    private JLabel whand;
    private JLabel bhand2;
    private JLabel whand2;
    private JLabel EndText;
    private JLabel Wtxt;
    private JLabel Btxt;
    private Queue<Choice> choices = new LinkedList<>();

    public GameFrame() {
        initComponents();
        alignComponents();

        setSize(new Dimension(GAME_FRAME_WIDTH, GAME_FRAME_HEIGHT));
        setResizable(false);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setTitle("Game Of Generals");
        GraphicsEnvironment ge = GraphicsEnvironment.getLocalGraphicsEnvironment();
        GraphicsDevice[] gd = ge.getScreenDevices();
        setLocation(gd[DISPLAY_NUMBER].getDefaultConfiguration().getBounds().x +
                (gd[DISPLAY_NUMBER].getDefaultConfiguration().getBounds().width - GAME_FRAME_WIDTH) / 2, getY());

        setVisible(true);
        repaintFrame();
    }

    private void initComponents() {
        left_points = new ArrayList<>();
        right_points = new ArrayList<>();
        middle_points = new ArrayList<>();
        pane = new JPanel();
        background = new JLabel(new ImageIcon(ResourceManager.get(ImageResource.BACKGROUND)));
        board = new JLabel(new ImageIcon(ResourceManager.get(ImageResource.BOARD)));
        whand = new JLabel(new ImageIcon(ResourceManager.get(ImageResource.WHAND)));
        bhand = new JLabel(new ImageIcon(ResourceManager.get(ImageResource.BHAND)));
        whand2 = new JLabel(new ImageIcon(ResourceManager.get(ImageResource.WHAND)));
        bhand2 = new JLabel(new ImageIcon(ResourceManager.get(ImageResource.BHAND)));
        EndText = new JLabel((String) null);
        EndText.setFont(new Font("Serif",Font.BOLD,40));
        Wtxt = new JLabel("White's hand:");
        Wtxt.setFont(new Font("Serif",Font.BOLD,15));
        Wtxt.setForeground(Color.WHITE);
        Btxt = new JLabel("Black's hand:");
        Btxt.setFont(new Font("Serif",Font.BOLD,15));
        Btxt.setForeground(Color.WHITE);
        setContentPane(pane);

        for (int i = 1; i <6 ; i++) {
            for(int j=1;j<6;j++){
                ArrayList<Integer> pos = new ArrayList<>(Arrays.asList(i,j));
                Spot point = new Spot();
                middle_points.add(point);}}
        for (int i = 1; i < 13; i++) {
            Spot point = new Spot();
            right_points.add(point);}
        for (int i = 1; i < 13; i++) {
            Spot point = new Spot();
            left_points.add(point);}

    }

    private void alignComponents(){
        pane.setLayout(null);

        //todo

        background.setBounds(0, 0, 1100, 700);
        board.setBounds(300, 100, 500, 500);
        whand.setBounds(100,100,100,600);
        bhand.setBounds(900,100,100,600);
        whand2.setBounds(0,100,100,600);
        bhand2.setBounds(1000,100,100,600);
        Wtxt.setBounds(50,0,100,100);
        Btxt.setBounds(950,0,200,100);
        EndText.setBounds(450,0,300,100);

        for(int i=5;i>0;i--) {
            for(int j=5;j>0;j--){
                Spot mspot = middle_points.get((i-1)*5 +j-1);
                mspot.setBounds(100*i +200,100*j ,100,100);
                pane.add(mspot);
                mspot.addActionListener(new MouseEventHandler(this, new Choice(new ArrayList<>(Arrays.asList(i,6-j)))));}}

        for(int i=6;i>0;i--) {
            Spot lspot = left_points.get(i-1);
            lspot.setBounds(100,100*i ,100,100);
            pane.add(lspot);
            lspot.addActionListener(new MouseEventHandler(this, new Choice(new ArrayList<>(Arrays.asList(i,0)))));
            lspot = left_points.get(i-1+6);
            lspot.setBounds(0,100*i ,100,100);
            pane.add(lspot);
            lspot.addActionListener(new MouseEventHandler(this, new Choice(new ArrayList<>(Arrays.asList(i+6,0)))));}
        for(int i=1;i<7;i++) {
            Spot rspot = right_points.get(i-1);
            rspot.setBounds(900,100*i ,100,100);
            pane.add(rspot);
            rspot.addActionListener(new MouseEventHandler(this, new Choice(new ArrayList<>(Arrays.asList(0,i)))));
            rspot = right_points.get(i-1+6);
            rspot.setBounds(1000,100*i ,100,100);
            pane.add(rspot);
            rspot.addActionListener(new MouseEventHandler(this, new Choice(new ArrayList<>(Arrays.asList(0,6+i)))));
        }
        pane.add(Wtxt);
        pane.add(EndText);
        pane.add(Btxt);
        pane.add(board);
        pane.add(whand);
        pane.add(bhand);
        pane.add(whand2);
        pane.add(bhand2);
        pane.add(background);


        SetPieceLabels();
        repaintFrame();
    }

    public void repaintFrame(){
        this.revalidate();
        this.repaint();
    }

    public void addChoice(Choice choice) {
        System.out.println(choice.pos);
        choices.add(choice);
        if (choices.size() > 1){
            Choice StartingChoice = choices.poll();
            Choice DestinationChoice = choices.poll();
            ArrayList<Integer> a =StartingChoice.pos;
            String LabelChoice=getlabel(StartingChoice.pos);

            if((StartingChoice.pos.get(0)==0)||(StartingChoice.pos.get(1)==0)){a=new ArrayList<Integer>(Arrays.asList(0,0)) ;}
            cmd = new Movementcmd(LabelChoice,a,DestinationChoice.pos);
            cmd.perform();
            Game.board.boardUpdate();
            ButtonClear();
            SetPieceLabels();
            if(!Game.go){EndText.setText(Game.winner.name + " wins!");end();}
            revalidate();
            repaintFrame();
        }
    }
    private String getlabel(ArrayList<Integer> pos){
        String str;
        if((pos.get(1)==0)&&(Game.White.hand.size()!=0)){str=Game.White.hand.get(pos.get(0)-1).label;}
        else{if((pos.get(0)==0)&&(Game.Black.hand.size()!=0)){str=Game.Black.hand.get(pos.get(1)-1).label;}
        else{if(Game.board.getPieceAt(pos)!=null){str=Game.board.getPieceAt(pos).label;}
        else{return null;}
        }

        }


        return str;}

    public void SetPieceLabels(){
        for(Piece pi :Game.listOfPieces){
            getbuttun(pi).setIcon(new ImageIcon(ResourceManager.get(pi.getIconName())));
        }
    }
    public Spot getbuttun(Piece pi){
        ArrayList<Integer> handpos =new ArrayList<Integer>(Arrays.asList(0,0));
        if(pi.pos.equals(handpos)){
            if(Game.White.hand.contains(pi)){
                return left_points.get(Game.White.hand.indexOf(pi));}
            else{return right_points.get(Game.Black.hand.indexOf(pi));}
        }
        else{return middle_points.get(5-pi.pos.get(1)+(pi.pos.get(0)-1)*5);}

    }
    public void ButtonClear(){
        for(Spot s:middle_points){s.setIcon(null);}
        for(Spot s:right_points){s.setIcon(null);}
        for(Spot s:left_points){s.setIcon(null);}

    }
    public void end(){
        for(int i=5;i>0;i--) {
            for(int j=5;j>0;j--){
                Spot mspot = middle_points.get((i-1)*5 +j-1);
                mspot.setEnabled(false);
}}

        for(int i=6;i>0;i--) {
            Spot lspot = left_points.get(i-1);
            lspot.setEnabled(false);
            }

        for(int i=1;i<7;i++) {
            Spot rspot = right_points.get(i-1);
            rspot.setEnabled(false);
            }

    }
}
