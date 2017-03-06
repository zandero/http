package com.zandero.http.test;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonInclude;
import org.junit.Ignore;

/**
 * BinResponse JSON for testing purposes only
 */
@Ignore
@JsonIgnoreProperties(ignoreUnknown = true)
@JsonInclude(JsonInclude.Include.NON_NULL)
public class HttpBinResponseJSON {

	public HttpBinResponseArguments args;

	public String data;

	public HttpBinResponseHeader headers;

	public HttpBinResponseForm form;

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class HttpBinResponseHeader {

		public String Bla;
	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class HttpBinResponseArguments {

	}

	@JsonIgnoreProperties(ignoreUnknown = true)
	@JsonInclude(JsonInclude.Include.NON_NULL)
	public class HttpBinResponseForm {

		public String Hello;
	}
}
