package groovyx.net.http

import static groovyx.net.http.Method.*
import static groovyx.net.http.ContentType.*

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.Reader
import java.io.ByteArrayOutputStream
import java.lang.AssertionError
import java.net.ServerSocket
import groovy.util.XmlSlurper
import groovy.util.slurpersupport.GPathResult
import org.apache.commons.io.IOUtils
import org.apache.http.client.HttpResponseException
import org.apache.http.params.HttpConnectionParams
import org.junit.Test


public class TestTest {


	@Test
	public void testPost() {
		if ( true ) return // short circuit
		def http = new HTTPBuilder('http://www.google.com/')
		http.request(POST,'text/plain') {
			body = "<this><is>a test</is></this>"

			response.success = { resp, xml ->
				println resp
			}

			response.failure = { resp ->
				println resp.statusCode
			}
		}
	}
}
