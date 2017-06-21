package com.forthe.xhttp.http;

import okhttp3.OkHttpClient;

public final class HttpConfig {
    final boolean isNeedDebug;
    final OkHttpClient client;
    final HttpLogger httpLogger;
    final ParamSupplier paramSupplier;
    final HttpCache cache;
    final HttpHost httpHost;
    final ErrorHandler errorHandler;
    private HttpConfig(final Builder builder){
        client = builder.client;
        paramSupplier = builder.paramSupplier;
        httpLogger = builder.httpLogger;
        cache = builder.cache;
        isNeedDebug = builder.isNeedDebug;
        httpHost = builder.httpHost;
        errorHandler = builder.errorHandler;
    }

    public static class Builder{
        private OkHttpClient client = null;
        private HttpLogger httpLogger = null;
        private ParamSupplier paramSupplier = null;
        private HttpCache cache = null;
        private boolean isNeedDebug = false;
        private HttpHost httpHost;
        private ErrorHandler errorHandler;
        public Builder() {
        }

        public Builder host(HttpHost httpHost){
            this.httpHost = httpHost;
            return  this;
        }


        public Builder client(OkHttpClient client) {
            this.client = client;
            return this;
        }

        
        public Builder paramSupplier(ParamSupplier paramSupplier) {
            this.paramSupplier = paramSupplier;
            return this;
        }

        public Builder httpLogger(HttpLogger httpLogger) {
            this.httpLogger = httpLogger;
            return this;
        }
        
        public Builder cache(HttpCache cache) {
            this.cache = cache;
            return this;
        }

        public Builder errorHandler(ErrorHandler errorHandler) {
            this.errorHandler = errorHandler;
            return this;
        }

        private void checkDefaults(){
            if(null == client){
                client = createDefaultClient();
            }
        }


        public Builder setNeedDebug(boolean isNeedDebug) {
            this.isNeedDebug = isNeedDebug;
            return this;
        }

        public HttpConfig builder(){
            checkDefaults();
            return new HttpConfig(this);
        }
        
        private OkHttpClient createDefaultClient(){
            return new OkHttpClient();
        }
    }

    public interface HttpHost{
        String getHost();
    }
}
