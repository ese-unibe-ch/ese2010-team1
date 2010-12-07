package play.mvc;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.Serializable;
import java.lang.reflect.Method;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import play.Logger;
import play.exceptions.UnexpectedException;
import play.libs.Codec;
import play.libs.Time;
import play.utils.Utils;

/**
 * HTTP interface
 */
public class Http {

    /**
     * An HTTP Header
     */
    public static class Header implements Serializable {

        /**
         * Header name
         */
        public String name;
        /**
         * Header value
         */
        public List<String> values;

        /**
         * First value
         * @return The first value
         */
        public String value() {
            return values.get(0);
        }
    }

    /**
     * An HTTP Cookie
     */
    public static class Cookie implements Serializable {

        /**
         * Cookie name
         */
        public String name;
        /**
         * Cookie domain
         */
        public String domain;
        /**
         * Cookie path
         */
        public String path = "/";
        /**
         * for HTTPS ?
         */
        public boolean secure = false;
        /**
         * Cookie value
         */
        public String value;
        /**
         * Cookie max-age
         */
        public Integer maxAge;
        /**
         * Don't use
         */
        public boolean sendOnError = false;
    }

    /**
     * An HTTP Request
     */
    public static class Request implements Serializable {

        /**
         * Server host
         */
        public String host;
        /**
         * Request path
         */
        public String path;
        /**
         * QueryString
         */
        public String querystring;
        /**
         * Full url
         */
        public String url;
        /**
         * HTTP method
         */
        public String method;
        /**
         * Server domain
         */
        public String domain;
        /**
         * Client address
         */
        public String remoteAddress;
        /**
         * Request content-type
         */
        public String contentType;
        /**
         * Controller to invoke
         */
        public String controller;
        /**
         * Action method name
         */
        public String actionMethod;
        /**
         * HTTP port
         */
        public Integer port;
        /**
         * is HTTPS ?
         */
        public Boolean secure = false;
        /**
         * HTTP Headers
         */
        public Map<String, Http.Header> headers = new HashMap<String, Http.Header>();
        /**
         * HTTP Cookies
         */
        public Map<String, Http.Cookie> cookies = new HashMap<String, Http.Cookie>();
        /**
         * Body stream
         */
        public transient InputStream body;
        /**
         * Additinal HTTP params extracted from route
         */
        public Map<String, String> routeArgs;
        /**
         * Format (html,xml,json,text)
         */
        public String format = null;
        /**
         * Full action (ex: Application.index)
         */
        public String action;
        /**
         * Bind to thread
         */
        public static ThreadLocal<Request> current = new ThreadLocal<Request>();
        /**
         * The really invoker Java methid
         */
        public transient Method invokedMethod;
        /**
         * Free space to store your request specific data
         */
        public Map<String, Object> args = new HashMap<String, Object>();
        /**
         * When the request has been received
         */
        public Date date = new Date();
        /**
         * New request or already submitted
         */
        public boolean isNew = true;
        /**
         * HTTP Basic User
         */
        public String user;
        /**
         * HTTP Basic Password
         */
        public String password;
        /**
         * Request comes from loopback interface
         */
        public boolean isLoopback;

        public void _init() {
            Header header = headers.get("authorization");
            if(header != null && header.value().startsWith("Basic ")) {
                String data = header.value().substring(6);
                String[] decodedData = new String(Codec.decodeBASE64(data)).split(":");
                user = decodedData.length > 0 ? decodedData[0] : null;
                password = decodedData.length > 1 ? decodedData[1] : null;
            }
        }

        /**
         * Automatically resolve request format from the Accept header
         * (in this order : html > xml > json > text)
         */
        public void resolveFormat() {

            if (format != null) {
                return;
            }

            if (headers.get("accept") == null) {
                format = "html".intern();
                return;
            }

            String accept = headers.get("accept").value();

            if (accept.indexOf("application/xhtml") != -1 || accept.indexOf("text/html") != -1 || accept.startsWith("*/*")) {
                format = "html".intern();
                return;
            }

            if (accept.indexOf("application/xml") != -1 || accept.indexOf("text/xml") != -1) {
                format = "xml".intern();
                return;
            }

            if (accept.indexOf("text/plain") != -1) {
                format = "txt".intern();
                return;
            }

            if (accept.indexOf("application/json") != -1 || accept.indexOf("text/javascript") != -1) {
                format = "json".intern();
                return;
            }

            if (accept.endsWith("*/*")) {
                format = "html".intern();
                return;
            }
        }

