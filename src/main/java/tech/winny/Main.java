package tech.winny;

import com.googlecode.lanterna.terminal.Terminal;
import com.googlecode.lanterna.terminal.DefaultTerminalFactory;

import java.io.IOException;

// Press Shift twice to open the Search Everywhere dialog and type `show whitespaces`,
// then press Enter. You can now see whitespace characters in your code.
public class Main {
    public static void main(String[] args) throws InterruptedException {
        DefaultTerminalFactory defaultTerminalFactory = new DefaultTerminalFactory();
        Terminal terminal = null;
        try {
            terminal = defaultTerminalFactory.createTerminal();
            for (;;) {
                for (var metric : PsiMonitor.getMetrics()) {
                    terminal.putString(metric.type().toString().toUpperCase());
                    terminal.putCharacter(':');
                    terminal.putCharacter('\n');
                    terminal.putString(metric.some().toString());
                    terminal.putCharacter('\n');
                    terminal.putString(metric.full().toString());
                    terminal.putCharacter('\n');
                    terminal.putCharacter('\n');
                }
                terminal.flush();
                Thread.sleep(2000);
                terminal.clearScreen();
            }


        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (terminal != null) {
                try {
                    terminal.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }
}