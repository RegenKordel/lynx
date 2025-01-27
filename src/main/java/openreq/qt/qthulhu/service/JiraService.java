/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package openreq.qt.qthulhu.service;

import com.atlassian.jira.bc.issue.search.SearchService;
import com.atlassian.jira.exception.CreateException;
import com.atlassian.jira.issue.Issue;
import com.atlassian.jira.issue.link.IssueLinkManager;
import com.atlassian.jira.issue.link.IssueLinkType;
import com.atlassian.jira.issue.link.IssueLinkTypeManager;
import com.atlassian.jira.issue.search.SearchException;
import com.atlassian.jira.issue.search.SearchResults;
import com.atlassian.jira.jql.parser.JqlParseException;
import com.atlassian.jira.jql.parser.JqlQueryParser;
import com.atlassian.jira.user.ApplicationUser;
import com.atlassian.jira.web.bean.PagerFilter;
import com.atlassian.jira.security.PermissionManager;
import com.atlassian.jira.security.plugin.ProjectPermissionKey;
import com.atlassian.plugin.spring.scanner.annotation.export.ExportAsService;
import com.atlassian.plugin.spring.scanner.annotation.imports.ComponentImport;
import com.atlassian.query.Query;
import openreq.qt.qthulhu.api.JiraApi;
import openreq.qt.qthulhu.rest.json.Dependency;
import openreq.qt.qthulhu.rest.json.Requirement;

import javax.inject.Inject;
import javax.inject.Named;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import openreq.qt.qthulhu.rest.json.JiraChecked;

/**
 *
 * @author ttlaurin
 */
@ExportAsService ({JiraApi.class})
@Named("jiraService")
public class JiraService implements JiraApi{

    private final JqlQueryParser parser;
    private final SearchService searchService;
    private final IssueLinkManager issueLinkManager;
    private final IssueLinkTypeManager issueLinkTypeManager;
    private final PermissionManager permissionManager;
    private final ProjectPermissionKey issueEditPermission = new ProjectPermissionKey("EDIT_ISSUES");

    @Inject
    public JiraService(@ComponentImport JqlQueryParser jqlQueryParser,
                       @ComponentImport SearchService searchService, @ComponentImport IssueLinkManager issueLinkManager,
                       @ComponentImport IssueLinkTypeManager issueLinkTypeManager, @ComponentImport PermissionManager permissionManager) {
        this.parser = jqlQueryParser;
        this.searchService = searchService;
        this.issueLinkManager = issueLinkManager;
        this.issueLinkTypeManager = issueLinkTypeManager;
        this.permissionManager = permissionManager;
    }
    
    @Override
    public List<Requirement> filterRequirements(List<Requirement> requirements, ApplicationUser user) throws JqlParseException, SearchException {
        List<Requirement> filtered = new ArrayList<>();

        // for each issue/requirement item, check if user has access to it 
        for (Requirement req : requirements) {
            filtered.add(filterInfo(req, user));
        }
        return requirements;
    }

    // Checks if user can access the issue/requirement. 
    // If not, create a new one with only the ID (i.e. filter out other info).
    private Requirement filterInfo(Requirement req, ApplicationUser user) throws JqlParseException, SearchException {
        if (rightsCheck(req.getId(), user)) {
            return req;
        }
        Requirement filteredReq = new Requirement();
        filteredReq.setId(req.getId());
        return req;
    }

    // Query to Jira API if user can see the issue/requirement
    private boolean rightsCheck(String reqId, ApplicationUser user) throws JqlParseException, SearchException {
        String query = "id = " + reqId;
        Query conditionQuery = parser.parseQuery(query);

        SearchResults results = searchService.search(user, conditionQuery, PagerFilter.getUnlimitedFilter());

        return results.getTotal()>0;
    }

    // Method only for testing purpose
    @Override
    public String rightsCheckGetResult(String issueId, ApplicationUser user) throws JqlParseException, SearchException {
        String query = "id = " + issueId;
        Query conditionQuery = parser.parseQuery(query);
        SearchResults results = searchService.search(user, conditionQuery, PagerFilter.getUnlimitedFilter());

        return results.getTotal() + " for user " + user;
    }



    // Sets issue links in Jira for the dependencies accepted
    @Override
    public JiraChecked setAcceptedInJira(List<Dependency> dependencies, ApplicationUser user) throws JqlParseException, SearchException, CreateException {
//        String result = "";
        List<Dependency> checked = new ArrayList<>();

        for (Dependency dep : dependencies) {
            if (dep.getStatus()!=null && (dep.getStatus().toLowerCase().equals("accepted") || dep.getStatus().toLowerCase().equals("rejected") )) {
                String query = "id = " + dep.getFromid() + " or id = " + dep.getToid();
                Query conditionQuery = parser.parseQuery(query);

                SearchResults results = searchService.search(user, conditionQuery, PagerFilter.getUnlimitedFilter());
                if (results.getIssues().size()==2) {  
                    Issue fromIssue = results.getIssues().get(0);
                    Issue toIssue = results.getIssues().get(1);
                    if (permissionManager.hasPermission(issueEditPermission, fromIssue, user) && 
                            permissionManager.hasPermission(issueEditPermission, toIssue, user)) {
                        checked.add(dep);
//                        String type = dep.getDescription().get(0);
//                        String typeCapitalized = type.substring(0, 1).toUpperCase() + type.substring(1).toLowerCase();
//                        Collection<IssueLinkType> issueTypes = issueLinkTypeManager.getIssueLinkTypesByName(typeCapitalized);
//                        if (dep.getStatus().toLowerCase().equals("accepted") && issueTypes.size()>0) {
//                            Long typeId = issueTypes.iterator().next().getId();
//                            issueLinkManager.createIssueLink(fromIssue.getId(), toIssue.getId(), typeId, 1L, user);
//                            result += "\nAdded issue link from " + dep.getFromid() + " to " + dep.getToid()
//                                    + " with type " + typeCapitalized + " (type id = " + typeId + ")";
//                        }
                    }
                }
            }
        }

//        if (result.isEmpty()) {
//            result = "No changes made";
//        }

        String result = "Jira link adding disabled in this version";

        JiraChecked results = new JiraChecked(checked, result);
        return results;
    }

}