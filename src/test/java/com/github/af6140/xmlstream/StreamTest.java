package com.github.af6140.xmlstream;

import static org.junit.Assert.assertEquals;
import org.junit.Test;

import java.io.File;
import java.util.Iterator;


public class StreamTest {

    @Test
    public void testStreaming() {

        String fileName = getClass().getResource("/dataset.xml").getFile();

        System.out.println(fileName);
        File bookXML = new File(fileName);
        Stream stream = new Stream(bookXML);
        stream.setTargetElement("record");

//        String o = stream.nextObject();
//        System.out.println(o);
//        while(o!=null) {
//            o = stream.nextObject();
//            if(o!=null) {
//                System.out.println(o);
//            }
//        }

        Iterator it = stream.iterator();
        while(it.hasNext()) {
            String o = (String)it.next();
            System.out.println(o);

        }

    }

    @Test
    public void testStreamingComplex() {
        //https://github.com/mhbeals/scissorsandpaste
        String fileName = getClass().getResource("/valoretek_test.xml").getFile();

        System.out.println(fileName);
        File bookXML = new File(fileName);
        Stream stream = new Stream(bookXML);
        stream.setTargetElement("TEI");

//        String o = stream.nextObject();
//        System.out.println(o);
//        while(o!=null) {
//            o = stream.nextObject();
//            if(o!=null) {
//                System.out.println(o);
//            }
//        }

        Iterator it = stream.iterator();
        long count = 0;
        while(it.hasNext()) {
            String o = (String)it.next();
            //System.out.println(o);
            count++;
        }
        System.out.println("Total records: "+ count);
    }
}
