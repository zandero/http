package com.zandero.http.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.Ignore;

import java.util.Map;

/**
 * BinResponse JSON for testing purposes only
 */
@Ignore
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpBinResponseJSON {

	public String data;

	public Map<String, String> args;

	public Map<String, String> headers;

	public Map<String, String> form;
}
