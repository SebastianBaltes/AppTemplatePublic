package models.logevents;

import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;

import org.apache.commons.lang.builder.ReflectionToStringBuilder;

import play.db.ebean.Model;

@SuppressWarnings("serial")
@Entity
@Table(name = "log_httpRequest")
public class LogHttpRequest extends Model {

	@Id
	private Long id;
	private String method;
	private String url;
	private String host;
	private Integer responseCode;
	private String userAgent;
	private String referer;
	private Long startTime;
	private Long endTime;
	private String fromIP;

	public LogHttpRequest() {
	}

	public LogHttpRequest(String _method, String _url, String _host, Integer _responseCode, String _userAgent,
			String _referer, Long _startTime, Long _endTime, String _fromIP) {
		method = _method;
		url = _url;
		host = _host;
		responseCode = _responseCode;
		userAgent = _userAgent;
		referer = _referer;
		startTime = _startTime;
		endTime = _endTime;
		fromIP = _fromIP;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public String getMethod() {
		return method;
	}

	public void setMethod(String _method) {
		method = _method;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String _url) {
		url = _url;
	}

	public String getHost() {
		return host;
	}

	public void setHost(String _host) {
		host = _host;
	}

	public Integer getResponseCode() {
		return responseCode;
	}

	public void setResponseCode(Integer _responseCode) {
		responseCode = _responseCode;
	}

	public String getUserAgent() {
		return userAgent;
	}

	public void setUserAgent(String _userAgent) {
		userAgent = _userAgent;
	}

	public String getReferer() {
		return referer;
	}

	public void setReferer(String _referer) {
		referer = _referer;
	}

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long _startTime) {
		startTime = _startTime;
	}

	public Long getEndTime() {
		return endTime;
	}

	public void setEndTime(Long _endTime) {
		endTime = _endTime;
	}

	public String getFromIP() {
		return fromIP;
	}

	public void setFromIP(String _fromIP) {
		fromIP = _fromIP;
	}

	@Override
	public String toString() {
		return ReflectionToStringBuilder.toString(this);
	}

}
