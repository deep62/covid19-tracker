package io.javabrains.coronavirustracker.controllers;



import io.javabrains.coronavirustracker.models.DeathCases;
import io.javabrains.coronavirustracker.models.LocationStats;
import io.javabrains.coronavirustracker.models.ReoveredCase;
import io.javabrains.coronavirustracker.services.CoronaVirusDataService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    @Autowired
    CoronaVirusDataService coronaVirusDataService;

    @GetMapping("/")
    public String home(Model model)
    {
        List<LocationStats> allStats = coronaVirusDataService.getAllStats();
        List<DeathCases> allDeathStats = coronaVirusDataService.getAllDeathStats();
        List<ReoveredCase> allRecoveredStats = coronaVirusDataService.getAllRecoveredStats();
        int totalReportedCases = allStats.stream().mapToInt(stat->stat.getLatestTotalCases()).sum();
        int totalNewCases = allStats.stream().mapToInt(stat->stat.getDiffFromThePreviousDay()).sum();
        int totalDeathCases = allDeathStats.stream().mapToInt(deathStat ->deathStat.getLatestTotalDeath()).sum();
        int totalDeathCasesFromPreviousDay = allDeathStats.stream().mapToInt(deathStat ->deathStat.getDiffOfDeathFromThePreviousDay()).sum();
        // for recovered cases
        int totalRecoveredCases = allRecoveredStats.stream().mapToInt(recoveredStat->recoveredStat.getTotalRecoveredCase()).sum();
        int totalRecoveredFromLastDay = allRecoveredStats.stream().mapToInt(recoveredStat->recoveredStat.getDiffOfRecoveredFromThePreviousDay()).sum();

        int totalActiveCases = totalReportedCases - totalRecoveredCases;
        model.addAttribute("locationStats",allStats);
        model.addAttribute("totalReportedCases",totalReportedCases);
        model.addAttribute("diffFromThePreviousDay",totalReportedCases);
        model.addAttribute("totalNewCases",totalNewCases);
        model.addAttribute("totalDeathCases",totalDeathCases);
        model.addAttribute(("totalDeathCasesFromPreviousDay"),totalDeathCasesFromPreviousDay);
        model.addAttribute("totalRecoveredCases",totalRecoveredCases);
        model.addAttribute("totalRecoveredFromLastDay",totalRecoveredFromLastDay);
        model.addAttribute("totalActiveCases",totalActiveCases);

        return "home";
    }
}
