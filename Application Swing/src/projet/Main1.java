package projet;

import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.GridLayout;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.Shape;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.AffineTransform;
import java.awt.geom.Ellipse2D;
import java.awt.geom.Line2D;
import java.awt.geom.Point2D;
import java.awt.geom.Rectangle2D;
import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedList;

import javax.imageio.ImageIO;
import javax.swing.BorderFactory;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JPopupMenu;
import javax.swing.JSlider;
import javax.swing.SwingUtilities;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.vecmath.Vector2d;

import org.apache.commons.collections15.Factory;
import org.apache.commons.collections15.Transformer;

import gestionGraph.MesMouseMenus;
import gestionGraph.PopupVertexEdgeMenuMousePlugin;
import edu.uci.ics.jung.algorithms.layout.Layout;
import edu.uci.ics.jung.algorithms.layout.StaticLayout;
import edu.uci.ics.jung.graph.Graph;
import edu.uci.ics.jung.graph.SparseMultigraph;
import edu.uci.ics.jung.graph.util.Context;
import edu.uci.ics.jung.visualization.Layer;
import edu.uci.ics.jung.visualization.VisualizationViewer;
import edu.uci.ics.jung.visualization.control.EditingModalGraphMouse;
import edu.uci.ics.jung.visualization.control.ModalGraphMouse;
import edu.uci.ics.jung.visualization.decorators.EdgeShape;
import edu.uci.ics.jung.visualization.decorators.ToStringLabeller;
import edu.uci.ics.jung.visualization.renderers.GradientVertexRenderer;
import edu.uci.ics.jung.visualization.renderers.VertexLabelAsShapeRenderer;


public class Main1 {

    // CONSTANTES
    public static final int FRAME_WIDTH = 1168;
    public static final int FRAME_HEIGHT = 800;
    public static final int MENU_HEIGHT = 500;
    public static final int MENU_WIDTH = 200;

    // ATTRIBUTS
    private JFrame frame; // La fenêtre
    private JMenuBar menuBar; // Menu en haut horizontal
    private JMenuItem fermer, aide;
    private JButton reconstruire;
    public static Image imgFond; // Image de fond du graph
    private int noeudRangMin; // Pour le slider de paramètrisation des nouveaux noeuds créés à la main
    private int noeudRangMax;
    private int noeudRangInit;
    private int arreteRangMin; // Pour le slider de paramétrisation des nouvelles arrêtes créées à la main
    private int arreteRangMax;
    private int arreteRangInit;
    private int coefNoeudProcheMin; // Pour le slider de paramétrisation du coef de détection des noeuds proches
    private int coefNoeudProcheMax;
    private int coefNoeudProcheInit;
    private int coefLongNouvelleRouteMin; // Pour le slider de paramétrisation du coef de longueure des nouvelles routes
                                          // créées automatiquement
    private int coefLongNouvelleRouteMax;
    private int coefLongNouvelleRouteInit;
    private JSlider sliderNoeudRang, sliderArreteRang, sliderCoefNoeudProche, sliderCoefLongNouvelleRoute; // Les
                                                                                                           // sliders

    // CONSTRUCTEUR
    public Main1() {
        createView();
        placeComponents();
        createController();
    }

