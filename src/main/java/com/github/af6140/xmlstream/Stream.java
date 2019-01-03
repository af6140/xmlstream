package com.github.af6140.xmlstream;
import java.io.*;
import java.util.ArrayList;
import java.util.Iterator;

import javax.xml.namespace.NamespaceContext;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import javax.xml.stream.*;
import javax.xml.stream.events.EndElement;
import javax.xml.stream.events.StartElement;

import javax.xml.stream.events.XMLEvent;
import javax.xml.transform.Result;


public class Stream implements Iterable<String> {

    private final XMLInputFactory factory = XMLInputFactory.newFactory();
    private String targetElement = "foo";
    private File xmlInputFile;
    private XMLEventReader reader;

    private String prefix;
    private String suffix;

    public Stream(File file, String prefix, String suffix) {
        this.prefix = prefix;
        this.suffix = suffix;
        this.factory.setProperty(XMLInputFactory.IS_NAMESPACE_AWARE, false);

        this.xmlInputFile = file;
        try {
            reader = this.factory.createXMLEventReader(new FileInputStream(this.xmlInputFile));
        } catch (XMLStreamException e) {
            e.printStackTrace();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }

    }

    public Stream(File file) {
        this(file,"", "");
    }
    public String getTargetElement() {
        return targetElement;
    }

    public void setTargetElement(String targetElement) {
        this.targetElement = targetElement;
    }

    public String nextObject(){
        String next = null;
        try {
            while (reader.hasNext()) {
                XMLEvent event = reader.peek();
                int eventType = event.getEventType();
                if (eventType == XMLEvent.START_ELEMENT) {
                    StartElement startElement = event.asStartElement();
                    String elementLocalName = startElement.getName().getLocalPart();
                    //System.out.println(startElement.getName().toString());
                    if(this.targetElement.equals(elementLocalName)) {
                        String block = this.readObject(reader);
                        //System.out.println(block);
                        next = block;
                        //return next;
                        break;
                    } else {
                        reader.nextEvent();
                    }
                } else {
                    reader.nextEvent();
                }
            }
        } catch (XMLStreamException e) {
            e.printStackTrace();
        }

        if(next ==null) {
            this.closeReader();
            return null;
        }
        return new StringBuilder().append(this.prefix).append(next).append(this.suffix).toString();

    }

    private String readObject(XMLEventReader reader) throws XMLStreamException {
        StringWriter writer = new StringWriter();
        //XMLEventWriter eventWriter = outputFactory.createXMLEventWriter(writer);
        while(reader.hasNext()) {
            XMLEvent e = reader.nextEvent();
            int eventType = e.getEventType();
            e.writeAsEncodedUnicode(writer);
            if(eventType == XMLEvent.END_ELEMENT) {
                EndElement end = e.asEndElement();
                if (end.getName().getLocalPart().equals(this.targetElement)) {
                    break;
                }
            }
        }
        writer.flush();
        return writer.toString();
    }

    private class StreamIterator implements Iterator<String> {
        private String nextElement;

        public StreamIterator() {
            this.nextElement = Stream.this.nextObject();
            //System.out.print(this.nextElement);
        }
        public boolean hasNext() {
            if(nextElement==null) {
                return false;
            } else {
                return true;
            }
        }

        public String next() {
            String currentNext = this.nextElement;
            this.nextElement = Stream.this.nextObject();
            return currentNext;

        }

        public void remove() {
            throw new UnsupportedOperationException();
        }

    }
    public Iterator<String> iterator() {
        return new StreamIterator();
    }

    private void  closeReader() {
        if(this.reader!=null) {
            try {
                reader.close();
                reader = null;
            } catch (XMLStreamException e) {

            }
        }
    }
    public  void  close() {
        this.closeReader();
    }
}
