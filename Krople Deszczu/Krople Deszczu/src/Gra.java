import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;
import javax.swing.*;
import javax.swing.Timer;

public class Gra extends JPanel {
    public List<Drop> raindrops = new ArrayList<>();
    public List<Cloud> clouds = new ArrayList<>();
    public int totalCorrectDrops;
    public Image backgroundImage;
    public long startTime;
    final int MIN_DISTANCE = 100;
    public int level;
    public boolean gameEnded = false;
    public Timer timer;
    public List<int[]> incorrectProducts = new ArrayList<>();
    public Menu menu;
    public int droppedDrops = 0;
    public int correctClicks = 0;
    public int incorrectClicks = 0;
    int[][] products = {
            {3, 4},
            {3, 8},
            {9, 7},
            {4, 3},
            {2, 6},
            {6, 4},
            {2, 12},
            {6, 6},
            {4, 9},
            {8, 8},
            {16, 4}
    };

    public Gra( int level , Menu menu) {

        this.level = level;
        this.menu=menu;
        setLayout(new BorderLayout());
        backgroundImage = new ImageIcon("Background3.png").getImage();

        startTime = System.currentTimeMillis();

        /** Dodanie kilku chmur

         */
        clouds.add(new Cloud(100, 50));
        clouds.add(new Cloud(300, 100));
        clouds.add(new Cloud(500, 75));
        clouds.add(new Cloud(800, 50));






        int Result;
        switch (level) {
            case 1:
                Result = 24;
                break;
            case 2:
                Result = 12;
                break;
            case 3:
                Result = 36;
                break;
            default:
                Result = 64;
                break;
        }

        totalCorrectDrops = countTotalCorrectDrops(products, Result);


        /** Dodawanie kropli
         *
         */
        for (int[] number : products) {
            boolean placed = false;
            while (!placed) {
                int newDropX = (int) (Math.random() * 960);
                int newDropY = (int) (Math.random() * 200.0 - 100);
                RainDrop newRaindrop = new RainDrop(newDropX, newDropY, number[0], number[1]);

                // Sprawdzenie, czy nowa kropla nachodzi na już istniejące
                boolean overlaps = raindrops.stream().anyMatch(existingDrop -> {
                    int distanceX = Math.abs(existingDrop.getX() - newRaindrop.getX());
                    int distanceY = Math.abs(existingDrop.getY() - newRaindrop.getY());
                    // Sprawdzenie, czy odległość między kroplami jest mniejsza niż minimalna dozwolona
                    return distanceX < MIN_DISTANCE && distanceY < MIN_DISTANCE;
                });
                // Jeśli nie nachodzi, dodaj nową kroplę
                if (!overlaps) {

                    this.raindrops.add(newRaindrop);
                    placed = true;
                }
            }
        }

        /** Obsługa powrotu do menu
         *
         */
        JButton bmenu = new JButton("Menu");
        JPanel menuPanel = new JPanel(new FlowLayout(FlowLayout.RIGHT));
        menuPanel.setOpaque(false);
        bmenu.setPreferredSize(new Dimension(100, 50));
        bmenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parentContainer = getParentContainer(Gra.this);
                if (parentContainer != null && parentContainer instanceof JPanel) {
                    CardLayout cardLayout = (CardLayout) parentContainer.getLayout();
                    cardLayout.show(parentContainer, "Menu");
                    stopGame();
                }

            }

