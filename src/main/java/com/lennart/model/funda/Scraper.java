package com.lennart.model.funda;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.util.concurrent.TimeUnit;

/**
 * Created by LennartMac on 23/01/2020.
 */
public class Scraper {

    public static void main(String[] args) throws Exception {
        new Scraper().scrape();
    }

    private void scrape() throws Exception {
        TimeUnit.SECONDS.sleep(2);

        String baseUrl = "view-source:https://www.funda.nl/koop/heel-nederland/verkocht/sorteer-afmelddatum-af/p";

        for(int i = 795; i < 16_000; i++) {
            String url = baseUrl + i + "/";

            StringSelection stringSelection = new StringSelection(url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            TimeUnit.MILLISECONDS.sleep(500);

            //click url bar
            rightClick(801, 87);

            TimeUnit.MILLISECONDS.sleep(300);

            //click paste
            click(550, 237);

            TimeUnit.MILLISECONDS.sleep(4950);

            //right click
            rightClick(500, 500);
            TimeUnit.MILLISECONDS.sleep(150);

            //click save page as
            TimeUnit.MILLISECONDS.sleep(500);
            click(532 ,579);

            //click save
            TimeUnit.MILLISECONDS.sleep(2000);
            click(807, 305);
            click(807, 305);
        }
    }

    private void click(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON1_MASK);
            bot.mouseRelease(InputEvent.BUTTON1_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    private void rightClick(int x, int y) {
        try {
            Robot bot = new Robot();
            bot.mouseMove(x, y);
            bot.mousePress(InputEvent.BUTTON3_MASK);
            bot.mouseRelease(InputEvent.BUTTON3_MASK);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }
}
