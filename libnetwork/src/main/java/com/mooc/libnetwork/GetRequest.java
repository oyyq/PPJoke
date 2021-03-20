package com.mooc.libnetwork;

public class GetRequest<T> extends Request<T, GetRequest> {
    public GetRequest(String url) {
        super(url);
    }

    @Override
    protected okhttp3.Request generateRequest(okhttp3.Request.Builder builder) {
        String url = UrlCreator.createUrlFromParams(mUrl, params);
        //builder.get().url(url) => 指明get方法并且url是***
        okhttp3.Request request = builder.get().url(url).build();
        return request;
    }
}
