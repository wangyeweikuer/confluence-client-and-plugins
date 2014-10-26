package com.baidu.bkm.wiki.macro.includepages;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.atlassian.bonnie.Searchable;
import com.atlassian.confluence.content.render.xhtml.ConversionContext;
import com.atlassian.confluence.content.render.xhtml.Renderer;
import com.atlassian.confluence.core.ContentEntityObject;
import com.atlassian.confluence.core.service.NotAuthorizedException;
import com.atlassian.confluence.languages.LocaleManager;
import com.atlassian.confluence.macro.Macro;
import com.atlassian.confluence.macro.MacroExecutionException;
import com.atlassian.confluence.pages.AbstractPage;
import com.atlassian.confluence.pages.Page;
import com.atlassian.confluence.plugin.services.VelocityHelperService;
import com.atlassian.confluence.renderer.ContentIncludeStack;
import com.atlassian.confluence.search.service.ContentTypeEnum;
import com.atlassian.confluence.search.v2.ContentSearch;
import com.atlassian.confluence.search.v2.ISearch;
import com.atlassian.confluence.search.v2.InvalidSearchException;
import com.atlassian.confluence.search.v2.SearchFilter;
import com.atlassian.confluence.search.v2.SearchManager;
import com.atlassian.confluence.search.v2.SearchManager.EntityVersionPolicy;
import com.atlassian.confluence.search.v2.SearchQuery;
import com.atlassian.confluence.search.v2.SearchResults;
import com.atlassian.confluence.search.v2.SearchSort;
import com.atlassian.confluence.search.v2.SearchSort.Order;
import com.atlassian.confluence.search.v2.query.AllQuery;
import com.atlassian.confluence.search.v2.query.BooleanQuery;
import com.atlassian.confluence.search.v2.query.ContentTypeQuery;
import com.atlassian.confluence.search.v2.query.CreatorQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery;
import com.atlassian.confluence.search.v2.query.DateRangeQuery.DateRangeQueryType;
import com.atlassian.confluence.search.v2.query.InSpaceQuery;
import com.atlassian.confluence.search.v2.query.LabelQuery;
import com.atlassian.confluence.search.v2.searchfilter.ContentPermissionsSearchFilter;
import com.atlassian.confluence.search.v2.searchfilter.SpacePermissionsSearchFilter;
import com.atlassian.confluence.search.v2.sort.CreatedSort;
import com.atlassian.confluence.search.v2.sort.ModifiedSort;
import com.atlassian.confluence.search.v2.sort.TitleSort;
import com.atlassian.confluence.user.UserAccessor;
import com.atlassian.confluence.util.i18n.I18NBean;
import com.atlassian.confluence.util.i18n.I18NBeanFactory;
import com.atlassian.renderer.v2.macro.MacroException;
import com.atlassian.sal.api.user.UserManager;
import com.atlassian.user.User;

/**
 * 基于标签（label）将某个空间中的页面聚合到一个页面内
 * @author wangye04 笨笨
 * @email wangye04@baidu.com
 * @datetime Jul 1, 2014 11:16:27 AM
 */
public class IncludePagesMacro implements Macro {
	
	private static final Logger				log			= LoggerFactory.getLogger(IncludePagesMacro.class);
	private static final String				T_PAGELIST	= "templates/includepages-pagelist.vm";
	private static final String	yMd			= "yyyyMMdd";
	private static final int				max_labels	= 5;
	private static final int				max_authors	= 10;
	private static final int				max_results	= 100;
	private static final int				max_results_with_keywords = 500;
	private final UserAccessor userAccessor;
	private final UserManager userManager;
	private final LocaleManager localeManager;
	private final I18NBeanFactory			i18NBeanFactory;
	private final Renderer					viewRenderer;
	private final VelocityHelperService		velocityHelperService;
	private final SearchManager				searchManager;
	private static final String dateframeSeparator = "-";
	private static final Pattern recentPattern = Pattern.compile("([1-9][0-9]{0,2})([hdwmy])", Pattern.CASE_INSENSITIVE);
	private static final Pattern dateframePattern = Pattern.compile("(\\d{8})?\\s*(" + dateframeSeparator + ")\\s*(\\d{8})?");
	@SuppressWarnings("serial")
	private static final Map<Character, Integer> recentUnitMap = new HashMap<Character, Integer>() {
		{
			put('H', 1);
			put('D', 24);
			put('W', 7*24);
			put('M', 30*24);
			put('Y', 365*24);
		}
	};
	