        /**
         * Retrieve the current request
         * @return the current request
         */
        public static Request current() {
            return current.get();
        }
        
        /**
         * This request was sent by an Ajax framework.
         * (rely on the X-Requested-With header).
         */
        public boolean isAjax() {
            if(!headers.containsKey("x-requested-with")) {
                return false;
            }
            return "XMLHttpRequest".equals(headers.get("x-requested-with").value());
        }

        /**
         * Get the request base (ex: http://localhost:9000
         * @return the request base of the url (protocol, host and port)
         */
        public String getBase() {
            if (port == 80 || port == 443) {
                return String.format("%s://%s", secure ? "https" : "http", domain).intern();
            }
            return String.format("%s://%s:%s", secure ? "https" : "http", domain, port).intern();
        }

        @Override
        public String toString() {
            return method + " " + path + (querystring != null && querystring.length() > 0 ? "?" + querystring : "");
        }

        /**
         * Return the languages requested by the browser, ordered by preference (preferred first).
         * If no Accept-Language header is present, an empty list is returned.
         */
        public List<String> acceptLanguage() {
            final Pattern qpattern = Pattern.compile("q=([0-9\\.]+)");
            if (!headers.containsKey("accept-language")) return new ArrayList<String>();
            String acceptLanguage = headers.get("accept-language").value();
            List<String> languages = Arrays.asList(acceptLanguage.split(","));
            Collections.sort(languages, new Comparator<String>(){
                public int compare(String lang1, String lang2) {
                    double q1 = 1.0;
                    double q2 = 1.0;
                    Matcher m1 = qpattern.matcher(lang1);
                    Matcher m2 = qpattern.matcher(lang2);
                    if (m1.find()) q1 = Double.parseDouble(m1.group(1));
                    if (m2.find()) q2 = Double.parseDouble(m2.group(1));
                    return (int)(q2 - q1);
                }});
            List<String> result = new ArrayList<String>();
            for (String lang: languages) {
                result.add(lang.split(";")[0]);
            }
            return result;
        }

        public boolean isModified(String etag, long last) {
            if (!(headers.containsKey("if-none-match") && headers.containsKey("if-modified-since"))) {
                return true;
            } else {
                String browserEtag = headers.get("if-none-match").value();
                if (!browserEtag.equals(etag)) {
                    return true;
                } else {
                    try {
                        Date browserDate = Utils.getHttpDateFormatter().parse(headers.get("if-modified-since").value());
                        if (browserDate.getTime() >= last) {
                            return false;
                        }
                    } catch (ParseException ex) {
                        Logger.error("Can't parse date", ex);
                    }
                    return true;
                }
            }
        }
    }

    /**
     * An HTTP response
     */
    public static class Response {

        /**
         * Response status code
         */
        public Integer status = 200;
        /**
         * Response content type
         */
        public String contentType;
        /**
         * Response headers
         */
        public Map<String, Http.Header> headers = new HashMap<String, Header>();
        /**
         * Response cookies
         */
        public Map<String, Http.Cookie> cookies = new HashMap<String, Cookie>();
        /**
         * Response body stream
         */
        public ByteArrayOutputStream out;
        /**
         * Send this file directly
         */
        public Object direct;
        /**
         * Bind to thread
         */
        public static ThreadLocal<Response> current = new ThreadLocal<Response>();

        /**
         * Retrieve the current response
         * @return the current response
         */
        public static Response current() {
            return current.get();
        }

        /**
         * Get a response header
         * @param name Header name case-insensitive
         * @return the header value as a String
         */
        public String getHeader(String name) {
            for(String key : headers.keySet()) {
                if(key.toLowerCase().equals(name.toLowerCase())) {
                    if(headers.get(key) != null) {
                        return headers.get(key).value();
                    }
                }
            }
            return null;
        }

        /**
         * Set a response header
         * @param name Header name
         * @param value Header value
         */
        public void setHeader(String name, String value) {
            Header h = new Header();
            h.name = name;
            h.values = new ArrayList<String>();
            h.values.add(value);
            headers.put(name, h);
        }


        /**
         * Set a new cookie
         * @param name Cookie name
         * @param value Cookie value
         */
        public void setCookie(String name, String value) {
            setCookie(name, value, (Integer) null);
        }

