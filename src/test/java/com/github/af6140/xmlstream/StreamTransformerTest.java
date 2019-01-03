package com.github.af6140.xmlstream;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;


public class StreamTransformerTest {

    @Test
    public void testStreamTransformer() {
        String xmlFileName = getClass().getResource("/phone_book.xml").getFile();
        String xslFileName = getClass().getResource("/phone_book.xsl").getFile();
        File xml = new File(xmlFileName);
        File xsl = new File(xslFileName);
        String prefix = "<PHONEBOOK>";
        String suffix = "</PHONEBOOK>";
        PrintWriter out = new PrintWriter(System.out, true);
        //prefix = ""; suffix = "";
        StreamTransformer streamTransformer = new StreamTransformer(xml, xsl, out,"PERSON", prefix, suffix );
        try {
            streamTransformer.transform();
        } catch (TransformerException e) {
            e.printStackTrace();
        }finally {
            streamTransformer.close();
        }
    }

    @Test
    public void testStreamTransformerComplex() {
        String xmlFileName = getClass().getResource("/valoretek_test.xml").getFile();
        String xslFileName = getClass().getResource("/valoretek.xsl").getFile();
        File xml = new File(xmlFileName);
        File xsl = new File(xslFileName);
        String prefix = "<offerReport>";
        String suffix = "</offerReport>";
        prefix = ""; suffix = "";

        StreamTransformer streamTransformer=null;
        try {
            FileWriter out = new FileWriter(new File("/dev/null"));
            //prefix = ""; suffix = "";
            streamTransformer = new StreamTransformer(xml, xsl, out,"offer_detail", prefix, suffix );
            streamTransformer.transform();
        } catch (TransformerException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if(streamTransformer!=null) streamTransformer.close();
        }
    }
}
