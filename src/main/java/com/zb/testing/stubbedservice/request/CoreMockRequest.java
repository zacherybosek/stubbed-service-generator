package com.zb.testing.stubbedservice.request;

import com.zb.testing.stubbedservice.enums.FilterType;
import com.zb.testing.stubbedservice.enums.VerificationMode;
import com.zb.testing.stubbedservice.StubbedService;
import com.zb.testing.stubbedservice.exceptions.NumberOfCallsAssertionException;
import com.zb.testing.stubbedservice.request.info.InvokeInformation;
import com.zb.testing.stubbedservice.request.info.SizeStoredSizeLimitedLinkedList;
import com.zb.testing.stubbedservice.request.util.HttpMapUtil;
import com.zb.testing.stubbedservice.response.Response;
import com.zb.testing.stubbedservice.view.StringView;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.util.AntPathMatcher;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.method.HandlerMethod;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.Deque;
import java.util.LinkedList;

/**
 * Created by Zachery on 7/24/2016.
 */
public class CoreMockRequest implements MockRequest {

    private String url;
    private MediaType requestContentType;
    private HttpMethod requestMethod;
    private VerificationMode verifyMode;
    private FilterType parameterType;
    private FilterType headerType;
    private Deque<Response> responses;

    private long sleepTime = 0;
    private boolean isRegExEnabled = false;
    private boolean requestLogSizeSet = false;

    private SizeStoredSizeLimitedLinkedList<InvokeInformation> invokeInformations = new SizeStoredSizeLimitedLinkedList<>(StubbedService.DEFAULT_REQUEST_LOG_SIZE);

    //TODO do I need this ?
    private MultiValueMap<String, String> parameters;
    private MultiValueMap<String, String> headers;
    private boolean withMethodCalled = false;

    //TODO learn what the hell an AntPathMatcher is
    private static final AntPathMatcher pathGlobMatcher = new AntPathMatcher();
    private Response defaultResponse = null;

    public CoreMockRequest(String url) {
        this.url = url;
        this.requestContentType = MediaType.ALL;
        this.requestMethod = HttpMethod.GET;
        this.parameterType = FilterType.ANY;
        this.headerType = FilterType.ANY;
        this.responses = new LinkedList<Response>();
    }

    @Override
    public MockRequest giveResponse(String content) {
        return giveResponse(content, MediaType.ALL, HttpStatus.OK);
    }

    @Override
    public MockRequest giveResponse(String content, MediaType contentType) {
        return giveResponse(content, contentType, HttpStatus.OK);
    }

    @Override
    public MockRequest giveResponse(String content, HttpStatus responseCode) {
        return giveResponse(content, MediaType.ALL, responseCode);
    }

    @Override
    public MockRequest giveResponse(String content, MediaType contentType, HttpStatus responseCode) {
        Response response = Response.getResponse(content, contentType, responseCode);
        defaultResponse = response;
        responses.add(response);
        return this;
    }

    @Override
    public MockRequest withContentType(MediaType contentType) {
        this.requestContentType = contentType;
        return this;
    }

    @Override
    public MockRequest withMethod(HttpMethod method) {
        this.requestMethod = method;
        this.withMethodCalled = true;
        return this;
    }

    @Override
    public MockRequest withSleep(long sleepTime) {
        this.sleepTime = sleepTime;
        return this;
    }

    @Override
    public MockRequest withRegEx() {
        this.isRegExEnabled = true;
        return this;
    }

    @Override
    public MockRequest withRequestLogSize(int requestLogSize) {
        this.requestLogSizeSet = true;
        invokeInformations.setListSizeLimit(requestLogSize);
        return this;
    }

    protected long getSleepTime() {
        return sleepTime;
    }

