/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package awsassertions;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import sun.misc.IOUtils;

/**
 *
 * @author aaronbuehne
 */
public class AWSAssertions {
    //Variables to assert
    public static String AppointmentType = null;
    public static String Date = null;
    public static String DoctorType = null;
    public static String Zip = null;
    public static String Time = null;
    public static int completed = 0;

    /**
     * @param args the command line arguments
     * Takes a single number as argument. The number of which test to synthesize voice for and send to the lex bot
     */
    public static void main(String[] args) {
        AWSAssertions.completed = 0;
        int toTest = Integer.valueOf(args[0]) - 1;
        List<String> lines = null;
        ArrayList<String[]> commands = synthesizeMPG();
        
        //The stop command is still required because as far as I know, there's no way to cancel the bot
        //Basically, when I send a bot one test, and then the next test, the bot will respond to the second test in context to the first text
        //Even if the two tests are not related
        String toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I would like to speak to a person\"`--voice-id`Joanna`stop.mpg";
        String[] stop = toSplit.split("`");
        
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`stop.mpg`stopped.mpg";
        String[] stopped = toSplit.split("`");
        
        ArrayList<String[]> tests = sendToLex();
        
        
        try {
            
            //Processbuilder that calls the command of whochever number is passed as the argument
            //Creates the MPG first
            ProcessBuilder pb;
            Process process;
            BufferedReader br;
            BufferedReader ebr;
            String s;
            if(toTest <= 12) {
                pb = new ProcessBuilder(commands.get(toTest));
                process = pb.start();

                br = new BufferedReader(new InputStreamReader(process.getInputStream()));
                ebr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
                System.out.println("Here is the standard output of the command:\n");
                s = null;
                while ((s = br.readLine()) != null) {
                    System.out.println(s);
                }
                System.out.println("Here is the error output of the command:\n");
                while ((s = ebr.readLine()) != null) {
                    System.out.println(s);
                }

                pb = new ProcessBuilder(stop);
                process = pb.start();
            }
            //Then sends the MPG file to the lexbot and writes the output to a .txt file
            pb = new ProcessBuilder(tests.get(toTest));
            process = pb.start();
            
            br = new BufferedReader(new InputStreamReader(process.getInputStream()));
            ebr = new BufferedReader(new InputStreamReader(process.getErrorStream()));
            FileWriter fw = new FileWriter("./testResult"+ (toTest + 1) +".txt");
            BufferedWriter bw = new BufferedWriter(fw);
            String toWrite = "";
            System.out.println("Here is the standard output of the command:\n");
            s = null;
            while ((s = br.readLine()) != null) {
                System.out.println(s);
                toWrite = toWrite + s + "\n";
            }
            bw.write(toWrite);
            bw.flush();
            bw.close();
            System.out.println("Here is the error output of the command:\n");
            while ((s = ebr.readLine()) != null) {
                System.out.println(s);
            }
            
            pb = new ProcessBuilder(stopped);
            process = pb.start();

        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        //Reads the output .txt file that the lex bot returns to fill the variables.
        String toRead = "testResult" + (toTest + 1) + ".txt";
        try {
            lines =Files.readAllLines(Paths.get(toRead), StandardCharsets.UTF_8);
        }
        catch (Exception e) {
            System.out.println(e);
        }
        
        if(lines.size() <= 0){
            System.out.println("Test " + args[0] + " empty. Lambda error occured");
            
        }
        else {
            AWSAssertions.completed = 1;
            int startIndex = lines.get(7).indexOf(":") + 2;
            int endIndex = lines.get(7).length()-2;
            AppointmentType = lines.get(7).substring(startIndex, endIndex);
            
            startIndex = lines.get(8).indexOf(":") + 2;
            endIndex = lines.get(8).length()-2;
            Date = lines.get(8).substring(startIndex, endIndex);
            
            
            startIndex = lines.get(9).indexOf(":") + 2;
            endIndex = lines.get(9).length()-2;
            DoctorType = lines.get(9).substring(startIndex, endIndex);
            
            startIndex = lines.get(10).indexOf(":") + 2;
            endIndex = lines.get(10).length()-2;
            Zip = lines.get(10).substring(startIndex, endIndex);
            
            startIndex = lines.get(11).indexOf(":") + 2;
            endIndex = lines.get(11).length();
            Time = lines.get(11).substring(startIndex, endIndex);
        }
            
    }
    //Creates an array list of all the commands required to synthesize the test mpgs
    public static ArrayList<String[]> synthesizeMPG() {
        ArrayList<String[]> commands = new ArrayList<String[]>();
        
        String toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a check-up for friday at 1pm\"`--voice-id`Joanna`test1.mpg";
        String[] toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"Book an appointment for Tuesday\"`--voice-id`Kimberly`test2.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a whitening\"`--voice-id`Kendra`test3.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"Please make an appointment for March 5th with a Dentist\"`--voice-id`Joanna`test4.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a dentist appointment for January 25th, 1997\"`--voice-id`Ivy`test5.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need to see a doctor\"`--voice-id`Matthew`test6.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"Make me an appointment. Preferably with a dentist\"`--voice-id`Joey`test7.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"Whitening on Friday. Preferably  At Noon.\"`--voice-id`Joey`test8.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a checkup for Saturday at midnight\"`--voice-id`Joey`test9.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a carpet-cleaning for next monday at 3pm\"`--voice-id`Kimberly`test10.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a check-up for October 27 at 3pm\"`--voice-id`Joanna`test11.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`polly`synthesize-speech`--output-format`pcm`--text`\"I need a whitening for Saturday at 2pm\"`--voice-id`Ivy`test12.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        commands.add(toAdd);
        
        return commands;
    }
    
    //Creates an arraylist of all the commands to send the mpgs to the lexbot
    public static ArrayList<String[]> sendToLex() {
        ArrayList<String[]> tests = new ArrayList<String[]>();
        
        String toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test1.mpg`testResult1.mpg";
        String[] toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test2.mpg`testResult2.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test3.mpg`testResult3.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test4.mpg`testResult4.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test5.mpg`testResult5.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test6.mpg`testResult6.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test7.mpg`testResult7.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test8.mpg`testResult8.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test9.mpg`testResult9.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test10.mpg`testResult10.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test11.mpg`testResult11.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test12.mpg`testResult12.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test13.mpg`testResult12.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test14.mpg`testResult12.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        toSplit = "/usr/local/aws/bin/aws`lex-runtime`post-content`--region`us-east-1`--bot-name`ScheduleAppointment`--bot-alias`$LATEST`--user-id`default`--content-type`audio/l16; rate=16000; channels=1`--input-stream`test15.mpg`testResult12.mpg";
        toAdd = null;
        toAdd = toSplit.split("`");
        tests.add(toAdd);
        
        return tests;
    }
    
}
