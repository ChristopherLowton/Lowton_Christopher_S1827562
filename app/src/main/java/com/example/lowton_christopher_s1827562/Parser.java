package com.example.lowton_christopher_s1827562;
//Christopher Lowton - S1827562
import android.util.Log;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.StringReader;
import java.util.LinkedList;

public class Parser {
    //Christopher Lowton - S1827562
    public Parser() {
    }

    public LinkedList<Item> parseXmlString(String xmlString) {
        LinkedList<Item> items = new LinkedList<Item>();

        try {
            XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
            factory.setNamespaceAware(true);
            XmlPullParser xpp = factory.newPullParser();
            xpp.setInput(new StringReader(xmlString));

            Item item = new Item();
            int currentTag = -1;
            int eventType = xpp.getEventType();
            while (eventType != XmlPullParser.END_DOCUMENT) {
                if(eventType == XmlPullParser.START_DOCUMENT) {
                    Log.d("TAG","Start document");
                } else if(eventType == XmlPullParser.START_TAG) {
                    String tag = xpp.getName().trim();
                    //Create a new item to add values to
                    if (tag.equals("item")) {
                        item = new Item();
                    } else if (tag.equals("title")) {
                        currentTag = 0;
                    } else if (tag.equals("description")) {
                        currentTag = 1;
                    } else if (tag.equals("link")) {
                        currentTag = 2;
                    } else if (tag.equals("point")) {
                        currentTag = 3;
                    } else if (tag.equals("author")) {
                        currentTag = 4;
                    } else if (tag.equals("comments")) {
                        currentTag = 5;
                    } else if (tag.equals("pubDate")) {
                        currentTag = 6;
                    }
                } else if(eventType == XmlPullParser.END_TAG) {
                    //Add item to list of items
                    if (xpp.getName().equals("item")) {
                        items.add(item);
                    }
                } else if(eventType == XmlPullParser.TEXT) {
                    String text = xpp.getText().trim().replace("<br />", "");
                    if (text.isEmpty() == false) {
                        switch(currentTag) {
                            case 0:
                                item.setTitle(text);
                                break;
                            case 1:
                                item.setDescription(text);
                                break;
                            case 2:
                                item.setLink(text);
                                break;
                            case 3:
                                item.setGeorssPoint(text);
                                break;
                            case 4:
                                item.setAuthor(text);
                                break;
                            case 5:
                                item.setComments(text);
                                break;
                            case 6:
                                item.setPubDate(text);
                                break;
                        }
                    }
                }
                eventType = xpp.next();
            }
        } catch (XmlPullParserException | IOException e) {
            e.printStackTrace();
        }
        return items;
    }
}