	/**
	 * @param userAccessor
	 * @param localeManager
	 * @param i18nBeanFactory
	 * @param viewRenderer
	 * @param velocityHelperService
	 * @param searchManager
	 */
	public IncludePagesMacro(UserAccessor userAccessor, UserManager userManager,
			LocaleManager localeManager, I18NBeanFactory i18nBeanFactory,
			Renderer viewRenderer, VelocityHelperService velocityHelperService,
			SearchManager searchManager) {
		this.userAccessor = userAccessor;
		this.userManager = userManager;
		this.localeManager = localeManager;
		i18NBeanFactory = i18nBeanFactory;
		this.viewRenderer = viewRenderer;
		this.velocityHelperService = velocityHelperService;
		this.searchManager = searchManager;
	}

	private void filterDefaultParams(Map<String, String> paramMap) {
		if (StringUtils.isEmpty(paramMap.get("showparams"))) {
			paramMap.put("showparams", String.valueOf(true));
		}
		if (StringUtils.isEmpty(paramMap.get("reverse"))) {
			paramMap.put("reverse", String.valueOf(false));
		}
	}
	
	@Override
	public String execute(Map<String, String> paramMap, String paramString, ConversionContext conversionContext)
			throws MacroExecutionException {
		try {
			// 校验循环包含
			mustInPageContext(conversionContext);
			ContentEntityObject ceo = conversionContext.getEntity();
			boolean popRequired = false;
			if (ceo instanceof Page) {// 因为只展示对应的页面，所以只需要关注当前运行的页面是否被包含进来就行，如果当前是博客，则肯定不会包含进来
				if (ContentIncludeStack.contains(ceo)) {
					throw new MacroException(getText("error.includeself"));
				}
				ContentIncludeStack.push(ceo);
				popRequired = true;
			}
			try {
				return getResult(paramMap, conversionContext);
			} finally {
				if (popRequired) {
					ContentIncludeStack.pop();
				}
			}
		} catch (NotAuthorizedException e) {
			log.error(IncludePagesMacro.class + ".execute() failed!", e);
			return renderMessage(paramMap, getText("error.tip") + getText("error.pagenotfound"));
		} catch (Exception e) {
			log.error(IncludePagesMacro.class + ".execute() failed!", e);
			return renderMessage(paramMap, getText("error.error") + e.getMessage());
		}
	}
	
	private String getResult(Map<String, String> paramMap, ConversionContext conversionContext) throws InvalidSearchException {
		String currentSpaceKey = conversionContext.getSpaceKey();
		filterDefaultParams(paramMap);
		List<Map<String, Object>> pages = doSearch(paramMap, currentSpaceKey);
		String errmsg = "";
		if (pages.size() == 0 && StringUtils.isEmpty(errmsg)) {
			errmsg = getText("error.tip") + getText("error.pagenotfound");
		}
		Map<String, Object> contextMap = velocityHelperService.createDefaultVelocityContext();
		contextMap.put("errmsg", errmsg);
		contextMap.put("showparams", paramMap.get("showparams"));
		contextMap.put("info", getInfo(paramMap));
		contextMap.put("pages", pages);
		contextMap.put("displayType", getDisplayType(paramMap));
		return velocityHelperService.getRenderedTemplate(T_PAGELIST, contextMap);
	}
	
