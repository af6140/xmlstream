package com.github.af6140.xmlstream;

import org.w3c.dom.Document;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;
import java.io.*;
import java.util.Iterator;

public class StreamTransformer {

    private File xmlFile;
    private File xslFile;
    private Writer outputWriter;
    private String targetElement;
    private String suffix;
    private String prefix;

    private DocumentBuilderFactory builderFactory;
    private TransformerFactory transformerFactory;
    private Stream stream;
    private StreamResult result;

    private Boolean omitXMLDeclaration = true;
    private String  outputSeperator = "\n";

    public Boolean getOmitXMLDeclaration() {
        return omitXMLDeclaration;
    }

    public void setOmitXMLDeclaration(Boolean omitXMLDeclaration) {
        this.omitXMLDeclaration = omitXMLDeclaration;
    }

    public String getOutputSeperator() {
        return outputSeperator;
    }

    public void setOutputSeperator(String outputSeperator) {
        this.outputSeperator = outputSeperator;
    }

    public StreamTransformer(File xmlFile, File xslFile, Writer outputWriter, String targetElement, String prefix, String suffix){
        this.xmlFile = xmlFile;
        this.xslFile = xslFile;
        this.suffix = suffix;
        this.prefix = prefix;
        this.outputWriter = outputWriter;
        this.targetElement = targetElement;
        builderFactory = DocumentBuilderFactory.newInstance();
        transformerFactory = TransformerFactory.newInstance();
    }

    public StreamTransformer(File xmlFile, File xslFile, Writer outputWriter, String targetElement){
        this(xmlFile, xslFile, outputWriter, targetElement, "","");
    }

    public void transform() throws TransformerException {
        try {
            StreamSource styleSource = new StreamSource(xslFile);
            Transformer transformer = transformerFactory.newTransformer(styleSource);
            result = new StreamResult(outputWriter);
            transformer.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, this.omitXMLDeclaration.toString());
            stream = new Stream(xmlFile, this.prefix, this.suffix);
            stream.setTargetElement(this.targetElement);
            Iterator it = stream.iterator();
            while(it.hasNext()) {
                String o = (String)it.next();
                DocumentBuilder builder = this.builderFactory.newDocumentBuilder();
                InputSource inputSource = new InputSource( new StringReader( o ) );
                Document document = builder.parse(inputSource);
                DOMSource source = new DOMSource(document);
                transformer.transform(source, result);
                if(this.outputSeperator!=null) {
                    result.getWriter().write(this.outputSeperator);
                }
            }


        } catch (TransformerConfigurationException e) {
            throw  new TransformerException(e.getMessage());
        } catch (ParserConfigurationException e) {
            throw  new TransformerException(e.getMessage());
        } catch (FileNotFoundException e){
            throw  new TransformerException(e.getMessage());
        } catch (IOException e) {
            throw  new TransformerException(e.getMessage());
        } catch (SAXException e) {
            throw  new TransformerException(e.getMessage());
        } catch (javax.xml.transform.TransformerException e) {
            throw  new TransformerException(e.getMessage());
        } finally {
            this.close();
        }
    }

    public void close(){
        try {
            this.outputWriter.flush();
        }catch(Exception e) {
            try {
                this.outputWriter.close();
            } catch (IOException e1) {
            }
        }
        this.stream.close();
    }
}