    @Override
    public ModelAndView invoke(HttpServletRequest request, HttpServletResponse response, HandlerMethod handlerMethod) {
        if(getSleepTime() > 0) {
            try {
                Thread.sleep(getSleepTime());
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        InvokeInformation invokeInfo = new InvokeInformation(request, response);
        invokeInformations.add(invokeInfo);
        Response mockResponse = responses.poll();
        if(mockResponse == null) {
            mockResponse = defaultResponse;
        }
        response.setStatus(mockResponse.getResponseCode().value());
        response.setContentType(mockResponse.getResponseContentType().toString());
        StringView st = new StringView(mockResponse.getResponseContent(), mockResponse.getResponseContentType(), mockResponse.getResponseCode());
        ModelAndView mav = new ModelAndView(st);
        return mav;
    }

    public String getUrl() {
        return url;
    }

    /**
     * This function will do nothing once withRequestLogSize has been called
     * @param requestLogSize requested size of log
     */
    public void setRequestLogSize(int requestLogSize) {
        if(!requestLogSizeSet) {
            invokeInformations.setListSizeLimit(requestLogSize);
        }
    }

    private boolean urlMatches(HttpServletRequest request) {
        //TODO i think this if is backwards
        if(isRegExEnabled) {
            return request.getRequestURI().matches(url);
        } else {
            //TODO the problem with the if above is caught here
            if(pathGlobMatcher.isPattern(url)) {
                return pathGlobMatcher.match(url, request.getRequestURI());
            } else {
                return request.getRequestURI().equalsIgnoreCase(url);
            }
        }
    }

    @Override
    public boolean matches(HttpServletRequest request, HandlerMethod handlerMethod) {
        if(urlMatches(request)
                && doesHttpMethodMatch(request)
                && doesRequestContentTypeMatch(request)
                && doesParameterMapMatch(request)
                && doHeadersMatch(request)) {
            return true;
        } else {
            return false;
        }
    }

    private boolean doesHttpMethodMatch(HttpServletRequest request) {
        if(withMethodCalled) {
            return request.getMethod().equals(requestMethod.toString());
        }
        return true;
    }


    private boolean doesParameterMapMatch(HttpServletRequest request) {
        if(parameterType.getValue().equals(FilterType.ANY.getValue())) {
            return true;
        } else if( parameterType.getValue().equals(FilterType.NONE.getValue())) {
            //TODO not consistent with the headers equilalent. FIgure out who is right.
            return request.getParameterMap().isEmpty();
        } else if (parameterType.getValue().equals(FilterType.CUSTOM.getValue())){
            return HttpMapUtil.match(parameters, HttpMapUtil.convertToMap(request, HttpMapUtil.HttpRequestType.URL_PARAMETERS));
        }
        return false;
    }


    private boolean doHeadersMatch(HttpServletRequest request) {
        if(headerType.getValue().equals(FilterType.ANY.getValue())) {
            return true;
        }
        else if (headerType.getValue().equals(FilterType.NONE.getValue())) {
            return headers.isEmpty();
        } else if (headerType.getValue().equals(FilterType.CUSTOM.getValue())) {
            return HttpMapUtil.match(headers, HttpMapUtil.convertToMap(request, HttpMapUtil.HttpRequestType.HTTP_HEADERS));
        }
        return false;
    }

    private boolean doesRequestContentTypeMatch(HttpServletRequest request) {
        if(requestContentType != null) {
            if(requestContentType.equals(MediaType.ALL)) {
                return true;
            }
            if(requestContentType.toString().equals(request.getContentType())) {
                return true;
            }
            return false;
        }
        return true;
    }

    @Override
    public SizeStoredSizeLimitedLinkedList<InvokeInformation> getInvokeInformation() {
        return invokeInformations;
    }

    //TODO I dont think I need this
    public HttpMethod getRequestMethod() {
        return requestMethod;
    }

    @Override
    public MockRequest verify(VerificationMode verifyMode) {
        this.verifyMode = verifyMode;
        return this;
    }

    @Override
    public MockRequest withParams(FilterType parameterType) {
        if(parameterType.toString() != null && parameterType.getValue().equals(FilterType.ANY.getValue())) {
            return withParams(parameterType, null);
        }
        return withParams(FilterType.NONE, null);
    }

    @Override
    public MockRequest withParams(MultiValueMap<String, String> parameters) {
        if(parameters.isEmpty()) {
            return withParams(FilterType.NONE, null);
        }
        return withParams(FilterType.CUSTOM, parameters);
    }

    public MockRequest withParams(FilterType parameterType, MultiValueMap<String, String> parameters) {
        this.parameters = parameters;
        this.parameterType = parameterType;
        return this;
    }

    @Override
    public MockRequest withHeaders(MultiValueMap<String, String> headers) {
        if(headers.isEmpty()) {
            return withHeaders(FilterType.NONE, headers);
        }
        return withHeaders(FilterType.CUSTOM, headers);
    }

    @Override
    public MockRequest withHeaders(FilterType headerType) {
        if(headerType.toString() != null && headerType.getValue().equals(FilterType.ANY.getValue())) {
            return withHeaders(headerType, null);
        }
        return withHeaders(FilterType.NONE, new LinkedMultiValueMap<String, String>());
    }

    public MockRequest withHeaders(FilterType headerType, MultiValueMap<String, String> headers) {
        this.headerType = headerType;
        this.headers = headers;
        return this;
    }

    //TODO I dont think I need this
    public VerificationMode getVerifyMode() {
        return verifyMode;
    }

    public void verifyRequest() {
        VerificationMode vm = verifyMode;
        if(vm!=null) {
            long totalCalls = getInvokeInformation().size();
            if(totalCalls < vm.getMinimumCalls()) {
                throw new NumberOfCallsAssertionException(getUrl()+"was called less than asserted number of times. Actual: " +totalCalls+". Expected Minimum:"+vm.getMinimumCalls()+". Expected Maximum: " + vm.getMaximumCalls());
            } else if ( totalCalls > vm.getMaximumCalls()) {
                throw new NumberOfCallsAssertionException(getUrl()+"was called more than asserted number of times. Actual: " +totalCalls+". Expected Minimum:"+vm.getMinimumCalls()+". Expected Maximum: " + vm.getMaximumCalls());
            }
        }
    }
}