	private String renderMessage(Map<String, String> paramMap, String msg) {
		String info = "";
		if (paramMap.get("showparams").equals("true")) {
			info = getInfo(paramMap) + "<br />";
		}
		return "<div class=\"aui-message\">" + info + "<font color=\"red\">" + msg + "</font></div>";
	}
	/**
	 * 构造过滤条件信息
	 * @param paramMap
	 * @return
	 */
	private String getInfo(Map<String, String> paramMap) {
		String spaceKey = paramMap.get("space");
		StringBuilder info = new StringBuilder(getText("info.spaceWrapper", StringUtils.isEmpty(spaceKey) ?
				getText("info.currentSpace") : getText("info.specificSpace", spaceKey)));
		if (!StringUtils.isEmpty(paramMap.get("dateframevm"))) {
			info.append(paramMap.get("dateframevm"));
		}
		info.append(getText("info.labels", paramMap.get("labelNames")));
		if (!StringUtils.isEmpty(paramMap.get("authors"))) {
			info.append(getText("info.authors", paramMap.get("authors")));
		}
		if (!StringUtils.isEmpty(paramMap.get("keywords"))) {
			info.append(getText("info.keywords", paramMap.get("keywords")));
		}
		info.append(getText("info.end"));
		return info.toString();
	}

	/**
	 * 必须在空间中，在页面或者博客中使用
	 * @param cctx
	 */
	private void mustInPageContext(ConversionContext cctx) {
		ContentEntityObject e = cctx.getEntity();
		if (cctx.getSpaceKey() == null || e == null || !(e instanceof AbstractPage)) {
			throw new IllegalArgumentException(getText("error.mustUsedInPageOrBlogpost"));
		}
	}
	
	private String getText(String key, Object... params) {
		User user = userAccessor.getUserByKey(userManager.getRemoteUserKey());
		I18NBean i18n = i18NBeanFactory.getI18NBean(localeManager.getLocale(user));
		String prefix = "com.baidu.bkm.wiki.macro.bkm-macros.includepages.";
		return i18n.getText(prefix + key, params);
	}
	
	private String getDisplayType(Map<String, String> paramMap) {
		String s = paramMap.get("displayType");
		if ("entire".equals(s)) {
			return "entire";
		}
		return "title";
	}
	
	/**
	 * 执行搜索
	 * @param paramMap
	 * @param currentSpaceKey 当前空间
	 * @return
	 * @throws InvalidSearchException
	 */
	private List<Map<String, Object>> doSearch(Map<String, String> paramMap, String currentSpaceKey)
			throws InvalidSearchException {
		// query
		SearchQuery labelQ = getLabelQuery(paramMap);
		SearchQuery spaceQ = getSpaceQuery(paramMap, currentSpaceKey);
		SearchQuery pageQ = new ContentTypeQuery(ContentTypeEnum.PAGE);
		SearchQuery datetimeQ = getDateTimeQuery(paramMap);
		SearchQuery authorQ = getAuthorQuery(paramMap);
		SearchQuery query = BooleanQuery.andQuery(labelQ, spaceQ, pageQ, datetimeQ, authorQ);
		// filter
		SearchFilter searchFilter = getFilter();
		// sort
		SearchSort searchSort = getSort(paramMap);
		// search
		String keywords = paramMap.get("keywords");
		int length = max_results;
		if (StringUtils.isBlank(keywords)) {
			keywords = null;
			length = max_results_with_keywords;
		} else {
			keywords = StringUtils.trim(keywords);
		}
		ISearch search = new ContentSearch(query, searchSort, searchFilter, 0, length);
		// dosearch
		SearchResults searchResults = searchManager.search(search);
		// toentity
		List<Searchable> contents = searchManager.convertToEntities(searchResults, EntityVersionPolicy.LATEST_VERSION);
		// topage
		return filterPages(contents, keywords);
	}
	
	/**
	 * 需要权限
	 * @return
	 */
	private SearchFilter getFilter() {
		return ContentPermissionsSearchFilter.getInstance().and(SpacePermissionsSearchFilter.getInstance());
	}

