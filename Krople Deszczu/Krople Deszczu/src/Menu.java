import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;


public class Menu extends JFrame implements ActionListener{

    JButton level1Button, level2Button,level3Button,level4Button, exit;

    private JLabel timeLabel1 ,timeLabel2, timeLabel3, timeLabel4, Label, Label2;

    private JPanel timePanel1, timePanel2, timePanel3, timePanel4;
    private JPanel cards;
    private CardLayout cardLayout;


    private Image backgroundImage;

    private double bestTime1 = Double.MAX_VALUE;
    private double bestTime2 = Double.MAX_VALUE;
    private double bestTime3 = Double.MAX_VALUE;
    private double bestTime4 = Double.MAX_VALUE;



    public Menu(CardLayout cardLayout) {
        setUndecorated(true); // Usunięcie klasycznego okna
        this.cardLayout = cardLayout;
        setSize(1024, 768);
        setLayout(new BorderLayout());
        backgroundImage = new ImageIcon("Background3.png").getImage();

        this.cardLayout = new CardLayout();
        cards = new JPanel(this.cardLayout);

        JPanel menuPanel = createMenuPanel();
        cards.add(menuPanel, "Menu");

        add(cards);

        this.cardLayout.show(cards, "Menu");
        setLocationRelativeTo(null);
        setVisible(true);

    }

    /** Tworzenie panelu
     *
     * @return
     */
    public JPanel createMenuPanel ()
    {
        JPanel menuPanel = new JPanel() {
        @Override
        protected void paintComponent(Graphics g) {
            super.paintComponent(g);
            g.drawImage(backgroundImage, 0, 0, this.getWidth(), this.getHeight(), this);

        }
        };


        menuPanel.setLayout(null);

        Label = new JLabel("Twoim zadaniem jest kliknięcie w spadające krople, których wynik jest równy podanemu iloczynowi.");
        Label.setBounds(180, 20, 800, 25);
        Label.setFont(new Font("SansSerif", Font.BOLD, 16));
        Label2 = new JLabel("Wybierz poziom.");
        Label2.setBounds(430, 50, 800, 25);
        Label2.setFont(new Font("SansSerif", Font.BOLD, 16));
        menuPanel.add(Label);
        menuPanel.add(Label2);


        level1Button = new JButton("Iloczyn równy 24");
        level1Button.setBounds(200, 100, 200, 100);

        level2Button = new JButton("Iloczyn równy 12");
        level2Button.setBounds(600, 100, 200, 100);

        level3Button = new JButton("Iloczyn równy 36");
        level3Button.setBounds(200, 300, 200, 100);

        level4Button = new JButton("Iloczyn równy 64");
        level4Button.setBounds(600, 300, 200, 100);


        exit = new JButton("Wyjdz z gry");
        exit.setBounds(400, 500, 200, 200);

        menuPanel.add(level1Button);
        menuPanel.add(level2Button);
        menuPanel.add(level3Button);
        menuPanel.add(level4Button);
        menuPanel.add(exit);


        timeLabel1 = new JLabel();
        timeLabel2 = new JLabel();
        timeLabel3 = new JLabel();
        timeLabel4 = new JLabel();
        timeLabel1.setBounds(220, 200, 200, 50);
        timeLabel2.setBounds(620, 200, 200, 50);
        timeLabel3.setBounds(220, 400, 200, 50);
        timeLabel4.setBounds(620, 400, 200, 50);
        menuPanel.add(timeLabel1);
        menuPanel.add(timeLabel2);
        menuPanel.add(timeLabel3);
        menuPanel.add(timeLabel4);

        timePanel1 = new JPanel();
        timePanel2 = new JPanel();
        timePanel3 = new JPanel();
        timePanel4= new JPanel();
        timePanel1.setBounds(220, 220, 175, 15);
        timePanel2.setBounds(620, 220, 175, 15);
        timePanel3.setBounds(220, 420, 175, 15);
        timePanel4.setBounds(620, 420, 175, 15);
        timePanel1.setOpaque(false);
        timePanel2.setOpaque(false);
        timePanel3.setOpaque(false);
        timePanel4.setOpaque(false);
        menuPanel.add(timePanel1);
        menuPanel.add(timePanel2);
        menuPanel.add(timePanel3);
        menuPanel.add(timePanel4);

        level1Button.addActionListener(this);
        level2Button.addActionListener(this);
        level3Button.addActionListener(this);
        level4Button.addActionListener(this);
        exit.addActionListener(this);



        return menuPanel;
    }

    /** Metoda wyświetlająca najlepsze czasy w menu
     */
    public void showLevelCompletionTime(long time, int level)
    {
        double timeInSeconds = time / 1000.0;
        if (level == 1)
            {
                if (timeInSeconds < bestTime1)
                    {
                        bestTime1=timeInSeconds;
                        timePanel1.setOpaque(true);
                        timeLabel1.setText("Najlepszy czas: " + timeInSeconds + " sekund");
                    }
            }
        else if (level == 2)
            {
                if (timeInSeconds < bestTime2) {
                    bestTime2 = timeInSeconds;
                    timePanel2.setOpaque(true);
                    timeLabel2.setText("Najlepszy czas: " + timeInSeconds + " sekund");
                }
            }
        else if (level == 3)
            {
                if (timeInSeconds < bestTime3)
                {
                    bestTime3 = timeInSeconds;
                    timePanel3.setOpaque(true);
                    timeLabel3.setText("Najlepszy czas: " + timeInSeconds + " sekund");
                }
            }
        else if (level == 4)
            {
                if (timeInSeconds < bestTime4)
                {
                    bestTime4=timeInSeconds;
                    timePanel4.setOpaque(true);
                    timeLabel4.setText("Najlepszy czas: " + timeInSeconds + " sekund");
                }

            }
    }


    /** Wybieranie poziomów
     * @param e the event to be processed
     */
    public void actionPerformed (ActionEvent e)
    {
        Object source = e.getSource();
        if (source == level1Button) {
            startGame(1);
        }
        else if (source == level2Button)
        {
            startGame(2);
        }
        else if (source == level3Button)
        {
            startGame(3);
        }
        else if (source == level4Button)
        {
            startGame(4);
        }
        else if (source == exit)
        {
            System.exit(0);
        }
    }

    /** Start gry
     * @param level
     */
    public void startGame(int level)
    {

        Gra gra = new Gra(level, this);
        cards.add(gra, "Gra");
        this.cardLayout.show(cards, "Gra");
    }

}


