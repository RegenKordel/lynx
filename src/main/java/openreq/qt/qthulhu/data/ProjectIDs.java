package openreq.qt.qthulhu.data;

/**
 * This class gives projects their corresponding unique id.
 * The purpose is to get unique integer ids for every node
 */
public class ProjectIDs
{
    //TODO: 90% sure this should be ENUM
    //TODO: Add private projects (or find a better way to get all ids, maybe use API) or a hashmap?

    //constructor for Sonarqube
    private ProjectIDs()
    {

    }

    public static int getProjectID(String project)
    {
        int id;
        switch (project)
        {
            case "RTFACT":
                id = 10;
                break;
            case "BAP":
                id = 11;
                break;
            case "BI":
                id = 12;
                break;
            case "GAP":
                id = 13;
                break;
            case "HAP":
                id = 14;
                break;
            case "JUX":
                id = 15;
                break;
            case "MAP":
                id = 16;
                break;
            case "NMAP":
                id = 17;
                break;
            case "TCAP":
                id = 18;
                break;
            case "TFSAP\t":
                id = 19;
                break;
            default:
                id = 99;
        }
        return id;
    }
}
