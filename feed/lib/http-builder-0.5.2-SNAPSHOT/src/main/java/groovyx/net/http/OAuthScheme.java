/**
 * 
 */
package groovyx.net.http;

import java.util.HashMap;
import java.util.Map;

import oauth.signpost.OAuthConsumer;
import oauth.signpost.commonshttp.CommonsHttpOAuthConsumer;
import oauth.signpost.exception.OAuthException;

import org.apache.http.Header;
import org.apache.http.HeaderElement;
import org.apache.http.HttpRequest;
import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthenticationException;
import org.apache.http.auth.Credentials;
import org.apache.http.auth.MalformedChallengeException;
import org.apache.http.params.HttpParams;

/**
 * @author tnichols
 */
class OAuthScheme implements AuthScheme {
	
	public static final String SCHEME_NAME = "OAuth";
	public static final String CONSUMER_KEY = "oauth.consumerKey";
	public static final String CONSUMER_SECRET = "oauth.consumerSecret";
	public static final String ACCESS_TOKEN = "oauth.accessToken"; 
	public static final String SECRET_TOKEN = "oauth.secretToken"; 
	
	protected OAuthConsumer oauth;
	private Map<String, String> params = new HashMap<String,String>();
	private boolean complete = false;
	
	public OAuthScheme( HttpParams params ) {
		this.oauth = new CommonsHttpOAuthConsumer(
 				params.getParameter( CONSUMER_KEY ).toString(),
				params.getParameter( CONSUMER_SECRET ).toString() );
		oauth.setTokenWithSecret( 
				params.getParameter( ACCESS_TOKEN ).toString(),
				params.getParameter( SECRET_TOKEN ).toString() );
	}

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#authenticate(org.apache.http.auth.Credentials, org.apache.http.HttpRequest)
	 */
	public Header authenticate( Credentials credentials, HttpRequest request )
			throws AuthenticationException {
		// for now, ignore credentials.  Later, they should be used to store the 
		// tokens.  
		try {
			oauth.sign( request );
		}
		catch ( OAuthException ex ) {
			throw new AuthenticationException("Error signing OAuth", ex);
		}
		Header authHeader = request.getHeaders( "Authorization" )[0];
		// Signpost adds a header, but really we want the AuthSheme to add the header.
		// We could also dig into Signpost's internals to generate the signature w/o 
		// directly passing the request to be signed.
		request.removeHeader( authHeader );
		this.complete = true;
		return authHeader;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#processChallenge(org.apache.http.Header)
	 */
	public void processChallenge( Header header ) throws MalformedChallengeException {
		for ( HeaderElement e : header.getElements() )
			if ( e.getName().endsWith( "realm" ) ) this.params.put("realm", e.getValue());
	}

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#getParameter(java.lang.String)
	 */
	public String getParameter( String name ) {
		return this.params.get(name);
	}

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#getRealm()
	 */
	public String getRealm() {
		return getParameter("realm");
	}

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#getSchemeName()
	 */
	public String getSchemeName() { 
		return SCHEME_NAME;
	}

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#isComplete()
	 */
	public boolean isComplete() { return this.complete; }

	/* (non-Javadoc)
	 * @see org.apache.http.auth.AuthScheme#isConnectionBased()
	 */
	public boolean isConnectionBased() { return false; }
	
}