            private Container getParentContainer(Component component) {
                Container parent = component.getParent();
                while (parent != null && !(parent instanceof JPanel)) {
                    parent = parent.getParent();
                }
                return parent;
            }
        });
        menuPanel.add(bmenu);
        add(menuPanel, BorderLayout.NORTH);


        /** Dodanie obsługi zdarzeń myszy
         *
         */

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Iterator<Drop> iterator = raindrops.iterator();
                while (iterator.hasNext()) {
                    Drop raindrop = iterator.next();
                    Rectangle bounds = raindrop.getBounds();
                    RainDrop rainDrop = (RainDrop) raindrop;
                    if (bounds.contains(e.getPoint()) ) {
                        if (rainDrop.getResult() == Result) {
                            iterator.remove(); // Usunięcie kropelki po kliknięciu
                            correctClicks++;
                            if (correctClicks == totalCorrectDrops) {
                                repaint();
                                long finishTime = System.currentTimeMillis();
                                long timeTaken = finishTime - startTime;
                                if (incorrectProducts.isEmpty())
                                {
                                    stopGame();
                                    JOptionPane.showMessageDialog(Gra.this, "Dobre kliknięcia: " + correctClicks
                                                    + "\nZłe kliknięcia: " + incorrectClicks,
                                            "\nPrzechodzisz do następnego poziomu", JOptionPane.INFORMATION_MESSAGE);
                                    menu.showLevelCompletionTime(timeTaken, level); // Wyświetl czas w menu
                                    goToNextLevel();
                                }
                                else
                                {
                                    stopGame();
                                    JOptionPane.showMessageDialog(Gra.this, "Dobre kliknięcia: " + correctClicks
                                            + "\nZłe kliknięcia: " + incorrectClicks, "Podsumowanie", JOptionPane.INFORMATION_MESSAGE);
                                    askForIncorrectProducts();
                                }

                            }
                        }
                        else
                        {
                            incorrectClicks++;
                            incorrectProducts.add(new int[] {rainDrop.getNumber1(), rainDrop.getNumber2()});
                            rainDrop.setIncorrect(true);
                        }
                    }
                }
                repaint();
            }
        });

        this.timer = new Timer(17, (e) -> {
            if (!gameEnded) {
                Iterator<Drop> iterator = this.raindrops.iterator();
                for (Cloud cloud : clouds) {
                    cloud.move();
                }
                while (iterator.hasNext()) {

                    Drop raindrop = iterator.next();
                    raindrop.translate(0, 1);

                    if (raindrop.getY() > getHeight()+100) {
                        droppedDrops++;
                        if (droppedDrops == raindrops.size())
                        {
                            JOptionPane.showMessageDialog(Gra.this, "Wszystkie krople spadły." + "\nDobre kliknięcia: " + correctClicks
                                    + "\nZłe kliknięcia: " + incorrectClicks, "Podsumowanie", JOptionPane.INFORMATION_MESSAGE);
                            askForIncorrectProducts();

                        }
                    }
                }
                this.repaint();
            }
        });
        this.timer.start();



    }

    /** Metoda pytająca gracza o poprawne wyniki działań
     *
     */
    public void askForIncorrectProducts() {
        for (int[] productPair : incorrectProducts) {
            boolean correctAnswer = false;
            do {
                String response = JOptionPane.showInputDialog(this, "Jaki jest wynik iloczynu dla: " + productPair[0] + " * " + productPair[1] + "?");
                // Sprawdzenie, czy użytkownik anulował dialog
                if (response == null) {
                    JOptionPane.showMessageDialog(this, "Anulowano. Prawidłowa odpowiedź to: " + (productPair[0] * productPair[1]), "Informacja", JOptionPane.INFORMATION_MESSAGE);
                    break;
                }
                try {
                    int answer = Integer.parseInt(response);
                    if (answer == productPair[0] * productPair[1]) {
                        JOptionPane.showMessageDialog(this, "Poprawna odpowiedź!", "Gratulacje", JOptionPane.INFORMATION_MESSAGE);
                        correctAnswer = true;
                    } else {
                        JOptionPane.showMessageDialog(this, "To nie jest poprawna odpowiedź. Spróbuj ponownie.", "Błąd", JOptionPane.ERROR_MESSAGE);
                    }
                } catch (NumberFormatException e) {
                    JOptionPane.showMessageDialog(this, "Proszę wpisać liczbę.", "Błąd", JOptionPane.ERROR_MESSAGE);
                }
            } while (!correctAnswer);
        }
        // Po przejściu przez wszystkie iloczyny

        JOptionPane.showMessageDialog(this, "Teraz musisz powtórzyć poziom.", "Powtórz poziom", JOptionPane.INFORMATION_MESSAGE);
        restartLevel();
    }

    /** Metoda odpowiedzialna za przejście do następnego poziomu */
    public void goToNextLevel()
    {
        if (level==4)
        {
            level=0;
        }
        if (this.timer != null) {
            this.timer.stop();
        }

        menu.startGame(level + 1);
    }
    /** Metoda odpowiedzialna za restart poziomu*/
    public void restartLevel()
    {
        droppedDrops = 0;
        if (this.timer != null)
        {
            this.timer.stop();
        }
        menu.startGame(this.level);
    }
    /** Metoda odpowiedzialna za zatrzymanie gry*/
    public void stopGame() {
        if (this.timer != null) {
            this.timer.stop();
        }
        gameEnded = true;
    }

    /** Metoda, która zlicza liczbe kropel dających poprawny wynik
     *
     * @param pairs
     * @param Result
     * @return
     */
    public int countTotalCorrectDrops(int[][] pairs, int Result) {
        int count = 0;
        for (int[] pair : pairs) {
            if (pair[0] * pair[1] == Result) {
                count++;
            }
        }
        return count;
    }

    /** Metoda odpowiedzialna za rysowanie elementów
     *
     * @param g the <code>Graphics</code> object to protect
     */
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);


        // Rysowanie chmur
        g.setColor(Color.WHITE);
        for (Cloud cloud : clouds) {
            g.fillOval(cloud.getX(), cloud.getY(), 80, 40);
            g.fillOval(cloud.getX() + 20, cloud.getY() - 20, 80, 40);
            g.fillOval(cloud.getX() + 40, cloud.getY(), 80, 40);
        }
        for (Drop drop : raindrops) {
            drop.draw(g);
        }
        g.setColor(Color.BLACK);
        // Rysowanie poleceń
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        if (level==1)
        {
            g.drawString("Iloczyn równy 24", 400, 30);
        }
        else if (level ==2)
        {
            g.drawString("Iloczyn równy 12", 400, 30);
        }
        else if (level ==3)
        {
            g.drawString("Iloczyn równy 36", 400, 30);
        }
        else if (level ==4)
        {
            g.drawString("Iloczyn równy 64", 400, 30);
        }
        // Rysowanie czasu
        long Millis = System.currentTimeMillis() - startTime;
        // Konwersja czasu z milisekund na sekundy
        int Seconds = (int) (Millis / 1000);
        String timeString = String.format("Czas: %02d:%02d",Seconds / 60, Seconds % 60);

        // Rysowanie tła tekstu
        g.setColor(Color.WHITE);
        g.fillRect(0, getHeight() - 30, 150, 35);

        g.setColor(Color.BLACK);
        g.setFont(new Font("SansSerif", Font.BOLD, 24));
        g.drawString(timeString, 10, getHeight() - 10);
    }


    /** Klasa reprezentująca chmurę
     *
     */
    private class Cloud {
        private int x;
        private int y;

        public Cloud(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void move() {
            /** Przesunięcie chmury w lewo
             *
             */
            x -= 1;
            if (x + 120 < 0) {
                x = getWidth();
                /** Przenoszenie chmury na prawo po opuszczeniu obszaru rysowania
                 */
            }
        }
    }

    /** Klasa odpowiadająca za spadanie kropel
     *
     */
    public class Drop {
         int x;
         int y;
         boolean Incorrect = false;
        public Drop(int x, int y) {
            this.x = x;
            this.y = y;
        }

        public void setIncorrect (boolean Incorrect)
        {
            this.Incorrect=Incorrect;
        }

        public int getX() {
            return x;
        }

        public int getY() {
            return y;
        }

        public void translate(int dx, int dy) {
            this.x += dx;
            this.y += dy;
        }

        public Rectangle getBounds() {
            return new Rectangle(x, y, 45, 90);
        }

        public void draw(Graphics g) {
            if (Incorrect) {
                g.setColor(Color.RED);
                g.fillOval(x, y, 45, 90);
            }
            else {
                g.setColor(Color.CYAN);
                g.fillOval(x, y, 45, 90);
            }


        }
    }

    /** Klasa zwracająca liczby na kroplach
     *
     */
    public class RainDrop extends Drop {
        public String operation;
        public int result, Number1, Number2;


        public RainDrop(int x, int y, int Number1, int Number2) {
            super(x, y);
            this.operation = Number1 + "*" + Number2;
            this.result = Number1 * Number2;
            this.Number1=Number1;
            this.Number2=Number2;

        }

        public int getNumber1 ()
        {
            return Number1;
        }

        public int getNumber2 ()
        {
            return Number2;
        }
        public int getResult() {
            return result;
        }


        @Override
        public void draw(Graphics g) {
            super.draw(g);

            g.setColor(Color.BLACK);
            g.setFont(new Font("SansSerif", Font.BOLD, 16));
            g.drawString(operation, getX()+ 7, getY() + 46);

        }
    }
}