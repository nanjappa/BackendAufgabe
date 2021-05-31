package com.politischereden.springbootpolitische;

import Dto.ResponseDto;
import com.opencsv.CSVReaderBuilder;
import lombok.RequiredArgsConstructor;

import org.springframework.web.bind.annotation.*;

import java.io.FileReader;
import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import com.opencsv.CSVReader;
import static  com.politischereden.springbootpolitische.ApiConstants.*;

@RestController
@RequiredArgsConstructor
@RequestMapping(path = ROOT)
public class HomeController {

    private ResponseDto responseDto;

    @GetMapping(path = EVALUATION,produces = APPLICATION_JSON)
    public ResponseDto getPolitischeInformation(@RequestParam String mostSpeechesForYear, @RequestParam String mostSpokenThema ) {
        try {

            CSVReader reader = null;

            reader = new CSVReaderBuilder(new FileReader(CSVDatafileName)).withSkipLines(FIRSTLINE).build();
            String[] CSVDataLineArray;

            Map<String, Integer> mapPoliticians = new HashMap<>();
            String maxSpeechesForYearPolitik = null;

            DateTimeFormatter df = DateTimeFormatter .ofPattern(DATE_PATTERN, Locale.forLanguageTag(LOCALE));
            while ((CSVDataLineArray = reader.readNext()) != null) {
                int year1 = LocalDate.parse( CSVDataLineArray[THIRD_COLUMN], df).atStartOfDay().getYear();
                if(Integer.parseInt(mostSpeechesForYear) == year1) {
                    System.out.println(CSVDataLineArray[FIRST_COLUMN] + CSVDataLineArray[FOURTH_COLUMN]);
                    boolean duplicate = false;
                    if(mapPoliticians.containsKey(CSVDataLineArray[FIRST_COLUMN]))
                    {
                        int wordcount = mapPoliticians.get(CSVDataLineArray[FIRST_COLUMN]);
                        wordcount += Integer.parseInt(CSVDataLineArray[FOURTH_COLUMN]);
                        duplicate = true;
                        mapPoliticians.put(CSVDataLineArray[FIRST_COLUMN], wordcount);
                    }
                    if(!duplicate)
                         mapPoliticians.put(CSVDataLineArray[FIRST_COLUMN], Integer.parseInt(CSVDataLineArray[FOURTH_COLUMN]));
                }

            }
            if(!mapPoliticians.isEmpty()) {

                //Optional<Map.Entry<String, Integer>> maxEntry = mapPoliticians.entrySet().stream().max(Comparator.comparing(Map.Entry::getValue));
                maxSpeechesForYearPolitik = mapPoliticians.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                String min = mapPoliticians.entrySet().stream().min((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
                System.out.println("Politician with maximum speeches in given year is " + maxSpeechesForYearPolitik);
                System.out.println("Politician with minimum speeches in given year is " + min);
            }


            findPoliticianWithMinimumWordsInAllSpeeches();

            String politicianWithMaximumSpeechegivenTopic = findPoliticianWithMaximumSpeechesForGivenTopic(mostSpokenThema);

            String politicanWithleastwordy = findPoliticianWithMinimumWordsInAllSpeeches();

            responseDto = ResponseDto.builder().mostSpeeches(maxSpeechesForYearPolitik).mostSecurity(politicianWithMaximumSpeechegivenTopic).leastWordy(politicanWithleastwordy).build();
            System.out.println(responseDto);
            String recievedArray[][] = readCSVFileIntoArray();
            System.out.println("Printing the data from two dimensional array" );
            while()
            {}
            

            for (int i = 0; i < recievedArray.length; i++) {
                for (int j = 0; j < recievedArray[i].length; j++) {
                    System.out.print(recievedArray[i][j] + " ");
                }
                System.out.println("");
            }

        }
        catch (IOException e) {
            e.printStackTrace();
        }
        return responseDto;

    }

    public String[][] readCSVFileIntoArray() throws IOException {
        CSVReader reader = null;

        reader = new CSVReaderBuilder(new FileReader(CSVDatafileName)).withSkipLines(FIRSTLINE).build();
        String[][] CSVDataLineArray = new String[LINE_COUNT][COLUMN_COUNT];
        DateTimeFormatter df = DateTimeFormatter .ofPattern(DATE_PATTERN, Locale.forLanguageTag(LOCALE));
        for (int lineno = 0 ; lineno < LINE_COUNT; i++)
        {
            CSVDataLineArray[lineno] = reader.readNext();
        }

        return CSVDataLineArray;

    }

    public String findPoliticianWithMinimumWordsInAllSpeeches() throws IOException {

        CSVReader reader = null;
        //Skip the header line
        reader = new CSVReaderBuilder(new FileReader(CSVDatafileName)).withSkipLines(FIRSTLINE).build();
        String[] line;
        Map<String, Integer> mapPoliticians = new HashMap<>();

        DateTimeFormatter df = DateTimeFormatter.ofPattern(DATE_PATTERN, Locale.forLanguageTag(LOCALE));
        //Collect an a
        while ((line = reader.readNext()) != null) {
            int year1 = LocalDate.parse(line[THIRD_COLUMN], df).atStartOfDay().getYear();
            System.out.println(line[FIRST_COLUMN] + line[FOURTH_COLUMN]);
            boolean duplicate = false;
            if (mapPoliticians.containsKey(line[FIRST_COLUMN])) {
                int wordcount = mapPoliticians.get(line[FIRST_COLUMN]);
                wordcount += Integer.parseInt(line[3]);
                duplicate = true;
                mapPoliticians.put(line[FIRST_COLUMN], wordcount);
            }
            if (!duplicate)
                mapPoliticians.put(line[FIRST_COLUMN], Integer.parseInt(line[3]));
        }

        String min = null;

        if (!mapPoliticians.isEmpty()) {

            min = mapPoliticians.entrySet().stream().min((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            System.out.println("Politician with minimum words spoken overall is " + min);
        }

        return min;
    }



    public String findPoliticianWithMaximumSpeechesForGivenTopic(String Topic) throws IOException {
        //String is politician and Integer will be total number of words spoken on a given topic.
        Map<String, Integer> maxSpeechesPoliticianForATheme = new HashMap<>();
        CSVReader reader = null;
        //Skip the header line
        reader = new CSVReaderBuilder(new FileReader(CSVDatafileName)).withSkipLines(FIRSTLINE).build();
        String[] line;
        DateTimeFormatter df = DateTimeFormatter .ofPattern("yyyy-MM-dd", Locale.forLanguageTag("Locale.ENGLISH"));
        System.out.println("The given topic is " + Topic);
        String max = null;
        while ((line = reader.readNext()) != null ) {

            System.out.println("inside while loop");
            if(Topic.equals(line[1])) {
                System.out.println("inside first if");
                if (!maxSpeechesPoliticianForATheme.containsKey(line[FIRST_COLUMN]))
                    maxSpeechesPoliticianForATheme.put(line[FIRST_COLUMN], Integer.parseInt(line[3]));
                else {
                    int wordcount = maxSpeechesPoliticianForATheme.get(line[FIRST_COLUMN]);
                    wordcount += Integer.parseInt(line[3]);
                    maxSpeechesPoliticianForATheme.put(line[FIRST_COLUMN], wordcount);
                }
            }
        }
        if(!maxSpeechesPoliticianForATheme.isEmpty()) {
            max = maxSpeechesPoliticianForATheme.entrySet().stream().max((entry1, entry2) -> entry1.getValue() > entry2.getValue() ? 1 : -1).get().getKey();
            System.out.println("Politician with maximum speeches on given topic  " + Topic + "is  " + max);
        }

        return max;

    }

}
