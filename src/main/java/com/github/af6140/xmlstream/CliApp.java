package com.github.af6140.xmlstream;

import com.kakawait.spring.boot.picocli.autoconfigure.ExitStatus;
import com.kakawait.spring.boot.picocli.autoconfigure.HelpAwarePicocliCommand;
import com.kakawait.spring.boot.picocli.autoconfigure.PicocliConfigurerAdapter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.Banner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.ApplicationContext;
import org.springframework.context.annotation.Configuration;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import picocli.CommandLine;
import picocli.CommandLine.Help.Ansi;
import picocli.CommandLine.Option;
import picocli.CommandLine.Parameters;

import java.io.*;
import java.util.HashMap;
import java.util.List;


@SpringBootApplication
public class CliApp {

    private static String[] _args;
    @Autowired
    private ApplicationContext applicationContext;

    public static void main(String[] args) {
        _args = args;
        SpringApplication application = new SpringApplication(CliApp.class);
        application.run(args);
    }

    @Component
    @CommandLine.Command
    static class MainCommand extends HelpAwarePicocliCommand {
        @Option(names = {"-v", "--version"}, description = "display version info")
        boolean versionRequested;

        @Override
        public ExitStatus call() {
            if (versionRequested) {
                System.out.println("1.0.0");
                return ExitStatus.TERMINATION;
            }
            return ExitStatus.OK;
        }
    }

    private static void exitWithError(Object o, int status, String message, Object... params) {
        System.err.printf(message, params);
        System.err.println();
        new CommandLine(o).usage(System.err);
        exit(status);
    }

    private static void exit(int status) {
        System.exit(status); // no need to start a new thread
    }

    @Configuration
    class CustomPicocliConfiguration extends PicocliConfigurerAdapter {

        @Override
        public void configure(CommandLine commandLine) {
            // Here you can configure Picocli commandLine
            // You can add additional sub-commands or register converters.
//            commandLine.parseWithHandlers(new CommandLine.RunFirst(),
//                    // set exit code to use when any exception occurs.
//                    // Unfortunately it is currently not possible to have different exit codes per exception...
//                    CommandLine.defaultExceptionHandler().andExit(1), CliApp._args);
        }
    }

    @Component
    @CommandLine.Command(name = "transform")
    static class TransformCommand extends HelpAwarePicocliCommand {

        @Option(names = "-xml", required = true, description = "xml file")
        String xmlFile;

        @Option(names = "-xsl", required = true, description = "xsl file")
        String xslFile;

        @Option(names = "-target", required = true, description = "target element")
        String targetElement;

        @Option(names = "-output", required = true, description = "output file")
        String outputFile;

        @Option(names = "-header", required = false, description = "header for the output file")
        String header="";

        @Option(names = "-footer", required = false, description = "footer for the output file")
        String footer="";

        @Override
        public ExitStatus call() throws TransformerException{
            File xml = new File(xmlFile);
            File xsl = new File(xslFile);
            String prefix = this.header;
            String suffix = this.footer;
            if(!xml.exists() || !xsl.exists()) {
                exitWithError(this, 1, "Xml or xsl file does not exist");
            }
            if(StringUtils.isEmpty(this.targetElement)) {
                exitWithError(this, 1, "Invalid target element sepcified: "+this.targetElement);
            }
            StreamTransformer streamTransformer=null;
            try {
                BufferedWriter out = new BufferedWriter(new FileWriter(this.outputFile), 4096*300);

                //prefix = ""; suffix = "";
                streamTransformer = new StreamTransformer(xml, xsl, out, this.targetElement, prefix, suffix );
                streamTransformer.transform();
            } catch (TransformerException e) {
                exitWithError(this, 1, e.getMessage());
            } catch (FileNotFoundException e){
                exitWithError(this, 1, e.getMessage());
            }
            catch (IOException e) {
                exitWithError(this, 1, e.getMessage());
            } finally {
                if(streamTransformer!=null) streamTransformer.close();
            }

            return ExitStatus.OK;
        }

    }
}