	/**
	 * 得到label的query条件
	 */
	private SearchQuery getLabelQuery(Map<String, String> paramMap) {
		String lnstr = paramMap.get("labelNames");
		String[] strs = StringUtils.split(lnstr, ",; ");
		List<LabelQuery> qs = new ArrayList<>();
		for (int i = 0; i < strs.length; i++) {
			if (StringUtils.isBlank(strs[i])) {
				continue;
			}
			qs.add(new LabelQuery(strs[i]));
		}
		if (qs.size() == 0) {
			throw new IllegalArgumentException(getText("error.novalidlabels"));
		}
		if (qs.size() > max_labels) {
			throw new IllegalArgumentException(getText("error.toomanylabels", max_labels));
		}
		return BooleanQuery.orQuery(qs.toArray(new LabelQuery[qs.size()]));
	}
	
	/**
	 * 空间查询条件
	 * @param paramMap
	 * @param currentSpaceKey
	 * @return
	 */
	private SearchQuery getSpaceQuery(Map<String, String> paramMap, String currentSpaceKey) {
		String spaceKey = paramMap.get("space");
		if (StringUtils.isBlank(spaceKey)) {
			return new InSpaceQuery(currentSpaceKey);
		}
		if (spaceKey.equals(currentSpaceKey)) {
			paramMap.remove("space");
		}
		return new InSpaceQuery(spaceKey);
	}
	
	/**
	 * 获取作者过滤条件
	 * @param paramMap
	 * @return
	 */
	private SearchQuery getAuthorQuery(Map<String, String> paramMap) {
		String authors = paramMap.get("authors");
		if (StringUtils.isBlank(authors)) {
			return AllQuery.getInstance();
		}
		String[] strs = StringUtils.split(authors, ", ;");
		List<SearchQuery> queries = new ArrayList<>();
		for (String s : strs) {
			if (StringUtils.isNotBlank(s)) {
				queries.add(new CreatorQuery(s.trim()));
			}
		}
		if (queries.size() == 0) {
			return AllQuery.getInstance();
		}
		if (queries.size() > max_authors) {
			throw new IllegalArgumentException(getText("error.toomanyauthors", max_authors));
		}
		return BooleanQuery.orQuery(queries.toArray(new SearchQuery[queries.size()]));
	}

	/**
	 * 从dateframe参数解析最近x小时，天、周、月，然后转化为开始时间，具体格式为不超过三位整数加上单位后缀{h,d,w,m,y}，不区分大小写
	 * @param dateframe
	 * @param paramMap
	 * @return
	 */
	private Date getFromDate(String dateframe, Map<String, String> paramMap) {
		Matcher matcher = recentPattern.matcher(dateframe);
		if (matcher.matches()) {
			int n = Integer.parseInt(matcher.group(1));
			char granu = Character.toUpperCase(matcher.group(2).charAt(0));
			Calendar cal = Calendar.getInstance();
			cal.add(Calendar.HOUR_OF_DAY, -n*recentUnitMap.get(granu));
			paramMap.put("dateframevm", getText("dateframevm.recent" + granu, n));
			return cal.getTime();
		} else {
			throw new IllegalArgumentException(getText("error.invalidDateFormat"));
		}
	}
	
