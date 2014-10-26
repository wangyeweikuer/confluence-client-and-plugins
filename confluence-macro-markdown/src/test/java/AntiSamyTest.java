import java.net.MalformedURLException;
import java.net.URL;

import org.junit.Test;
import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.owasp.validator.html.PolicyException;
import org.owasp.validator.html.ScanException;

public class AntiSamyTest {
	
	@Test
	public void test() throws PolicyException, ScanException, MalformedURLException {
		AntiSamy as = new AntiSamy();
		URL uri = getClass().getClassLoader().getResource("antisamy-ebay-1.4.4.xml");
		Policy policy = Policy.getInstance(uri);
		CleanResults s = as.scan("xxxxx<script>alert('xx');</script>hhhh", policy);
		System.err.println(s.getCleanHTML());
	}
}