        public void removeCookie(String name) {
            setCookie(name, "", 0);
        }

        /**
         * Set a new cookie that will expire in (current) + duration
         * @param name
         * @param value
         * @param duration Ex: 3d
         */
        public void setCookie(String name, String value, String duration) {
            int expire = Time.parseDuration(duration);
            setCookie(name, value, Integer.valueOf(expire));
        }

        public void setCookie(String name, String value, Integer maxAge) {
            setCookie(name, value, null, "/", maxAge);
        }

        public void setCookie(String name, String value, String domain, String path, String duration) {
            int expire = Time.parseDuration(duration);
            setCookie(name, value, domain, path, Integer.valueOf(expire));
        }

        public void setCookie(String name, String value, String domain, String path, Integer maxAge) {
            if (cookies.containsKey(name) && cookies.get(name).path.equals(path) && ((cookies.get(name).domain == null && domain == null) || (cookies.get(name).domain.equals(domain)))) {
                cookies.get(name).value = value;
                if (maxAge != null) {
                    cookies.get(name).maxAge = maxAge;
                }
            } else {
                Cookie cookie = new Cookie();
                cookie.name = name;
                cookie.value = value;
                cookie.path = path;
                if (domain != null) {
                    cookie.domain = domain;
                }
                if (maxAge != null) {
                    cookie.maxAge = maxAge;
                }
                cookies.put(name, cookie);
            }
        }

        /**
         * Add a cache-control header 
         * @param duration Ex: 3h
         */
        public void cacheFor(String duration) {
            int maxAge = Time.parseDuration(duration);
            setHeader("Cache-Control", "max-age=" + maxAge);
        }

        /**
         * Add cache-control headers
         * @param duration Ex: 3h
         */
        public void cacheFor(String etag, String duration, long lastModified) {
            int maxAge = Time.parseDuration(duration);
            setHeader("Cache-Control", "max-age=" + maxAge);
            setHeader("Last-Modified", Utils.getHttpDateFormatter().format(new Date(lastModified)));
            setHeader("Etag", etag);
        }
        /**
         * Add headers to allow cross-domain requests. Be careful, a lot of browsers don't support
         * these features and will ignore the headers. Refer to the browsers' documentation to
         * know what versions support them.
         * @param allowOrigin a comma separated list of domains allowed to perform the x-domain call, or "*" for all.
         */
        public void accessControl(String allowOrigin) {
            accessControl(allowOrigin, null, false);
        }

        /**
         * Add headers to allow cross-domain requests. Be careful, a lot of browsers don't support
         * these features and will ignore the headers. Refer to the browsers' documentation to
         * know what versions support them.
         * @param allowOrigin a comma separated list of domains allowed to perform the x-domain call, or "*" for all.
         * @param allowCredentials Let the browser send the cookies when doing a x-domain request. Only respected by the browser if allowOrigin != "*"
         */
        public void accessControl(String allowOrigin, boolean allowCredentials) {
            accessControl(allowOrigin, null, allowCredentials);
        }

        /**
         * Add headers to allow cross-domain requests. Be careful, a lot of browsers don't support
         * these features and will ignore the headers. Refer to the browsers' documentation to
         * know what versions support them.
         * @param allowOrigin a comma separated list of domains allowed to perform the x-domain call, or "*" for all.
         * @param allowMethods a comma separated list of HTTP methods allowed, or null for all.
         * @param allowCredentials Let the browser send the cookies when doing a x-domain request. Only respected by the browser if allowOrigin != "*"
         */
        public void accessControl(String allowOrigin, String allowMethods, boolean allowCredentials) {
            setHeader("Access-Control-Allow-Origin", allowOrigin);
            if (allowMethods != null) {
                setHeader("Access-Control-Allow-Methods", allowMethods);
            }
            if (allowCredentials == true) {
                if (allowOrigin.equals("*")) {
                    Logger.warn("Response.accessControl: When the allowed domain is \"*\", Allow-Credentials is likely to be ignored by the browser.");
                }
                setHeader("Access-Control-Allow-Credentials", "true");
            }
        }

        public void print(Object o) {
            try {
                out.write(o.toString().getBytes("utf-8"));
            } catch (IOException ex) {
                throw new UnexpectedException("UTF-8 problem ?", ex);
            }
        }

        public void reset() {
            out.reset();
        }
    }
}
