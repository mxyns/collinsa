package fr.insalyon.mxyns.collinsa.ui.frames;

import fr.insalyon.mxyns.collinsa.Collinsa;
import fr.insalyon.mxyns.collinsa.physics.entities.Entity;
import fr.insalyon.mxyns.collinsa.physics.ticks.TickMachine;
import fr.insalyon.mxyns.collinsa.render.Renderer;
import fr.insalyon.mxyns.collinsa.threads.ProcessingThread;

import javax.swing.*;
import java.util.Map;
import java.util.UUID;

public class Monitor {

    private JPanel mainPanel;
    private JList list1;
    private JButton step;
    private JList list2;
    private JList list3;
    private JLabel ticks;
    private JLabel count;
    private JLabel lastRend;
    private JFrame frame;
    public Timer timer;


    public Monitor() {

        frame = new JFrame();
        frame.setVisible(true);
        frame.setContentPane(mainPanel);
        frame.setLocationRelativeTo(null);
        frame.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
        frame.setSize(800,400);

        DefaultListModel<String> modelCurrent = (DefaultListModel<String>) list1.getModel();
        DefaultListModel<String> modelNext = (DefaultListModel<String>) list2.getModel();
        DefaultListModel<String> modelDiff = (DefaultListModel<String>) list3.getModel();

        TickMachine tickMachine = Collinsa.INSTANCE.getPhysics().getTickMachine();

        timer = new Timer( 250, e -> {

            Map<UUID, Entity> entitiesCurrent = Collinsa.INSTANCE.getPhysics().getTickMachine().current().entities;
            Map<UUID, Entity> entitiesNext = Collinsa.INSTANCE.getPhysics().getTickMachine().getPrev().entities;

            ticks.setText(tickMachine.current() + " | " + tickMachine.getPrev() + " | " + tickMachine.queue() + " | " + tickMachine.last() + " || " + (tickMachine.last().hashCode() == tickMachine.current().hashCode()));
            count.setText(tickMachine.current().entities.size() + " | " + tickMachine.getPrev().entities.size() + " | " + "null" + " | " + tickMachine.last().entities.size());


            modelCurrent.clear();
            entitiesCurrent.keySet().stream().sorted().forEach( uuid -> modelCurrent.addElement(uuid.toString()));

            modelNext.clear();
            entitiesNext.keySet().stream().sorted().forEach( uuid -> modelNext.addElement(uuid.toString()));

            modelDiff.clear();
            entitiesCurrent.keySet().stream().forEach( uuid -> {

                if (!entitiesNext.containsKey(uuid))
                    modelDiff.addElement("- " + uuid.toString());
            });

            entitiesNext.keySet().stream().forEach( uuid -> {

                if (!entitiesCurrent.containsKey(uuid))
                    modelDiff.addElement("+ " + uuid.toString());
            });

            lastRend.setText("last rendered = " + Renderer.lastRendered + " " + tickMachine.last().entities.size());
        });
        timer.start();
        step.addActionListener( e -> {

            ProcessingThread.stepTick = true;
            System.out.println(ProcessingThread.stepTick);
        });
    }
}
