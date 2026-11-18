package Gui;

import Logic.Choice;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

public class MouseEventHandler implements ActionListener {
    private GameFrame frame;
    private Choice choice;

    public MouseEventHandler(GameFrame frame, Choice choice) {
        this.frame = frame;
        this.choice = choice;
    }



    @Override
    public void actionPerformed(ActionEvent e) {
        frame.addChoice(this.choice);
    }
}
