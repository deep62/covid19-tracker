package io.javabrains.coronavirustracker.services;

import io.javabrains.coronavirustracker.models.DeathCases;
import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.models.ReoveredCase;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVRecord;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import javax.annotation.PostConstruct;
import java.io.IOException;
import java.io.StringReader;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;

@Service
public class CoronaVirusDataService {


    List<String> urls = new ArrayList<>();
    private static String VIRUS_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_confirmed_global.csv";
    private static String DEATH_DATA_URL = "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_deaths_global.csv";
    private static String RECOVERED_DATA_URL= "https://raw.githubusercontent.com/CSSEGISandData/COVID-19/master/csse_covid_19_data/csse_covid_19_time_series/time_series_covid19_recovered_global.csv";
    static int count = 0;
    private List<LocationStats> allStats = new ArrayList<>();
    private List<DeathCases> allDeathStats = new ArrayList<>();
    private List<ReoveredCase> allRecoveredStats = new ArrayList<>();
    private ExecutorService executor = Executors.newFixedThreadPool(6);
    private List<Future<?>> futures = new ArrayList<>();
    Iterable<CSVRecord> recordsForToalCovidCases = null;
    Iterable<CSVRecord> recordsForToalDeathCovidCases = null;
    Iterable<CSVRecord> recordsForRecoveredCovidCases = null;

    @PostConstruct
    @Scheduled(cron = "0 0 * * * *")
    public void fetchVirusData() throws IOException, InterruptedException {
        urls.add(VIRUS_DATA_URL);
        urls.add(DEATH_DATA_URL);

        List<LocationStats> newStats = new ArrayList<>();
        List<DeathCases> newDeathStats = new ArrayList<>();
        List<ReoveredCase> newRecoveredStats = new ArrayList<>();
        System.out.println("count::" + count++);


        Future covidDataResponseFuture = executor.submit(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(VIRUS_DATA_URL))
                    .build();
            HttpResponse<String> httpResponse = null;
            try {
                httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StringReader csvBodyReader = new StringReader(httpResponse.body());

            try {
                recordsForToalCovidCases = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        Future covidDeathDataResponseFuture = executor.submit(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(DEATH_DATA_URL))
                    .build();
            HttpResponse<String> httpResponse = null;
            try {
                httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StringReader csvBodyReader = new StringReader(httpResponse.body());
            try {
                recordsForToalDeathCovidCases = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        Future covidRecoveredDataResponseFuture = executor.submit(() -> {
            HttpClient client = HttpClient.newHttpClient();
            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RECOVERED_DATA_URL))
                    .build();
            HttpResponse<String> httpResponse = null;
            try {
                httpResponse = client.send(request, HttpResponse.BodyHandlers.ofString());
            } catch (IOException e) {
                e.printStackTrace();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            StringReader csvBodyReader = new StringReader(httpResponse.body());
            try {
                recordsForRecoveredCovidCases = CSVFormat.DEFAULT.withFirstRecordAsHeader().parse(csvBodyReader);
            } catch (IOException e) {
                e.printStackTrace();
            }

        });

        futures.add(covidDataResponseFuture);
        futures.add(covidDeathDataResponseFuture);
        futures.add(covidDeathDataResponseFuture);
        futures.forEach(f -> {
            try {
                f.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        });

        if(null!=recordsForToalCovidCases && null!=recordsForToalDeathCovidCases)
        {
            for (CSVRecord record : recordsForToalCovidCases) {
                LocationStats locationStats = new LocationStats();
                locationStats.setState(record.get("Province/State"));
                locationStats.setCountry(record.get("Country/Region"));
                int latestCases = Integer.parseInt(record.get(record.size() - 1));
                int previousDayCases = Integer.parseInt(record.get(record.size() - 2));
                locationStats.setLatestTotalCases(latestCases);
                locationStats.setDiffFromThePreviousDay(latestCases - previousDayCases);
                int latestDeathCases = Integer.parseInt(record.get(record.size() - 1));
                int previousDayDeathCases = Integer.parseInt(record.get(record.size() - 2));
                //System.out.println(locationStats);
                newStats.add(locationStats);
            }
            for(CSVRecord record : recordsForToalDeathCovidCases)
            {
                DeathCases deathCases = new DeathCases();
                int totalDeathCases = Integer.parseInt(record.get(record.size()-1));
                int previousDayDeathCases = Integer.parseInt(record.get(record.size()-2));
                deathCases.setLatestTotalDeath(totalDeathCases);
                deathCases.setDiffOfDeathFromThePreviousDay(totalDeathCases-previousDayDeathCases);
                newDeathStats.add(deathCases);

            }
            for(CSVRecord record : recordsForRecoveredCovidCases)
            {
                ReoveredCase recovereCases = new ReoveredCase();
                int totalRecoveredCases = Integer.parseInt(record.get(record.size()-1));
                int previousDayRecoveredCases = Integer.parseInt(record.get(record.size()-2));
                recovereCases.setTotalRecoveredCase(totalRecoveredCases);
                recovereCases.setDiffOfRecoveredFromThePreviousDay(totalRecoveredCases-previousDayRecoveredCases);
                newRecoveredStats.add(recovereCases);

            }

        }

        this.allStats = newStats;
        this.allDeathStats = newDeathStats;
        this.allRecoveredStats = newRecoveredStats;

    }

    public List<LocationStats> getAllStats() {
        return allStats;
    }

    public List<DeathCases> getAllDeathStats() {
        return allDeathStats;
    }

    public List<ReoveredCase> getAllRecoveredStats() {
        return allRecoveredStats;
    }
}

