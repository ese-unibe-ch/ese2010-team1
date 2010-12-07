import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.Map;

import org.apache.commons.httpclient.Header;
import org.junit.Before;
import org.junit.Test;

import play.libs.WS;
import play.libs.WS.FileParam;
import play.libs.WS.HttpResponse;
import play.test.UnitTest;

import com.google.gson.JsonObject;


public class RestTest extends UnitTest {
    static Map<String, Object> params;

    @Before
    public void setUp() {
        params = new HashMap<String, Object>();
        params.put("timestamp", 1200000L);
        params.put("cachable", true);
        params.put("multipleValues", new String[]{"欢迎", "dobrodošli", "ยินดีต้อนรับ"});
    }

    @Test
    public void testGet() {
        assertEquals("对!", WS.url("http://localhost:9003/ressource/%s", "ééééééçççççç汉语漢語").get().getString());
    }

    @Test
    public void testHead() {
        HttpResponse headResponse = WS.url("http://localhost:9003/ressource/%s", "ééééééçççççç汉语漢語").head();
        Header[] headResponseHeaders = headResponse.getHeaders();
        assertNull(headResponse.getString());
        HttpResponse getResponse = WS.url("http://localhost:9003/ressource/%s", "ééééééçççççç汉语漢語").get();
        Header[] getResponseHeaders = getResponse.getHeaders();
        for (int i = 0; i < getResponseHeaders.length; i++) {
            if (!"Set-Cookie".equals(getResponseHeaders[i].getName())) {
                assertEquals(getResponseHeaders[i].getValue(), headResponseHeaders[i].getValue());
            }
        }
    }

    @Test
    public void testPost() throws UnsupportedEncodingException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("id", 101);
        assertEquals(jsonResponse.toString(), WS.url("http://localhost:9003/ressource/%s", "名字").params(params).post().getJson().toString());
        File fileToSend = new File(new URLDecoder().decode(getClass().getResource("/kiki.txt").getFile(), "utf-8"));
        assertTrue(fileToSend.exists());
        assertEquals("POSTED!", WS.url("http://localhost:9003/ressource/file/%s", "名字").files(new FileParam(fileToSend, "file")).post().getString());
        assertEquals("FILE AND PARAMS POSTED!", WS.url("http://localhost:9003/ressource/fileAndParams/%s", "名字").files(new FileParam(fileToSend, "file")).params(params).post().getString());
    }

    @Test
    public void testPut() throws UnsupportedEncodingException {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("id", 101);
        assertEquals(jsonResponse.toString(), WS.url("http://localhost:9003/ressource/%s", "名字").params(params).put().getJson().toString());
        File fileToSend = new File(new URLDecoder().decode(getClass().getResource("/kiki.txt").getFile(), "utf-8"));
        assertTrue(fileToSend.exists());
        assertEquals("POSTED!", WS.url("http://localhost:9003/ressource/file/%s", "名字").files(new FileParam(fileToSend, "file")).put().getString());
        assertEquals("FILE AND PARAMS POSTED!", WS.url("http://localhost:9003/ressource/fileAndParams/%s", "名字").files(new FileParam(fileToSend, "file")).params(params).put().getString());
    }

    @Test
    public void testMethodOverride() {
        JsonObject jsonResponse = new JsonObject();
        jsonResponse.addProperty("id", 101);
        assertEquals(jsonResponse.toString(), WS.url("http://localhost:9003/ressource/%s", "名字").setHeader("X-HTTP-Method-Override", "POST").params(params).get().getJson().toString());
    }
}
