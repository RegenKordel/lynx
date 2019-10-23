package openreq.qt.qthulhu.jira.webwork;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.atlassian.jira.web.action.JiraWebActionSupport;
import javax.inject.Inject;
import javax.inject.Named;
import com.atlassian.webresource.api.assembler.PageBuilderService;

@Named
public class ErrorPageAction extends JiraWebActionSupport
{
    private static final Logger log = LoggerFactory.getLogger(ErrorPageAction.class);

    @Inject
    private PageBuilderService pageBuilderService;

    @Override
    public String execute() throws Exception {
        pageBuilderService.assembler().resources().requireWebResource(
                "openreq.qt.issuelinkmap-jira-plugin.issuelinkmap-jira-plugin:error-page-controller"
        );

        return "success";
    }

    public void setPageBuilderService(PageBuilderService pageBuilderService) {
        this.pageBuilderService = pageBuilderService;
    }
}