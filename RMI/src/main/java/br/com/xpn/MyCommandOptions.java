package br.com.xpn;

import org.apache.commons.cli.CommandLine;
import org.apache.commons.cli.CommandLineParser;
import org.apache.commons.cli.DefaultParser;
import org.apache.commons.cli.HelpFormatter;
import org.apache.commons.cli.Option;
import org.apache.commons.cli.Options;
import org.apache.commons.cli.ParseException;

public class MyCommandOptions {
    private CommandLine cmd;
    private Options options;

    public MyCommandOptions(){
        this.options = new Options();
        Option host = new Option("h", "host", true, "ip ou hostname remote server");
        host.setRequired(true);
        this.options.addOption(host);

        Option port = new Option("p", "port", true, "port remote server");
        port.setRequired(true);
        this.options.addOption(port);
    }

    public void run(final String[] args){
        final CommandLineParser parser = new DefaultParser();
        final HelpFormatter formatter = new HelpFormatter();
        try {
            this.cmd = parser.parse(this.options, args);
        } catch (final ParseException e) {
            System.out.println(e.getMessage());
            formatter.printHelp("utility-name", this.options);
            System.exit(1);
        }
    }

    public String getHost(){
        return this.cmd.getOptionValue("host");
    }

    public int getPort(){
        return Integer.parseInt(this.cmd.getOptionValue("port"));
    }

}
