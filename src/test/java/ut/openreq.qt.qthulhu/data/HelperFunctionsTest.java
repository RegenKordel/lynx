package ut.openreq.qt.qthulhu.data;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import openreq.qt.qthulhu.data.HelperFunctions;
import openreq.qt.qthulhu.data.LayerDepthChecker;
import org.junit.Test;

import static org.junit.Assert.assertTrue;

public class HelperFunctionsTest{

    @Test
    public void cleanTextTest()
    {
        JsonObject testJsonText = new JsonObject();
        testJsonText.addProperty("description", "This is\r\n a\r text description!!!?.");
        String cleanText = HelperFunctions.cleanText(testJsonText.get("description"));
        assertTrue("Wrong Text", cleanText.equals("This is a text description! ! ! ? . "));
    }

    @Test
    public void cleanEmptyTextTest()
    {
        JsonObject testJsonText = new JsonObject();
        String cleanText = HelperFunctions.cleanText(testJsonText.get("description"));
        System.out.println(cleanText);
        assertTrue("Wrong Text", cleanText.equals("none"));
    }

    @Test
    public void checkFillPartsTest()
    {
        JsonArray parts = new JsonArray();
        HelperFunctions.fillParts(parts, "placeholder");
        assertTrue("Still empty", !parts.equals(null));
    }

    @Test
    public void checkTooLowLayerTest()
    {
        assertTrue("should be 1", LayerDepthChecker.checkForValidLayerDepth(-1, 1) == 1);
    }

    @Test
    public void checkTooHighLayerTest()
    {
        assertTrue("should be 5", LayerDepthChecker.checkForValidLayerDepth(56, 1) == 5);
    }

    @Test
    public void checknullLayerTest()
    {
        assertTrue("should be 1", LayerDepthChecker.checkForValidLayerDepth(null, 3) == 1);
    }

    @Test
    public void checkCorrectLayerTest()
    {
        assertTrue("should be 3", LayerDepthChecker.checkForValidLayerDepth(4, -1) == 3);
    }
}