    // COMMANDES
    public void display() {
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
    }

private void createView() {
    frame = new JFrame("Reconstruction d'environnements urbains - Godé Vincent");
    frame.setPreferredSize(new Dimension(FRAME_WIDTH, FRAME_HEIGHT));
    fermer = new JMenuItem("Fermer", new ImageIcon("images/fermer.jpg"));
    aide = new JMenuItem("Aide", new ImageIcon("images/aide.jpg"));
    reconstruire = new JButton("Reconstruire");

    sliderNoeudRang = new JSlider(noeudRangMin, noeudRangMax, noeudRangInit);
    sliderNoeudRang.setMinorTickSpacing(0);
    sliderNoeudRang.setMajorTickSpacing(1);
    sliderNoeudRang.setPaintTicks(true);
    sliderNoeudRang.setPaintLabels(true);
    sliderNoeudRang.setBorder(BorderFactory.createTitledBorder("Rang carrefour : "));
    sliderNoeudRang.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
    sliderArreteRang = new JSlider(arreteRangMin, arreteRangMax, arreteRangInit);
    sliderArreteRang.setMinorTickSpacing(0);
    sliderArreteRang.setMajorTickSpacing(1);
    sliderArreteRang.setPaintTicks(true);
    sliderArreteRang.setPaintLabels(true);
    sliderArreteRang.setBorder(BorderFactory.createTitledBorder("Rang route : "));
    sliderArreteRang.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
    sliderCoefNoeudProche = new JSlider(coefNoeudProcheMin, coefNoeudProcheMax, coefNoeudProcheInit);
    sliderCoefNoeudProche.setMinorTickSpacing(5);
    sliderCoefNoeudProche.setPaintTicks(true);
    sliderCoefNoeudProche.setPaintLabels(true);
    sliderCoefNoeudProche.setBorder(BorderFactory.createTitledBorder("Distance de recherche : "));
    sliderCoefNoeudProche.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
    sliderCoefLongNouvelleRoute = new JSlider(coefLongNouvelleRouteMin, coefLongNouvelleRouteMax,
            coefLongNouvelleRouteInit);
    sliderCoefLongNouvelleRoute.setMinorTickSpacing(5);
    sliderCoefLongNouvelleRoute.setPaintTicks(true);
    sliderCoefLongNouvelleRoute.setPaintLabels(true);
    sliderCoefLongNouvelleRoute.setBorder(BorderFactory.createTitledBorder("Longueure route : "));
    sliderCoefLongNouvelleRoute.setPreferredSize(new Dimension(MENU_WIDTH - 40, 70));
}

private void placeComponents() {
    JPanel menu1 = new JPanel();
    {
        menu1.setPreferredSize(new Dimension(MENU_WIDTH, MENU_HEIGHT));
        menu1.setBorder(BorderFactory.createEtchedBorder());

        JLabel l1 = new JLabel("                ");
        menu1.add(l1);

        menu1.add(sliderNoeudRang);
        menu1.add(sliderArreteRang);

        JPanel menu1_1 = new JPanel(new GridLayout(3, 1));
        
        menu1.add(menu1_1);

        JPanel menu1_3 = new JPanel(new GridLayout(2, 1));
        {
            JLabel l5 = new JLabel("                ");
            menu1_3.add(l5);
            JLabel l6 = new JLabel("                ");
            menu1_3.add(l6);
        }
        menu1.add(menu1_3);

        menu1.add(sliderCoefNoeudProche);
        menu1.add(sliderCoefLongNouvelleRoute);

        JPanel menu1_2 = new JPanel(new GridLayout(1, 1));
        {
            menu1_2.add(reconstruire);
            menu1_2.setPreferredSize(new Dimension(156, 30));
        }
        menu1.add(menu1_2);

        JPanel menu1_4 = new JPanel(new GridLayout(1, 1));
        {
            ImageIcon imgIndications = new ImageIcon("images/indications.jpg");
            JLabel l4 = new JLabel(imgIndications);
            menu1_4.add(l4, BorderLayout.CENTER);
        }
        menu1.add(menu1_4);
    }
    frame.add(menu1, BorderLayout.EAST);
    menuBar = new JMenuBar();
    {
        JMenu menuFichier = new JMenu("Fichier");
        {
            menuFichier.add(fermer);
        }
        menuBar.add(menuFichier);
        JMenu menuEdition = new JMenu("A propos");
        {
            menuEdition.add(aide);
        }
        menuBar.add(menuEdition);
    }
    frame.setJMenuBar(menuBar);
}
    
private void createController() {
    frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

    fermer.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            System.exit(0);
        }
    });

    aide.addActionListener(new ActionListener() {
        public void actionPerformed(ActionEvent e) {
            JOptionPane.showMessageDialog(frame, "Contact :\n" + "Godé Vincent : vincent.gode@etu.unilim.fr");
        }
    });


}
 // POINT D'ENTREE
 public static void main(String[] args) {
    SwingUtilities.invokeLater(new Runnable() {
        public void run() {
            new Main1().display();
        }
    });
    try {
        imgFond = ImageIO.read(new File("images/images.jpg"));
    } catch (IOException e) {
        e.printStackTrace();
    }
}

}



