package com.example.parserxml;

import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

public class BukuXmlParser {
    private static final String ns = null;

    public List parse(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
            parser.setInput(in, null);
            parser.nextTag();
            return readBukus(parser);
        } finally {
            in.close();
        }
    }

    private List readBukus(XmlPullParser parser) throws XmlPullParserException, IOException {
        List bukus = new ArrayList();

        parser.require(XmlPullParser.START_TAG, ns, "bukus");
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("buku")) {
                bukus.add(readBuku(parser));
            } else {
                skip(parser);
            }
        }
        return bukus;
    }

    private void skip(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

    private String readText(XmlPullParser parser) throws IOException, XmlPullParserException {
        String result = "";
        if (parser.next() == XmlPullParser.TEXT) {
            result = parser.getText();
            parser.nextTag();
        }
        return result;
    }

    private Buku readBuku(XmlPullParser parser) throws XmlPullParserException, IOException {
        parser.require(XmlPullParser.START_TAG, ns, "buku");
        String kode = null;
        String judul = null;
        String pengarang = null;
        while (parser.next() != XmlPullParser.END_TAG) {
            if (parser.getEventType() != XmlPullParser.START_TAG) {
                continue;
            }
            String name = parser.getName();
            if (name.equals("kode")) {
                kode = readKode(parser);
            } else if (name.equals("judul")) {
                judul = readJudul(parser);
            } else if (name.equals("pengarang")) {
                pengarang = readPengarang(parser);
            } else {
                skip(parser);
            }
        }
        return new Buku(kode, judul, pengarang);
    }

    private String readKode(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "kode");
        String kode = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "kode");
        return kode;
    }

    private String readJudul(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "judul");
        String judul = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "judul");
        return judul;
    }

    private String readPengarang(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, "pengarang");
        String pengarang = readText(parser);
        parser.require(XmlPullParser.END_TAG, ns, "pengarang");
        return pengarang;
    }
}
