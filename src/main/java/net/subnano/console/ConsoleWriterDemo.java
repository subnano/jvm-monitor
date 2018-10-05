package net.subnano.console;

import static net.subnano.console.Ansi.BgBlue;
import static net.subnano.console.Ansi.BgWhite;
import static net.subnano.console.Ansi.BgYellow;
import static net.subnano.console.Ansi.Blue;
import static net.subnano.console.Ansi.Red;
import static net.subnano.console.Ansi.Yellow;

public class ConsoleWriterDemo {

    private final ConsoleWriter screen;

    public ConsoleWriterDemo(ConsoleWriter screen) {
        this.screen = screen;
    }

    public void rewriteWithColors(String name) throws Exception  {
        screen.clearScreen();
        screen.print(Red.and(BgYellow).format("Hello"))
              .print(" ")
              .print(Yellow.format("World"));
        screen.display();

        Thread.sleep(1000);

        screen.display();

        Thread.sleep(1000);
    }

    public void rewrite(String name) throws Exception {
        screen.clearScreen();
        screen.print("Hello\nWorld");
        screen.display();

        Thread.sleep(1000);

        screen.clearScreen();
        screen.print("Hello\n" + name);
        screen.display();

        Thread.sleep(1000);
    }

    public static void main1(final String[] args) throws Exception {
        ConsoleWriter screen = new ConsoleWriter(System.out);
        ConsoleWriterDemo demo = new ConsoleWriterDemo(screen);
        demo.rewrite("Arthur Dent");
        demo.rewriteWithColors("Arthur Dent");
    }

    public static void main(String[] args) throws InterruptedException {
        ConsoleWriter screen = new ConsoleWriter(System.out);
        int barSize = 10;
        int spins = 50;
        for (int i=0; i<spins; i++) {
            screen.clear(0);
            screen.print("Booting ... ");
            switch (i % 4) {
                case 0: screen.print(Yellow.format("\\")); break;
                case 1: screen.print(Blue.format("|")); break;
                case 2: screen.print(Red.format("/")); break;
                case 3: screen.print(Ansi.Green.format("-")); break;
            }
            screen.display();
            Thread.sleep(50);
        }
    }
}
