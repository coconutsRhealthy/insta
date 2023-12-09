package com.lennart.model;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.InputEvent;
import java.awt.event.KeyEvent;
import java.util.List;
import java.util.concurrent.TimeUnit;

public class Scraper {

    public static void main(String[] args) throws Exception {
        new Scraper().scrape();
    }

    private List<String> getUsernamesToUse() {
        return new InstaAccounts().getAllInstaAccounts();
    }

    private void scrape() throws Exception {
        TimeUnit.SECONDS.sleep(2);

        String baseUrl = "http://picuki.com/profile/";
        List<String> userNames = getUsernamesToUse();

        int index = 0;

        for(String username : userNames) {
            index++;
            String url = baseUrl + username;

            StringSelection stringSelection = new StringSelection(url);
            Clipboard clipboard = Toolkit.getDefaultToolkit().getSystemClipboard();
            clipboard.setContents(stringSelection, null);

            TimeUnit.MILLISECONDS.sleep(500);

            //click initial url bar
            click(1047, 96);
            TimeUnit.MILLISECONDS.sleep(300);
            pressBackspace();
            TimeUnit.MILLISECONDS.sleep(300);

            //click url bar
            rightClick(1047, 96);

            TimeUnit.MILLISECONDS.sleep(300);

            //click paste
            click(1123, 249);
            TimeUnit.MILLISECONDS.sleep(50);

            //press enter
            pressEnter();

            //wait for page to load
            //TimeUnit.MILLISECONDS.sleep(4950);
            TimeUnit.MILLISECONDS.sleep(7000);

            //right click
            rightClick(1043, 309);
            TimeUnit.MILLISECONDS.sleep(150);

            //click save page as
            TimeUnit.MILLISECONDS.sleep(500);
            click(1151 ,397);

            ///
            click(842, 269);
            TimeUnit.MILLISECONDS.sleep(500);

            pressBackSpace();
            TimeUnit.MILLISECONDS.sleep(90);

            enterText("" + username);
            TimeUnit.MILLISECONDS.sleep(60);

//            //hier nog text invoeren
//            enterText(String.valueOf(index));
//            TimeUnit.MILLISECONDS.sleep(1000);

            //click save
            TimeUnit.MILLISECONDS.sleep(2000);
            click(1107, 669);
            click(1107, 669);
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

    public static void pressEnter() {
        try {
            Robot bot = new Robot();
            bot.keyPress(KeyEvent.VK_ENTER);
            bot.keyRelease(KeyEvent.VK_ENTER);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void pressBackspace() {
        try {
            Robot bot = new Robot();
            bot.keyPress(KeyEvent.VK_BACK_SPACE);
            bot.keyRelease(KeyEvent.VK_BACK_SPACE);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void enterText(String text) {
        char[] charArray = text.toCharArray();

        try {
            Robot bot = new Robot();

            for(char c : charArray) {
                bot.keyPress(KeyEvent.getExtendedKeyCodeForChar(c));
                bot.keyRelease(KeyEvent.getExtendedKeyCodeForChar(c));
            }
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

    public static void pressBackSpace() {
        try {
            Robot bot = new Robot();
            bot.keyPress(KeyEvent.VK_BACK_SPACE);
            bot.keyRelease(KeyEvent.VK_BACK_SPACE);
        } catch (AWTException e) {
            e.printStackTrace();
        }
    }

}
