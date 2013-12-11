package groovyx.net.http;

import org.apache.http.auth.AuthScheme;
import org.apache.http.auth.AuthSchemeFactory;
import org.apache.http.params.HttpParams;

public class OAuthSchemeFactory implements AuthSchemeFactory {
	public AuthScheme newInstance( HttpParams params ) {
		return new OAuthScheme( params );
	}
}