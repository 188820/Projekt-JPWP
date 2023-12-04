import java.awt.*;
import java.awt.event.*;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import javax.swing.*;

public class Gra extends JPanel {
    private List<Drop> raindrops = new ArrayList<>();
    private List<Cloud> clouds = new ArrayList<>();
    private CardLayout cardLayout;

    private Image backgroundImage;

    int x = 1;

    public Gra(CardLayout cardLayout) {
        this.cardLayout = cardLayout;
        backgroundImage = new ImageIcon("Background3.png").getImage();
        // Dodanie kilku chmur
        clouds.add(new Cloud(100, 50));
        clouds.add(new Cloud(300, 100));
        clouds.add(new Cloud(500, 75));
        clouds.add(new Cloud(800, 50));
        for (int i = 0; i < 10; ++i) {
            int newDropX = (int) (Math.random() * 1024);
            int newDropY = (int) (Math.random() * 200.0);
            RainDrop newRaindrop = new RainDrop(newDropX, newDropY, "2*7");

            // Sprawdzenie, czy nowa kropla nachodzi na już istniejące
            boolean overlaps = raindrops.stream()
                    .anyMatch(existingDrop -> existingDrop.getBounds().intersects(newRaindrop.getBounds()));

            // Jeśli nie nachodzi, dodaj nową kroplę
            if (!overlaps) {
                this.raindrops.add(newRaindrop);
            }
        }
        // Obsługa powrotu do menu
        JButton bmenu = new JButton("Menu");
        bmenu.setPreferredSize(new Dimension(100, 50));
        bmenu.addActionListener(new ActionListener() {
            @Override
            public void actionPerformed(ActionEvent e) {
                Container parentContainer = getParentContainer(Gra.this);
                if (parentContainer != null && parentContainer instanceof JPanel) {
                    CardLayout cardLayout = (CardLayout) parentContainer.getLayout();
                    cardLayout.show(parentContainer, "Menu");
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
        add(bmenu);

        // Dodanie obsługi zdarzeń myszy

        addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                Iterator<Drop> iterator = raindrops.iterator();
                while (iterator.hasNext()) {
                    Drop raindrop = iterator.next();
                    Rectangle bounds = raindrop.getBounds();
                    if (bounds.contains(e.getPoint())) {
                        iterator.remove(); // Usunięcie kropelki po kliknięciu
                    }
                }
                repaint();
            }
        });

        Timer timer = new Timer(25, (e) -> {

            Iterator<Drop> iterator = this.raindrops.iterator();
            for (Cloud cloud : clouds) {
                cloud.move();
            }
            while (iterator.hasNext()) {

                Drop raindrop = iterator.next();
                raindrop.translate(0, 1);

                if (raindrop.getY() > getHeight() && x == 1) {
                    iterator.remove();
                    System.out.println("Spadly");
                    x = 0;
                }
            }


            this.repaint();
        });
        timer.start();



    }

    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        g.drawImage(backgroundImage, 0, 0, getWidth(), getHeight(), this);
        for (Drop drop : raindrops) {
            drop.draw(g);
        }

        // Rysowanie chmur
        g.setColor(Color.WHITE);
        for (Cloud cloud : clouds) {
            g.fillOval(cloud.getX(), cloud.getY(), 80, 40);
            g.fillOval(cloud.getX() + 20, cloud.getY() - 20, 80, 40);
            g.fillOval(cloud.getX() + 40, cloud.getY(), 80, 40);
        }
    }


    // Klasa reprezentująca chmurę
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
            // Przesunięcie chmury w lewo
            x -= 1;
            if (x + 80 < 0) {
                x = getWidth(); // Przenieś chmurę na prawo po opuszczeniu obszaru rysowania
            }
        }
    }
    // Klasa odpowiadająca za spadanie kropel
    private class Drop {
        private int x;
        private int y;

        public Drop(int x, int y) {
            this.x = x;
            this.y = y;
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
            return new Rectangle(x, y, 30, 75);
        }

        public void draw(Graphics g) {
            g.setColor(Color.CYAN);
            g.fillOval(x, y, 30, 75);
        }
    }

     private class RainDrop extends Drop{
        private String operation;

        public RainDrop(int x, int y, String operation) {
            super(x, y);
            this.operation = operation;
        }
         @Override
         public void draw(Graphics g) {
             super.draw(g);
             g.setColor(Color.BLACK);
             g.drawString(operation, getX()+ 5, getY() + 40);
         }
    }
}