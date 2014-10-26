package com.baidu.bkm.wiki.macro.markdown;

import java.util.Map;

import org.owasp.validator.html.AntiSamy;
import org.owasp.validator.html.CleanResults;
import org.owasp.validator.html.Policy;
import org.pegdown.Extensions;
import org.pegdown.PegDownProcessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.plugin.webresource.WebResourceManager;

/**
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Sep 9, 2014 5:32:53 PM
 */
@SuppressWarnings("deprecation")
public class MarkdownMacro implements Macro {
	
	private static final Logger			logger			= LoggerFactory.getLogger(MarkdownMacro.class);
	private final WebResourceManager	webResourceManager;
	private final String				POLICY_FILE		= "antisamy-ebay-1.4.4.xml";
	private final String				MARKDOWN_CLASS	= "bkm-wiki-macro-markdown";
	private final AntiSamy				antiSamy;
	
	public MarkdownMacro(WebResourceManager webResourceManager) throws Exception {
		this.webResourceManager = webResourceManager;
		Policy policy = Policy.getInstance(getClass().getClassLoader().getResourceAsStream(POLICY_FILE));
		this.antiSamy = new AntiSamy(policy);
	}
	
	@Override
	public BodyType getBodyType() {
		return BodyType.PLAIN_TEXT;
	}
	
	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}
	
	@Override
	public String execute(Map<String, String> parameters, String body, ConversionContext conversionContext)
			throws MacroExecutionException {
		webResourceManager.requireResourcesForContext("com.baidu.bkm.wiki.macro.markdown:md-styles");
		try {
			body = markdownToHtml(body);
			return "<div class=\"" + MARKDOWN_CLASS + "\">" + body + "</div>";
		} catch (Exception e) {
			logger.error("Can't renderer the markdown[" + body + "] to html!", e);
			return "<div id=\"all-messages\"><div class=\"aui-message error\">"
					+ "<p>Renderer Error</p><span class=\"aui-icon icon-close\" role=\"button\"></span></div></div>";
		}
	}
	
	private String markdownToHtml(String markdown) throws Exception {
		if (markdown == null) {
			return "";
		}
		// markdown = StringEscapeUtils.escapeHtml(markdown);
		// Markdown4jProcessor processor = new Markdown4jProcessor();
		// return processor.process(markdown);
		// MarkdownProcessor mp = new MarkdownProcessor();
		// return mp.markdown(markdown);
		PegDownProcessor pp = new PegDownProcessor(Extensions.ALL);
		String h = pp.markdownToHtml(markdown);
		return post(h);
	}
	
	private String post(String html) throws Exception {
		CleanResults s = antiSamy.scan(html);
		return s.getCleanHTML().trim();
	}
}