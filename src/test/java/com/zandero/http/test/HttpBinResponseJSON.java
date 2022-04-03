package com.zandero.http.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;

import java.util.*;

/**
 * BinResponse JSON for testing purposes only
 */
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpBinResponseJSON {

	public String data;

	public Map<String, String> args;

	public Map<String, Set<String>> headers;

	public Map<String, Set<String>> form;
}