	/**
	 * 从dateframe解析单独的日期或者日期范围 "20120222-", "-20120222", "20120222-20120322", "20120322 - 20120222"
	 * @param dateframe
	 * @param paramMap
	 * @return
	 */
	private String[] getDatesFromFrame(String dateframe, Map<String, String> paramMap) {
		Matcher matcher = dateframePattern.matcher(dateframe);
		String info = "";
		if (matcher.matches()) {
			String[] dates = new String[] {matcher.group(1), matcher.group(3)};
			if (null != dates[0] && null == dates[1]) {
				info = getText("dateframevm.after", dates[0]);
			} else if (null != dates[0] && null != dates[1]) {
				if (dates[0].equals(dates[1])) {
					info = getText("dateframevm.currentDay", dates[1]);
				} else {
					info = getText("dateframevm.range", dates[0], dates[1]);
				}
			} else if (null == dates[0] && null != dates[1]) {
				info = getText("dateframevm.before", dates[1]);
			} else {
				throw new IllegalArgumentException(getText("error.invalidDateFormat"));
			}
			if (!StringUtils.isEmpty(info)) {
				paramMap.put("dateframevm", info);
			}
			return dates;
		} else {
				throw new IllegalArgumentException(getText("error.invalidDateFormat"));
		}	
	}
	/**
	 * yyyy-MM-dd或者yyyy-MM-dd HH:mm:ss
	 * @param paramMap
	 * @return
	 */
	private SearchQuery getDateTimeQuery(Map<String, String> paramMap) {
		String dateframe = paramMap.get("dateframe");
		String fromStr = null;
		String toStr = null;
		Date from = null;
		Date to = null;
		if (!StringUtils.isEmpty(dateframe)) {
			dateframe = dateframe.trim();
			if (dateframe.contains(dateframeSeparator) || yMd.length() < dateframe.length()) {
				String[] dates = getDatesFromFrame(dateframe, paramMap);
				fromStr = dates[0];
				toStr = dates[1];
			} else if (yMd.length() == dateframe.length()) {
				fromStr = toStr = dateframe;
				paramMap.put("dateframevm", getText("dateframevm.currentDay", dateframe));
			} else {
				from = getFromDate(dateframe, paramMap);
			}
		}
		if (!StringUtils.isEmpty(fromStr)) {
			from = parseDate(fromStr.trim(), false);
		}
		if (!StringUtils.isEmpty(toStr)) {
			to = parseDate(toStr.trim(), true);
		}
		if (from != null && to != null && !from.before(to)) {
			throw new IllegalArgumentException(getText("error.fromNotSmallToTime"));
		}
		return new DateRangeQuery(from, to, true, false, DateRangeQueryType.CREATED);
	}

	/**
	 * @param str
	 * @param isEndTime 有些时间需要扩展范围才行，比如开始时间是2013-12-12,则表示从2013-12-12 00:00:00开始，而结束时间是2013-12-12，则表示结束时间在2013-12-13
	 *        00:00:00之前
	 * @return
	 */
	private Date parseDate(String str, boolean isEndTime) {
		try {
			if (str.length() == yMd.length()) {
				Date dt = new SimpleDateFormat(yMd).parse(str);
				if (isEndTime) {
					dt = new Date(dt.getTime() + 86400000L);
				}
				return dt;
			} else {
				String txt = getText(isEndTime ? "error.toTimeInvalid" : "error.fromTimeInvalid");
				throw new IllegalArgumentException(txt);
			}
		} catch (Exception e) {
			String txt = getText(isEndTime ? "error.toTimeInvalid" : "error.fromTimeInvalid");
			throw new IllegalArgumentException(txt);
		}
	}
	
	/**
	 * 默认按照创建时间排序
	 * @param paramMap
	 * @return
	 */
	private SearchSort getSort(Map<String, String> paramMap) {
		String sort = paramMap.get("sort");
		Order o = "true".equalsIgnoreCase(paramMap.get("reverse")) ? Order.DESCENDING : Order.ASCENDING;
		if (TitleSort.KEY.equalsIgnoreCase(sort)) {
			return new TitleSort(o);
		} else if (CreatedSort.KEY.equalsIgnoreCase(sort)) {
			return new CreatedSort(o);
		} else if (ModifiedSort.KEY.equalsIgnoreCase(sort)) {
			return new ModifiedSort(o);
		}
		return new CreatedSort(o);
	}
	
	/**
	 * 获取页面内容
	 * @param contents
	 * @return
	 */
	private List<Map<String, Object>> filterPages(List<Searchable> contents, String keywords) {
		List<Map<String, Object>> pages = new ArrayList<>();
		int cnt = 0;
		for (Searchable ceo : contents) {
			if (ceo instanceof Page) {
				Page p = (Page) ceo;
				if (null == keywords || p.getDisplayTitle().contains(keywords)) {
					Map<String, Object> m = new HashMap<>();
					m.put("page", p);
					m.put("pageHtml", viewRenderer.render(p));
					pages.add(m);
					++cnt;
				}
			}
			if (cnt >= max_results)
				break;
		}
		return pages;
	}
	
	@Override
	public BodyType getBodyType() {
		return BodyType.NONE;
	}
	
	@Override
	public OutputType getOutputType() {
		return OutputType.BLOCK;
	}
}
